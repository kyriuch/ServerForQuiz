package sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.Socket;

public class ClientSocket implements Runnable {
    private Socket socket;
    private boolean isRunning;

    private ObjectOutputStream objectOutputStream;
    private BufferedReader bufferedReader;

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
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }

        assert gameManager != null;

        try {
            objectOutputStream.writeObject(new TcpMessage(gameManager.getCurrentQuestion(), Question.class.getName()));
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


        logger.info("Sent " + gameManager.getCurrentQuestion());

        while (isRunning) {
            try {
                String line = bufferedReader.readLine();
                if (gameManager.putAnswer(new Answer(line))) {
                    // good answer
                } else {
                    // wrong answer
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
        Injector injector = new Injector();
        ClientsManager clientsManager = (ClientsManager) injector.get(ClientsManager.class);
        clientsManager.removeClient(this);
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }
}
