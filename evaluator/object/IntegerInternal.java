package evaluator.object;

import evaluator.object.interfaces.NumberOrBoolean;
import evaluator.object.interfaces.ObjectInternal;

public class IntegerInternal implements ObjectInternal,NumberOrBoolean {

    public int Value;

    public IntegerInternal(int a)
    {
        Value = a;
    }

    @Override
    public String Type() {
        // TODO Auto-generated method stub
        return ObjectTypes.INTEGER_OBJ;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        return String.valueOf(Value);
    }

    @Override
    public void opposite() {
        // TODO Auto-generated method stub
        Value = -Value;
    }
    
}
