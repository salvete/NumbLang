package ast;

import java.util.LinkedList;

import ast.interfaces.Statement;

public class CallStatement implements Statement {
    public Statement function;
    public LinkedList<Statement> arguments;
    public boolean posOrNeg = false;

    
    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        sb.append(s + "Function Invocation\n");
        sb.append(s + "    function:\n");
        sb.append(function.getString(s + "        "));
        
        int len = arguments.size();

        for (int i = 0; i < len; i ++)
        {
            sb.append(s + "        Argument" + (i+1) + "\n");
            sb.append(arguments.get(i).getString(s + "            "));
        }

        return sb.toString();
    }
    @Override
    public void statementNode() {
        // TODO Auto-generated method stub
        
    }
    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "CallStatement";
    }
    
    
}
