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
    private User user;

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
            objectOutputStream.reset();
            logger.info("Sent Question TcpMessage");
        } catch (IOException e) {
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
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
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

    private void proceedIncomingTcpMessage(TcpMessage tcpMessage) throws IllegalAccessException, InstantiationException, ClassNotFoundException {

        if (tcpMessage.getOutClass().equals(ChatMessage.class)) {
            tcpMessage.setHandler(o -> {
                Injector injector = new Injector();

                try {
                    GameManager gameManager = (GameManager) injector.get(GameManager.class);
                    ChatMessage chatMessage = new ChatMessage("NORMAL",
                            ((ChatMessage) o).getUser(),
                            ((ChatMessage) o).getMessage());

                    gameManager.sendTcpMessageToAllClients(new TcpMessage(chatMessage, ChatMessage.class));

                    if(chatMessage.getMessage().equalsIgnoreCase("/points")) {
                        gameManager.sendTcpMessageToAllClients(new TcpMessage(new ChatMessage(
                                "SERVER", "SERVER", user.getName() + " posiada " +
                                user.getPoints() + " punktów"), ChatMessage.class));
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }


            });
        } else if (tcpMessage.getOutClass().equals(Answer.class)) {
            tcpMessage.setHandler(o -> {
                Injector injector = new Injector();

                try {
                    GameManager gameManager = (GameManager) injector.get(GameManager.class);
                    if (gameManager.putAnswer((Answer) o)) {
                        user.addPoint();

                        gameManager.sendTcpMessageToAllClients(new TcpMessage(
                                new ChatMessage("SERVER", "SERVER", user.getName() +
                                        " poprawnie odpowiada: " + ((Answer) o).getContent()), ChatMessage.class));

                        gameManager.sendTcpMessageToAllClients(new TcpMessage(
                                gameManager.getCurrentQuestion(), Question.class
                        ));
                    } else {
                        gameManager.sendTcpMessageToAllClients(new TcpMessage(new ChatMessage("SERVER",
                                "SERVER", user.getName() + " błędnie odpowiada: " +
                                ((Answer) o).getContent()), ChatMessage.class));
                    }

                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } else if (tcpMessage.getOutClass().equals(User.class)) {
            tcpMessage.setHandler(o -> {
                Injector injector = new Injector();
                this.user = (User) o;

                try {
                    GameManager gameManager = (GameManager) injector.get(GameManager.class);
                    ChatMessage chatMessage = new ChatMessage("SERVER", "SERVER",
                            user.getName() + " połączył się");
                    System.out.println(chatMessage.getMessage());


                    gameManager.sendTcpMessageToAllClients(new TcpMessage(chatMessage, ChatMessage.class));
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

        tcpMessage.executeHandler();
    }
}
