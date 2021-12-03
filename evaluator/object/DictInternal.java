package evaluator.object;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import evaluator.object.interfaces.ObjectInternal;

public class DictInternal implements ObjectInternal {

    public HashMap<String, ObjectInternal> dict;

    public DictInternal()
    {
        dict = new HashMap<>();
    }

    @Override
    public String Type() {
        // TODO Auto-generated method stub
        return ObjectTypes.DICT_OBJ;
    }

    @Override
    public String Inspect() {
        // TODO Auto-generated method stub
        
        return Inspect("");
    }

    public String Inspect(String s)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(s + "{\n");
    

        Iterator<Map.Entry<String, ObjectInternal>> it = dict.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry<String,ObjectInternal> entry = it.next();

            sb.append(s + "    " + "key:\n");
            sb.append(s + "    " + "    " +  entry.getKey() + "\n");
            sb.append(s + "    " + "value:\n");
            sb.append(entry.getValue().Inspect(s + "    " + "    "));
            sb.append("\n");
        }

        sb.append(s + "}\n");

        return sb.toString();
    }
    
}
