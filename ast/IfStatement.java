package ast;

import java.lang.management.BufferPoolMXBean;
import java.security.PublicKey;

import ast.interfaces.Statement;
import token.Token;

public class IfStatement implements Statement {

    public Token token;
    public Statement condiction;
    public BlockStatement ifCondiction;
    public BlockStatement elseCondition;


    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        sb.append(s + "IF Statement\n");
        sb.append(s + "    condition:\n");
        sb.append(condiction.getString(s + "        "));
        sb.append(s + "    IFBlock:\n");
        sb.append(ifCondiction.getString(s+"        "));
        sb.append(s + "    ElseBlock:\n");
        sb.append(elseCondition.getString(s+"        "));

        return sb.toString();
    }

    @Override
    public void statementNode() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "IfStatement";
    }
    
}
