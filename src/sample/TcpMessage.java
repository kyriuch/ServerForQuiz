package sample;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TcpMessage implements Serializable {

    protected interface Handler {
        void handle(Object o);
    }

    private static final Map<Class, Handler> dispatch = new HashMap<>();

    private Object outObject;
    private Class outClass;

    public TcpMessage() {
        
    }

    public TcpMessage(Object outObject, Class outClass) {
        this.outObject = outObject;
        this.outClass = outClass;
    }

    public Object getOutObject() {
        return outObject;
    }

    public void setOutObject(Object outObject) {
        this.outObject = outObject;
    }

    public Class getOutClass() {
        return outClass;
    }

    public void setOutClass(Class outClass) {
        this.outClass = outClass;
    }

    public void setHandler(Handler handler) {
        if(!dispatch.containsKey(outClass)) {
            dispatch.put(outClass, handler);
        }
    }

    public void executeHandler() {
        Handler handler = dispatch.get(outClass);

        if(handler != null) {
            handler.handle(outObject);
        }
    }
}