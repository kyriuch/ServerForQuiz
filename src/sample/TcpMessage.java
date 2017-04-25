package sample;

import java.io.Serializable;

public class TcpMessage implements Serializable {
    private Object outObject;
    private Class outClass;

    public TcpMessage(Object outObject, Class outClass) {
        this.outObject = outObject;
        this.outClass = outClass;
    }

    public TcpMessage() {
        
    }

    public Object getOutObject() {
        return outObject;
    }

    public Class getOutClass() {
        return outClass;
    }

    public void setOutObject(Object outObject) {
        this.outObject = outObject;
    }

    public void setOutClass(Class outClass) {
        this.outClass = outClass;
    }
}
