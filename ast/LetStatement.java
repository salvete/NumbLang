package ast;

import ast.interfaces.Statement;
import token.Token;

public class LetStatement implements Statement{
    public Token token;
    public Token name;
    public Statement value;


    
    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append(s + "LET Statement\n");
        sb.append(s + "    name:\n");
        sb.append(s + "        " + name.value + "\n");
        sb.append(s + "    value:\n");
        sb.append(value.getString(s + "        ") + "\n");
        return sb.toString();
    }
    @Override
    public void statementNode() {
        // TODO Auto-generated method stub
        
    }
    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "LetStatement";
    }
}
