package ast;



import ast.interfaces.Statement;
import token.Token;

public class IdentifierStatement implements Statement {
    public Token name;
    public boolean posOrNeg = false; 

    public  IdentifierStatement(Token t)
    {
        name = t;
    }

    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append(s + "Identifier\n");
        sb.append(s + "    name:\n");
        sb.append(s + "        " + name.value + "\n");
        return sb.toString();
    }

    @Override
    public void statementNode() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "Identifier";
    }

    
}
