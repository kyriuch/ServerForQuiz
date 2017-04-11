package sample;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Created by Tomek on 11.04.2017.
 */
public class GameManager implements Runnable {
    QuestionsAndAnswersContainer questionsAndAnswersContainer;

    @Override
    public void run() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            questionsAndAnswersContainer = objectMapper
                    .readValue(new File("D:\\result.json"), QuestionsAndAnswersContainer.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
