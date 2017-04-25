package sample;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

public class GameManager implements Runnable {
    private QuestionsAndAnswersContainer questionsAndAnswersContainer;
    private Question currentQuestion;
    private Answer currentAnswer;
    boolean isRunning = false;

    private void sendQuestionToClients(Question question) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Injector injector = new Injector();
        ClientsManager clientsManager = (ClientsManager) injector.get(ClientsManager.class);

        if (clientsManager.getList() != null && !clientsManager.getList().isEmpty()) {
            clientsManager.getList().stream()
                    .map(ClientSocket::getObjectOutputStream)
                    .forEach(outputStream -> {
                        if (outputStream != null) {
                            try {
                                outputStream.writeObject(currentQuestion);
                                outputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }

    public GameManager() {
        if (!isRunning) {
            run();
        }
    }

    @Override
    public void run() {
        isRunning = true;
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

        try {
            questionsAndAnswersContainer = gson.fromJson(new FileReader("D:\\result.json"), QuestionsAndAnswersContainer.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Map.Entry<Question, Answer> firstEntry = questionsAndAnswersContainer.getMap().entrySet().iterator().next();

        currentQuestion = new Question(firstEntry.getKey().getContent());
        currentAnswer = new Answer(firstEntry.getValue().getContent());
    }

    public boolean putAnswer(Answer answer) {
        if (answer.getContent().equals(currentAnswer.getContent())) {
            Iterator it = questionsAndAnswersContainer.getMap().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();

                Answer entryAnswer = (Answer) entry.getValue();

                if (answer.getContent().equals(entryAnswer.getContent())) {
                    if (it.hasNext()) {
                        entry = (Map.Entry) it.next();
                    } else {
                        it = questionsAndAnswersContainer.getMap().entrySet().iterator();
                        entry = (Map.Entry) it.next();
                    }

                    currentQuestion = (Question) entry.getKey();
                    currentAnswer = (Answer) entry.getValue();

                    break;
                }
            }
            return true;
        }

        return false;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public Answer getCurrentAnswer() {
        return currentAnswer;
    }
}
