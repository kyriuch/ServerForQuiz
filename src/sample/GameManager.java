package sample;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Map;

public class GameManager implements Runnable {
    private QuestionsAndAnswersContainer questionsAndAnswersContainer;
    private QuestionPlusAnswer currentQuestionPlusAnswer;
    boolean isRunning = false;

    private void sendQuestionToClients(QuestionPlusAnswer question) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Injector injector = new Injector();
        ClientsManager clientsManager = (ClientsManager) injector.get(ClientsManager.class);

        if(clientsManager.getList() != null && !clientsManager.getList().isEmpty()) {
            clientsManager.getList().stream()
                    .map(clientSocket -> new Object[]{clientSocket.getPrintWriter(), clientSocket.getObjectOutputStream()})
                    .forEach(tab -> {
                        if(tab[0] != null && tab[1] != null) {
                            ((PrintWriter) tab[0]).println("QUESTION_COMING");
                            try {
                                ((ObjectOutputStream) tab[1]).writeObject(question);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }

    public GameManager() {
        if(!isRunning) {
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

            currentQuestionPlusAnswer = new QuestionPlusAnswer();

            currentQuestionPlusAnswer.setAnswer(firstEntry.getValue());
            currentQuestionPlusAnswer.setQuestion(firstEntry.getKey());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean putAnswer(Answer answer) {
        if(answer.getContent().equals(currentQuestionPlusAnswer.getAnswer().getContent())) {
            Map.Entry<Question, Answer> newEntry;

            boolean takeNow = false;

            for(Map.Entry<Question, Answer> entry:questionsAndAnswersContainer.getHashMap().entrySet()) {
                if(takeNow) {
                    currentQuestionPlusAnswer.setAnswer(entry.getValue());
                    currentQuestionPlusAnswer.setQuestion(entry.getKey());

                    takeNow = false;
                    break;
                }

                if(entry.getValue().getContent().equals(answer.getContent())) {
                    takeNow = true;
                }
            }

            // if(takeNow == false) { koniec pytan }

            return true;
        }

        return false;
    }

    public QuestionPlusAnswer getCurrentQuestionPlusAnswer() {
        return currentQuestionPlusAnswer;
    }

    public Question getCurrentQuestion() {
        return currentQuestionPlusAnswer.getQuestion();
    }

    public Answer getCurrentAnswer() {
        return currentQuestionPlusAnswer.getAnswer();
    }
}
