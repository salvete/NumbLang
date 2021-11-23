package evaluator.object;

import java.util.LinkedList;

import evaluator.object.interfaces.ObjectInternal;

public class ListInternal implements ObjectInternal {

    public LinkedList<ObjectInternal> elements;

    @Override
    public String Type() {
        // TODO Auto-generated method stub
        return ObjectTypes.List_OBJ;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < elements.size(); i ++)
        {
            if (i == 0)
            {
                sb.append(elements.get(i).Inspect() + " ");
            }
            else
            {
                sb.append(", " + elements.get(i).Inspect() + " ");
            }
        }

        sb.append("]");
        return sb.toString();
    }
}
