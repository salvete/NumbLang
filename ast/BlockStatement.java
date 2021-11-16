package ast;

import java.util.LinkedList;

import ast.interfaces.Statement;

public class BlockStatement implements Statement{

    public LinkedList<Statement> statements;

    public BlockStatement(){
        statements = new LinkedList<>();
    }



    @Override
    public void statementNode() {
        // TODO Auto-generated method stub
        
    }


    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "BlockStatement";
    }


    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        sb.append(s + "BlockStatement\n");
        int len = statements.size();

        for (int i = 0; i < len; i ++)
        {
            sb.append(s + "    Statement" + (i+1) + "\n");
            sb.append(statements.get(i).getString(s + "        "));
        }
        return sb.toString();
    }
    
}
