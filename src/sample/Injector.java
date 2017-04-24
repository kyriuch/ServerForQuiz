package sample;

import java.util.HashMap;

public class Injector {
    private static HashMap<Class, Object> hashMap = new HashMap<>();

    public Object get(Class key) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if(!hashMap.containsKey(key)) {
            Object output = key.newInstance();
            hashMap.put(key, output);
        }

        return hashMap.get(key);
    }
}
