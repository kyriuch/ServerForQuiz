package sample;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Map;

public class GameManager implements Runnable {
    QuestionsAndAnswersContainer questionsAndAnswersContainer;
    QuestionPlusAnswer currentQuestionPlusAnswer;
    ClientsManager clientsManager;
    private static int id = 0;

    private void sendQuestionToClients(QuestionPlusAnswer question) {
        clientsManager.getList().stream()
                .map(clientSocket -> new Object[]{clientSocket.getPrintWriter(), clientSocket.getObjectOutputStream()})
                .forEach(tab -> {
            ((PrintWriter) tab[0]).println("QUESTION_COMING");
            try {
                ((ObjectOutputStream) tab[1]).writeObject(question);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void run() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            questionsAndAnswersContainer = objectMapper
                    .readValue(new File("D:\\result.json"), QuestionsAndAnswersContainer.class);

            Map.Entry<Question, Answer> firstEntry = questionsAndAnswersContainer.getHashMap().entrySet().iterator().next();

            currentQuestionPlusAnswer = new QuestionPlusAnswer();

            currentQuestionPlusAnswer.setAnswer(firstEntry.getValue());
            currentQuestionPlusAnswer.setQuestion(firstEntry.getKey());
            sendQuestionToClients(currentQuestionPlusAnswer);
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

    public void setClientsManager(ClientsManager clientsManager) {
        this.clientsManager = clientsManager;
    }

    public QuestionPlusAnswer getCurrentQuestionPlusAnswer() {
        return currentQuestionPlusAnswer;
    }
}
