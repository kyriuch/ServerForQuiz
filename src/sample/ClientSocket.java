package sample;

import com.sun.xml.internal.ws.developer.SerializationFeature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class ClientSocket implements Runnable {
    private Socket socket;
    private boolean isRunning;

    private PrintWriter printWriter;
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
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }

        assert gameManager != null;

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            printWriter.println((objectMapper.writeValueAsString(
                    new TcpMessage(gameManager.getCurrentQuestion(), Question.class)
            )));
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Sent " + gameManager.getCurrentQuestion());

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
        printWriter.close();
        Injector injector = new Injector();
        ClientsManager clientsManager = (ClientsManager) injector.get(ClientsManager.class);
        clientsManager.removeClient(this);
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }
}
