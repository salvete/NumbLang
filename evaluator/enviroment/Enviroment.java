package evaluator.enviroment;

import java.util.HashMap;

import evaluator.object.NullInternel;
import evaluator.object.interfaces.ObjectInternal;

public class Enviroment {
    public HashMap<String,ObjectInternal> store;
    Enviroment outer;
    
    public Enviroment(Enviroment out)
    {
        store = new HashMap<>();
        outer = out;
    }

    public ObjectInternal Get(String name)
    {
        if (store.containsKey(name))
            return store.get(name);
        else
        {
            if (outer != null)
                return outer.Get(name);
            else
                return new NullInternel();
        }
    }

    public ObjectInternal Set(String name, ObjectInternal obj)
    {
        store.put(name, obj);
        return obj;
    }

    
    public static Enviroment getNewEnviroment(Enviroment out)
    {
        return new Enviroment(out);  
    }

}
