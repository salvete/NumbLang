package ast;

import ast.interfaces.Statement;
import token.Token;

// 表达式在AST中的表现形式
public class ExpressionStatement implements Statement{
    public Token opr;
    public Statement left;
    public Statement right;
    public boolean posOrNeg = false;
    public boolean isLeaf = false;

    @Override
    public void statementNode() {
        // TODO Auto-generated method stub
        
    }
    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        if (isLeaf)
        {
            sb.append(s + "Experssion\n");
            sb.append(s + "    value:\n");
            sb.append(s + "        " + opr.value + "\n");
        }
        else
        {
            sb.append(s + "Experssion\n");
            sb.append(s + "    Operatioin:\n");
            sb.append(s + "        "+opr.type+ "\n");
            sb.append(s + "    Left:\n");
            sb.append(left.getString(s + "        "));
            sb.append(s + "    Right:\n");
            sb.append(right.getString(s + "        "));
        }
        
        return sb.toString();
    }
    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "Expression";
    }

    
    
}
