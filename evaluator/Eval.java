package evaluator;

import java.beans.beancontext.BeanContext;
import java.security.AuthProvider;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.Attributes.Name;

import javax.lang.model.util.ElementScanner6;
import javax.swing.text.PlainView;
import javax.swing.text.StyleContext.SmallAttributeSet;

import ast.*;
import ast.interfaces.Statement;
import evaluator.enviroment.Enviroment;
import evaluator.object.*;
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
        else if (stmt instanceof ListStatement)
        {
            return evalListStatement((ListStatement)stmt,env);
        }
        else if (stmt instanceof  GetElemetStatement)
        {
            return evalGetElementStatement((GetElemetStatement)stmt,env);
        }
        else if (stmt instanceof StringStatement)
        {
            return evalStringStatement((StringStatement)stmt,env);
        }
        else if (stmt instanceof DictStatement)
        {
            return evalDictStatement((DictStatement)stmt, env);
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

    
    private static ObjectInternal curry(FunctionInternal retFun, LinkedList<LinkedList<Statement>> args, Enviroment env)
    {

        LinkedList<ObjectInternal>paras = getParameters(args.get(0), env);

        if (paras.size() != args.get(0).size())
        {
            error("Prameters are wrong!");
            return new NullInternel();
        }

        LinkedList<Statement> saved = new LinkedList<>(args.get(0));

        args.removeFirst();



        Enviroment local = Enviroment.getNewEnviroment(env);

        if (paras.size() != retFun.parameters.size())
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Parameters' size don't match:\n");
            sb.append(paras.size() + "\n");
            sb.append(retFun.parameters.size() + "\n");
            error(sb.toString());
        }

        for (int i = 0; i < retFun.parameters.size(); i ++)
        {
            if (!(retFun.parameters.get(i) instanceof IdentifierStatement))
            {
                error("function parameter's type is not identifier!");
                return new NullInternel();
            }

            String name = ((IdentifierStatement)retFun.parameters.get(i)).name.value;

            local.Set(name, paras.get(i));
        }

        ObjectInternal res = Eval(retFun.body, local);

        if(args.size() == 0)
        {
            args.add(0,saved);
            return res;
        }
        else
        {
            if (!(res instanceof FunctionInternal))
            {
                error("multicall function!");
                return new NullInternel();
            }

            ObjectInternal ans = curry((FunctionInternal)res, args, local);
            args.add(0,saved);
            return ans;
        }
    }


    private static ObjectInternal evalCallStatement(CallStatement stmt, Enviroment env)
    {


        LinkedList<ObjectInternal> paras = getParameters(stmt.arguments.get(0), env);

            if (paras.size() != stmt.arguments.get(0).size())
            {
                error("Prameters are wrong!");
                return new NullInternel();
            }

            LinkedList<Statement> saved = new LinkedList<>(stmt.arguments.get(0));

            stmt.arguments.removeFirst();

          
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

            ObjectInternal res = Eval(fun.body, local);

            if (stmt.arguments.size() == 0)
            {
                stmt.arguments.add(0,saved);
                return res;
            }
            else
            {
                if (!(res instanceof FunctionInternal))
                {
                    error("multicall function!");
                    return new NullInternel();
                }

                ObjectInternal ans =  curry((FunctionInternal)res, stmt.arguments, local);
                stmt.arguments.add(0, saved);
                return ans;
            }

    }

    private static LinkedList<ObjectInternal> getParameters(LinkedList<Statement> parameters, Enviroment env)
    {
        LinkedList<ObjectInternal> res = new LinkedList<>();

       
        LinkedList<ObjectInternal> tmp = new LinkedList<>();

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
            else if (Objects.equals(left.Type(),ObjectTypes.LIST_OBJ))
            {
                res = revalList((ListInternal)left,(ListInternal)right,opr);
            }
            else if (Objects.equals(left.Type(), ObjectTypes.STRING_OBJ))
            {
                res = revalString((StringInternal)left, (StringInternal)right, opr);
            }
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

    private static ObjectInternal revalList(ListInternal left, ListInternal right, String opr)
    {
        ObjectInternal res = null;

        if (Objects.equals(opr, TokenType.ADD))
        {
            for (ObjectInternal o : right.elements)
            {
                left.elements.add(o);
            }

            res = left;
        }
        else
        {
            error("[Error] Unknown operation on two lists.");
            res = new NullInternel();
        }

        return res;
    }


    private static ObjectInternal revalString(StringInternal left, StringInternal right, String opr)
    {
        ObjectInternal res = null;

        if (!Objects.equals(opr, TokenType.ADD))
        {
            error("[Error] Unknow operation on two Strings: " + opr);
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            sb.append(left.content);
            sb.append(right.content);
            res = new StringInternal(sb.toString());
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

    private static ObjectInternal evalListStatement(ListStatement stmt, Enviroment env)
    {
        ListInternal res = new ListInternal();

        for ( Statement s : stmt.store)
        {
            res.elements.add(Eval(s,env));
        }

        return res;
    }

    private static ObjectInternal evalGetElementStatement(GetElemetStatement stmt, Enviroment env)
    {
        ObjectInternal res = new NullInternel();

        if (!(stmt.listName instanceof  IdentifierStatement))
        {
            error("[Error] List name  is not correct.");
        }

        IdentifierStatement listName = (IdentifierStatement) stmt.listName;

        if (!(env.Get(listName.name.value) instanceof  ListInternal) && !(env.Get(listName.name.value) instanceof DictInternal))
        {
            error("[Error] " + listName.name.value + " is not a list or a dict.");
            return res;
        }

        if (env.Get(listName.name.value) instanceof ListInternal)
        {
            ListInternal list = (ListInternal) env.Get(listName.name.value);

            if (!(stmt.listIndex instanceof ExpressionStatement))
            {
                error("[Error] " + "list index is not an expression.");
                return  res;
            }

            ObjectInternal index = Eval((ExpressionStatement)stmt.listIndex,env);

            if (!(index instanceof  IntegerInternal))
            {
                error("[Error] list index is not an integer.");
                return res;
            }

            int pos = ((IntegerInternal)index).Value;

            if (pos < 0 || pos >= list.elements.size())
            {
                error("[Error] index " + pos + "  is out of range.");
                return res;
            }

            return list.elements.get(pos);    
        }
        else
        {

            DictInternal dict = (DictInternal) env.Get(listName.name.value);

            if (!(stmt.listIndex instanceof StringStatement))
            {
                error("[Error] " + "dict inded is not a strign.");
                return res;
            }

            StringInternal key = (StringInternal) Eval((StringStatement)stmt.listIndex, env);

            ObjectInternal tryGet = dict.dict.get(key.content);

            return tryGet == null ? res : tryGet;
        }
    }

    private static ObjectInternal evalStringStatement(StringStatement stmt, Enviroment env)
    {
        return new StringInternal(stmt.content);
    }

    private static ObjectInternal evalDictStatement(DictStatement stmt, Enviroment env)
    {
        DictInternal res = new DictInternal();

        for(Map.Entry<StringStatement, Statement> e : stmt.dict.entrySet())
        {
            StringInternal key = (StringInternal) Eval(e.getKey(), env);
            ObjectInternal value = Eval(e.getValue(),env);
            res.dict.put(key.content, value);
        }

        return res;
    }


    private static void error(String msg)
    {
        System.err.println(msg);
    }
}
