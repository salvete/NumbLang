package evaluator;

import java.beans.beancontext.BeanContext;
import java.security.AuthProvider;
import java.util.LinkedList;
import java.util.Objects;
import java.util.jar.Attributes.Name;

import javax.lang.model.util.ElementScanner6;
import javax.swing.text.PlainView;
import javax.swing.text.StyleContext.SmallAttributeSet;

import ast.BlockStatement;
import ast.CallStatement;
import ast.ExpressionStatement;
import ast.FunctionStatement;
import ast.IdentifierStatement;
import ast.IfStatement;
import ast.LetStatement;
import ast.ReturnStatement;
import ast.interfaces.Statement;
import evaluator.enviroment.Enviroment;
import evaluator.object.BooleanInternal;
import evaluator.object.FloatInternal;
import evaluator.object.FunctionInternal;
import evaluator.object.IntegerInternal;
import evaluator.object.NullInternel;
import evaluator.object.ObjectTypes;
import evaluator.object.ReturnInternal;
import evaluator.object.interfaces.NumberOrBoolean;
import evaluator.object.interfaces.ObjectInternal;
import token.Token;
import token.TokenType;

public class Eval {
    public static ObjectInternal Eval(Statement stmt, Enviroment env)
    {

        if (stmt instanceof BlockStatement)
        {
            return evalBlockStatement((BlockStatement)stmt, env);
        }
        else if (stmt instanceof CallStatement)
        {
            return evalCallStatement((CallStatement)stmt,env);
        }
        else if (stmt instanceof ExpressionStatement)
        {
            return evalExpressionStatement((ExpressionStatement)stmt, env);
        }
        else if (stmt instanceof FunctionStatement)
        {
            return evalFunctionStatement((FunctionStatement)stmt,env);
        }
        else if (stmt instanceof IdentifierStatement)
        {
            return evalIdentifierStatement((IdentifierStatement)stmt,env);
        }
        else if (stmt instanceof IfStatement)
        {
            return evalIfStatement((IfStatement)stmt,env);
        }
        else if (stmt instanceof LetStatement)
        {
            return evalLetStatement((LetStatement)stmt, env);
        }
        else if (stmt instanceof ReturnStatement)
        {
            return evalReturnStatement((ReturnStatement)stmt,env);
        }
        else
            return new NullInternel();
        
    }

    private static ObjectInternal evalBlockStatement(BlockStatement stmt, Enviroment env)
    {
        ObjectInternal res = null;

        for (Statement statement : stmt.statements) {
            res = Eval(statement, env);

            if (res instanceof ReturnInternal)
                return res;
        }
        return res;
    }

    private static ObjectInternal evalCallStatement(CallStatement stmt, Enviroment env)
    {
        LinkedList<ObjectInternal> paras = getParameters(stmt.arguments, env);

        if (paras.size() != stmt.arguments.size())
        {
            error("Prameters are wrong!");
            return new NullInternel();
        }

        ObjectInternal tmp = Eval(stmt.function, env);

        if (!(tmp instanceof FunctionInternal))
        {
            StringBuilder sb = new StringBuilder();
            sb.append("function:\n");
            sb.append(stmt.function.getString("    "));
            sb.append("does not exit!\n");
            error(sb.toString());
            return new NullInternel();
        }
        
        FunctionInternal fun = (FunctionInternal)tmp;

        Enviroment local = Enviroment.getNewEnviroment(fun.env);

        if (paras.size() != fun.parameters.size())
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Parameters' size don't match:\n");
            sb.append(paras.size() + "\n");
            sb.append(fun.parameters.size() + "\n");
            error(sb.toString());
        }

        for (int i = 0; i < fun.parameters.size(); i ++)
        {
            if (!(fun.parameters.get(i) instanceof IdentifierStatement))
            {
                error("function parameter's type is not identifier!");
                return new NullInternel();
            }

            String name = ((IdentifierStatement)fun.parameters.get(i)).name.value;

            local.Set(name, paras.get(i));
        }

