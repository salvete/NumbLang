package evaluator.object;

import evaluator.object.interfaces.NumberOrBoolean;
import evaluator.object.interfaces.ObjectInternal;

public class FloatInternal implements ObjectInternal,NumberOrBoolean {

    public float Value;

    public FloatInternal(float x)
    {
        Value = x;
    }


    @Override
    public String Type() {
        // TODO Auto-generated method stub
        return ObjectTypes.FLOAT_OBJ;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        return String.valueOf(Value);
    }


    @Override
    public void opposite() {
        // TODO Auto-generated method stub
        Value = - Value;
    }


    @Override
    public String Inspect(String s) {
        // TODO Auto-generated method stub
        return s + Inspect();
    }
    
}
