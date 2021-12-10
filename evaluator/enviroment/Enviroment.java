package evaluator.enviroment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

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
        for(Map.Entry<String,ObjectInternal> e: store.entrySet())
        {
            if (Objects.equals(name, e.getKey()))
                return e.getValue();
        }

       
        if (outer != null)
            return outer.Get(name);
        else
            return new NullInternel();
        
    }

    public ObjectInternal Set(String name, ObjectInternal obj)
    {
        Iterator<Map.Entry<String,ObjectInternal>> it = store.entrySet().iterator();
        boolean flag = false;

        while (it.hasNext())
        {
            Map.Entry<String,ObjectInternal> e = it.next();

            String name_ = e.getKey();
            
            if (Objects.equals(name, name_))
            {
                flag = true;
                store.put(name_, obj);
            }
        }

        if (!flag)
            store.put(name, obj);
        
        return obj;
    }

    
    public static Enviroment getNewEnviroment(Enviroment out)
    {
        return new Enviroment(out);  
    }

}
