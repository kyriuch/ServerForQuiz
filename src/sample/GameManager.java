package sample;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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
                    .map(ClientSocket::getPrintWriter)
                    .forEach(printer -> {
                        if (printer != null) {
                            printer.println("QUESTION_COMING");
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
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            questionsAndAnswersContainer = objectMapper
                    .readValue(new File("D:\\result.json"), QuestionsAndAnswersContainer.class);

            Map.Entry<Question, Answer> firstEntry = questionsAndAnswersContainer.getHashMap().entrySet().iterator().next();

            currentQuestion = new Question(firstEntry.getKey().getContent());
            currentAnswer = new Answer(firstEntry.getValue().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean putAnswer(Answer answer) {
        if (answer.getContent().equals(currentAnswer.getContent())) {
            Iterator it = questionsAndAnswersContainer.getHashMap().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();

                Answer entryAnswer = (Answer) entry.getValue();

                if (answer.getContent().equals(entryAnswer.getContent())) {
                    if (it.hasNext()) {
                        entry = (Map.Entry) it.next();
                    } else {
                        it = questionsAndAnswersContainer.getHashMap().entrySet().iterator();
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
