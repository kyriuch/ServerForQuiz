package sample;

import java.io.Serializable;

public class QuestionPlusAnswer implements Serializable {
    private Question question;
    private Answer answer;

    public QuestionPlusAnswer(){

    }

    public QuestionPlusAnswer(Question question, Answer answer) {
        this.question = question;
        this.answer = answer;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return question + ":" + answer;
    }
}
