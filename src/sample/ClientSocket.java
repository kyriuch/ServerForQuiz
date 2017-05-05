package sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class ClientSocket implements Runnable {
    private Socket socket;
    private boolean isRunning;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

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
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            isRunning = false;
        }

        assert gameManager != null;

        try {
            objectOutputStream.writeObject(new TcpMessage(gameManager.getCurrentQuestion(), Question.class));
            objectOutputStream.flush();
            logger.info("Sent Question TcpMessage");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            gameManager.sendTcpMessageToAllClients(new TcpMessage(
                    new ChatMessage("SERVER", "SERVER", "New user connected"), ChatMessage.class));
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        while (isRunning) {
            try {
                TcpMessage tcpMessage = (TcpMessage) objectInputStream.readObject();
                logger.info("Got TcpMessage - " + String.valueOf(tcpMessage));

                proceedIncomingTcpMessage(tcpMessage);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    stop();
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException | IOException e1) {
                    e1.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException {
        isRunning = false;

        objectInputStream.close();
        objectOutputStream.close();
        Injector injector = new Injector();
        ClientsManager clientsManager = (ClientsManager) injector.get(ClientsManager.class);
        clientsManager.removeClient(this);
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    private void proceedIncomingTcpMessage(TcpMessage tcpMessage) {

        if (tcpMessage.getOutClass().equals(ChatMessage.class)) {
            tcpMessage.setHandler(o -> {
                Injector injector = new Injector();

                try {
                    GameManager gameManager = (GameManager) injector.get(GameManager.class);
                    gameManager.sendTcpMessageToAllClients(new TcpMessage(o, ChatMessage.class));
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }


            });
        }else if(tcpMessage.getOutClass().equals(Answer.class)) {
            tcpMessage.setHandler(o -> {
                Injector injector = new Injector();

                try {
                    GameManager gameManager = (GameManager) injector.get(GameManager.class);
                    if(gameManager.putAnswer((Answer) o)) {
                        gameManager.sendTcpMessageToAllClients(new TcpMessage(
                                gameManager.getCurrentQuestion(), Question.class
                        ));
                    };
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

        tcpMessage.executeHandler();
    }
}
