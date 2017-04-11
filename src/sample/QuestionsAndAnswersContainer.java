package sample;

import java.util.LinkedHashMap;
import java.util.Map;

public class QuestionsAndAnswersContainer {
    private LinkedHashMap<Question, Answer> hashMap = new LinkedHashMap<Question, Answer>();

    public QuestionsAndAnswersContainer(LinkedHashMap<Question, Answer> hashMap) {
        this.hashMap = hashMap;
    }

    public QuestionsAndAnswersContainer() {

    }

    public void addQuestionAndAnswer(Question question, Answer answer) {
        hashMap.put(question, answer);
    }

    public LinkedHashMap<Question, Answer> getHashMap() {
        return hashMap;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for(Map.Entry<Question, Answer> entry:hashMap.entrySet()) {
            result.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
        }

        return result.toString();

    }
}
