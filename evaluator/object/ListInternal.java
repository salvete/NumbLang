package evaluator.object;

import evaluator.object.interfaces.ObjectInternal;

import java.util.LinkedList;

public class ListInternal implements ObjectInternal {
    public LinkedList<ObjectInternal> elements;

    public ListInternal()
    {
        elements = new LinkedList<>();
    }

    @Override
    public String Type() {
        return ObjectTypes.LIST_OBJ;
    }

    @Override
    public String Inspect() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");

        for (int i = 0; i < elements.size(); i ++)
        {
            if (i == 0)
            {
                sb.append(" " + elements.get(i).Inspect() + " ");
            }
            else
            {
                sb.append(", " + elements.get(i).Inspect());
            }
        }

        sb.append("]");
        return sb.toString();
    }

    @Override
    public String Inspect(String s) {
        // TODO Auto-generated method stub
        return s + Inspect();
    }
}