        return Eval(fun.body, local);
    }

    private static LinkedList<ObjectInternal> getParameters(LinkedList<Statement> parameters, Enviroment env)
    {
        LinkedList<ObjectInternal> res = new LinkedList<>();

        for (Statement s: parameters)
        {
            ObjectInternal now = Eval(s, env);

            if (now.Type() == ObjectTypes.NULL_OBJ)
                break;

            res.add(now);
        }
        return res;
    }


    private static ObjectInternal evalExpressionStatement(ExpressionStatement stmt, Enviroment env)
    {
        ObjectInternal obj = null;

       if (stmt.isLeaf)
       {

           switch(stmt.opr.type)
           {
               case TokenType.NUMBER: obj = new IntegerInternal(Integer.valueOf(stmt.opr.value));break;
               case TokenType.FLOAT: obj = new FloatInternal(Float.valueOf(stmt.opr.value));break;
               case TokenType.TRUE:
               case TokenType.FALSE: obj = new BooleanInternal(Boolean.valueOf(stmt.opr.value));break;
               default : obj = new NullInternel();
           }
           return obj;
       }
       else
       {
            if (stmt.opr.type.equals(TokenType.OPPOSITE))
            {
                 obj = Eval(stmt.left, env);

                if (!(obj instanceof NumberOrBoolean))
                {
                    error("Opposite should apply to number or boolean!");
                    obj = null;
                    return new NullInternel();
                }
                else
                {
                    ((NumberOrBoolean)obj).opposite();
                }
            }
            else
            {
                ObjectInternal left = Eval(stmt.left, env);
                ObjectInternal right = Eval(stmt.right, env);
                obj = oprLeftRight(left,right,stmt.opr.type);
            }
       }

       return obj;
    }

    private static ObjectInternal oprLeftRight(ObjectInternal left, ObjectInternal right, String opr)
    {
        ObjectInternal res = null;

        if (!Objects.equals(left.Type(), right.Type()))
        {
            error("Type error: " + left.Type() + " not equals to " + right.Type());
            res = new NullInternel();
        }
        else
        {
            if (Objects.equals(left.Type(), ObjectTypes.BOOLEAN_OBJ))
                res = revalBoolean(left,right,opr);
            else if (Objects.equals(left.Type(), ObjectTypes.INTEGER_OBJ))
                res = revalInteger(left,right,opr);
            else if ((Objects.equals(left.Type(), ObjectTypes.FLOAT_OBJ)))
                res = revalFloat(left,right,opr);
            else
                res = new NullInternel();
        }

        return res;
    }

    private static ObjectInternal revalBoolean(ObjectInternal left, ObjectInternal right, String opr)
    {
        ObjectInternal res = null;

        boolean lv = Boolean.valueOf(left.Inspect());
        boolean rv = Boolean.valueOf(right.Inspect());

        switch(opr)
        {
            case TokenType.AND: res = new BooleanInternal(lv && rv);break;
            case TokenType.OR: res = new BooleanInternal(lv || rv);break;
            case TokenType.EQU: res = new BooleanInternal(lv == rv);break;
            case TokenType.NEQU: res = new BooleanInternal(lv != rv);break;
            default: 
                error("Unknown: " +  lv + " " + opr + " " + rv);
                res = new NullInternel();
        }

        return res;
    }

    private static ObjectInternal revalInteger(ObjectInternal left, ObjectInternal right, String opr)
    {
        ObjectInternal res = null;

        int lv = Integer.valueOf(left.Inspect());
        int rv = Integer.valueOf(right.Inspect());

        switch(opr)
        {
            case TokenType.ADD: res = new IntegerInternal(lv + rv);break;
            case TokenType.SUB: res = new IntegerInternal(lv - rv);break;
            case TokenType.MUL: res = new IntegerInternal(lv * rv);break;
            case TokenType.DIV: res = new IntegerInternal(lv / rv);break;
            case TokenType.EQU: res = new BooleanInternal(lv == rv);break;
            case TokenType.NEQU: res = new BooleanInternal(lv != rv);break;
            default:
                error("Unknown: " +  lv + " " + opr + " " + rv);
                res = new NullInternel();
        }

        return res;
    }


    private static ObjectInternal revalFloat(ObjectInternal left, ObjectInternal right, String opr)
    {
        ObjectInternal res = null;

        float lv = Float.valueOf(left.Inspect());
        float rv = Float.valueOf(right.Inspect());

        switch(opr)
        {
            case TokenType.ADD: res = new FloatInternal(lv + rv);break;
            case TokenType.SUB: res = new FloatInternal(lv - rv);break;
            case TokenType.MUL: res = new FloatInternal(lv * rv);break;
            case TokenType.DIV: res = new FloatInternal(lv / rv);break;
            case TokenType.EQU: res = new BooleanInternal(lv == rv);break;
            case TokenType.NEQU: res = new BooleanInternal(lv != rv);break;
            default:
                error("Unknown: " +  lv + " " + opr + " " + rv);
                res = new NullInternel();
        }

        return res;
    }


    private static ObjectInternal evalFunctionStatement(FunctionStatement stmt, Enviroment env)
    {
        FunctionInternal res = new FunctionInternal(env);

        res.parameters = stmt.parameters;
        res.body = stmt.body;

        return res;
    }


    private static ObjectInternal evalIdentifierStatement(IdentifierStatement stmt, Enviroment env)
    {
        ObjectInternal res = null;

        res = env.Get(stmt.name.value);

        if (res instanceof NullInternel)
            error("Error: " + stmt.name.value + " doesn't exit!");

        return res;
    }

    
    private static ObjectInternal evalIfStatement(IfStatement stmt, Enviroment env)
    {
        boolean result = isTrue(stmt,env);
        ObjectInternal res = null;

        if (result)
        {
            res = evalBlockStatement(stmt.ifCondition, env);
        }
        else if (stmt.elseCondition != null)
        {
            res = evalBlockStatement(stmt.elseCondition, env);
        }
        else
            res = new NullInternel();

        return res;

    }

    private static boolean isTrue(IfStatement stmt, Enviroment env)
    {
        ObjectInternal condition = Eval(stmt.condition,env);

        if (condition == null)
            return false;
        else if (condition instanceof BooleanInternal && Boolean.valueOf(((BooleanInternal)condition).Inspect()) == false)
        {
            return false;
        }
        else
            return true;
    }


    private static ObjectInternal evalLetStatement(LetStatement stmt, Enviroment env)
    {
        ObjectInternal value = Eval(stmt.value, env);
        env.Set(stmt.name.value, value);
        return env.Get(stmt.name.value);
    }

    private static ObjectInternal evalReturnStatement(ReturnStatement stmt, Enviroment env)
    {
        ObjectInternal res = null;

        res = Eval(stmt.returnValue, env);

        return res;
    }

    private static void error(String msg)
    {
        System.err.println(msg);
    }
    
}
