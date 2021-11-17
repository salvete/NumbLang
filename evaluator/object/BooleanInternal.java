package evaluator.object;

import evaluator.object.interfaces.NumberOrBoolean;
import evaluator.object.interfaces.ObjectInternal;

public class BooleanInternal implements ObjectInternal,NumberOrBoolean {
    public boolean Value;

    public BooleanInternal(boolean b)
    {
        Value = b;
    }

    @Override
    public void opposite()
    {
        Value = !Value;
    }

    @Override
    public String Type() {
        // TODO Auto-generated method stub
        return ObjectTypes.BOOLEAN_OBJ;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        return String.valueOf(Value);
    }
    
}
