package ast;

import java.util.LinkedList;

import ast.interfaces.Statement;

public class ListStatement implements Statement {

    public LinkedList<Statement> store;

    public ListStatement()
    {
        store = new LinkedList<>();
    }

    @Override
    public String getString(String s) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();

        sb.append(s + "List\n");
        
        for (int i = 0; i < store.size(); i ++)
        {
            sb.append(s + "    Element" + (i+1) + "\n");
            sb.append(store.get(i).getString(s + "    " + "    "));
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
        return "ListStatement";
    }
    
}
