package sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class ClientSocket implements Runnable {
    private Socket socket;
    private boolean isRunning;

    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private ObjectOutputStream objectOutputStream;

    public ClientSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        isRunning = true;

        Injector injector = new Injector();
        GameManager gameManager = null;
        Logger logger = LoggerFactory.getLogger(ClientSocket.class);

        logger.info("Starting socket - " + String.valueOf(socket));

        try {
            gameManager = (GameManager) injector.get(GameManager.class);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        try {
            OutputStream os = socket.getOutputStream();
            printWriter = new PrintWriter(os);
            objectOutputStream = new ObjectOutputStream(os);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }

        printWriter.println("QUESTION_COMING");
        printWriter.flush();
        logger.info("Sent phrase \"QUESTION COMING\"");


        try {
            assert gameManager != null;

            objectOutputStream.writeObject(gameManager.getCurrentQuestion());
            objectOutputStream.flush();

            logger.info("Sent " + gameManager.getCurrentQuestion());
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
                try {
                    stop();
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void stop() throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {
        isRunning = false;

        bufferedReader.close();
        objectOutputStream.close();
        printWriter.close();
        Injector injector = new Injector();
        ClientsManager clientsManager = (ClientsManager) injector.get(ClientsManager.class);
        clientsManager.removeClient(this);
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }
}
