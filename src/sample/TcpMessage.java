package sample;

import java.io.Serializable;

public class TcpMessage implements Serializable {
    private Object outObject;
    private String outType;

    public TcpMessage(Object outObject, String outType) {
        this.outObject = outObject;
        this.outType = outType;
    }

    public TcpMessage() {
        
    }

    public Object getOutObject() {
        return outObject;
    }

    public String getOutType() {
        return outType;
    }

    public void setOutObject(Object outObject) {
        this.outObject = outObject;
    }

    public void setOutClass(String outType) {
        this.outType = outType;
    }
}
