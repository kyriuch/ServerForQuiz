package sample;

import java.util.HashMap;

public class Injector {
    private HashMap<String, Object> hashMap = new HashMap<>();

    public Object get(String type) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if(!hashMap.containsKey(type)) {
            Class c = Class.forName("sample." + type);
            Object output = c.newInstance();
            hashMap.put(type, output);
        }

        return hashMap.get(type);
    }
}
