package sample;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TcpMessage implements Serializable {

    protected interface Handler {
        void handle(Object o) throws IllegalAccessException, InstantiationException, ClassNotFoundException;
    }

    private Handler handler;

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
        this.handler = handler;
    }

    public void executeHandler() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        if(handler != null) {
            handler.handle(outObject);
        }
    }
}