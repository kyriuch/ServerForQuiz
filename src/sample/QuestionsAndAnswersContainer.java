package sample;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class QuestionsAndAnswersContainer {
    private Map<Question, Answer> map = new HashMap<>();

    public QuestionsAndAnswersContainer(Map<Question, Answer> map) {
        this.map = map;
    }

    public QuestionsAndAnswersContainer() {

    }

    public void addQuestionAndAnswer(Question question, Answer answer) {
        map.put(question, answer);
    }

    public Map<Question, Answer> getMap() {
        return map;
    }

    public void setMap(Map<Question, Answer> map) {
        this.map = map;
    }
}
