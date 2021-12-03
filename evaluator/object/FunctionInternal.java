package evaluator.object;

import java.util.LinkedList;

import ast.BlockStatement;
import ast.interfaces.Statement;
import evaluator.enviroment.Enviroment;
import evaluator.object.interfaces.ObjectInternal;

public class FunctionInternal implements ObjectInternal {
    public Enviroment env;
    public LinkedList<Statement> parameters;
    public BlockStatement body;

    public FunctionInternal(Enviroment e)
    {
        env = e;
    }


    @Override
    public String Type() {
        // TODO Auto-generated method stub
        return ObjectTypes.FUNCTION_OBJ;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        return "A function.";
    }


    @Override
    public String Inspect(String s) {
        // TODO Auto-generated method stub
        return s + Inspect();
    }
    
}
