package ast;

import ast.interfaces.BuildIn;
import ast.interfaces.Statement;
import token.Token;

public class BuildInStatement implements BuildIn {
    public Token func;
    public Statement lenObj;

    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append(s + "BuildIn function: len()\n");
        sb.append(lenObj.getString(s + "    "));
        return sb.toString();
    }

    @Override
    public void statementNode() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "BuildStatement";
    }

    @Override
    public String buildIn() {
        // TODO Auto-generated method stub
        return func.value;
    }
    
}
