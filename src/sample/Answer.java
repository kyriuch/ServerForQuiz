package sample;

public class Answer {
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
