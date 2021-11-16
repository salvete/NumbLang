package ast;

import ast.interfaces.Statement;
import token.Token;
public class ReturnStatement implements Statement {

    public Token token;
    public Statement returnValue;

    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append(s + "Return Statement\n");
        sb.append(s + "    name:\n");
        sb.append(s + "        " + token.value + "\n");
        sb.append(s + "    value:\n");
        sb.append(returnValue.getString(s+"        "));
        return sb.toString();
    }

    @Override
    public void statementNode() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "ReturnStatement";
    }

    
    
}
