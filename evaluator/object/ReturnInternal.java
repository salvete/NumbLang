package evaluator.object;

import evaluator.object.interfaces.ObjectInternal;

public class ReturnInternal implements ObjectInternal {

    public ObjectInternal Value;

    public ReturnInternal(ObjectInternal obj)
    {
        Value = obj;
    }

    @Override
    public String Type() {
        // TODO Auto-generated method stub
        return ObjectTypes.RETURN_OBJ;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        return Value.Inspect();
    }
    
}
