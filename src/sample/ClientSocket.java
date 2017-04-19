package sample;

import java.io.*;
import java.net.Socket;

public class ClientSocket implements Runnable {
    private Socket socket;
    private boolean isRunning;

    private ClientsManager clientsManager;
    private GameManager gameManager;

    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private ObjectOutputStream objectOutputStream;

    public ClientSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        isRunning = true;

        try {
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }

        printWriter.println("QUESTION_COMING");
        try {
            objectOutputStream.writeObject(gameManager.getCurrentQuestionPlusAnswer());
        } catch (IOException e) {
            e.printStackTrace();
        }


        while(isRunning) {
            try {
                String line = bufferedReader.readLine();
                if(gameManager.putAnswer(new Answer(line))) {
                    printWriter.println("CORRECT");
                } else {
                    printWriter.println("WRONG");
                }
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
    }

    public void stop() {
        isRunning = false;
        clientsManager.removeClient(this);
    }

    public void setClientsManager(ClientsManager clientsManager) {
        this.clientsManager = clientsManager;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }
}
