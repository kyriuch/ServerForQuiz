package sample;

public class Question {
    private String content;

    public Question(String content) {
        this.content = content;
    }

    public Question() {

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









