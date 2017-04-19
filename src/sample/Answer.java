package sample;

import java.io.Serializable;

public class Answer implements Serializable {
    private String content;

    public Answer(String content) {
        this.content = content;
    }

    public Answer() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
