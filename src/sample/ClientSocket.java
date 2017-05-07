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
            objectOutputStream.writeObject(new TcpMessage("SERVER", gameManager.getCurrentQuestion(), Question.class));
            objectOutputStream.flush();
            objectOutputStream.reset();
            logger.info("Sent Question TcpMessage");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (isRunning) {
            try {
                TcpMessage tcpMessage = (TcpMessage) objectInputStream.readObject();
                logger.info("Got TcpMessage - " + String.valueOf(tcpMessage) + " from " + tcpMessage.getFrom());

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
                    UserManager userManager = (UserManager) injector.get(UserManager.class);
                    ChatMessage chatMessage = new ChatMessage("NORMAL",
                            ((ChatMessage) o).getUser(),
                            ((ChatMessage) o).getMessage());

                    gameManager.sendTcpMessageToAllClients(new TcpMessage(tcpMessage.getFrom(), chatMessage, ChatMessage.class));

                    if(chatMessage.getMessage().equalsIgnoreCase("/points")) {
                        gameManager.sendTcpMessageToAllClients(new TcpMessage("SERVER", new ChatMessage(
                                "SERVER", "SERVER", tcpMessage.getFrom() + " posiada " +
                                userManager.getUserPoints(tcpMessage.getFrom()) + " punktów"), ChatMessage.class));
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
                    UserManager userManager = (UserManager) injector.get(UserManager.class);
                    if (gameManager.putAnswer((Answer) o)) {
                        userManager.addPointToUser(tcpMessage.getFrom());

                        gameManager.sendTcpMessageToAllClients(new TcpMessage("SERVER",
                                new ChatMessage("SERVER", "SERVER",
                                        tcpMessage.getFrom() + " poprawnie odpowiada: " + ((Answer) o).getContent()),
                                ChatMessage.class));

                        gameManager.sendTcpMessageToAllClients(new TcpMessage("SERVER",
                                gameManager.getCurrentQuestion(), Question.class
                        ));
                    } else {
                        gameManager.sendTcpMessageToAllClients(new TcpMessage("SERVER",
                                new ChatMessage("SERVER", "SERVER",
                                        tcpMessage.getFrom() + " błędnie odpowiada: " + ((Answer) o).getContent()),
                                ChatMessage.class));
                    }

                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        } else if (tcpMessage.getOutClass().equals(User.class)) {
            System.out.println("TUTAJ " + tcpMessage.getFrom());
            tcpMessage.setHandler(o -> {
                Injector injector = new Injector();
                UserManager userManager = (UserManager) injector.get(UserManager.class);
                userManager.addUser(((User) o).getName());

                try {
                    GameManager gameManager = (GameManager) injector.get(GameManager.class);
                    ChatMessage chatMessage = new ChatMessage("SERVER", "SERVER",
                            tcpMessage.getFrom() + " + połączył się");
                    System.out.println(chatMessage.getMessage());


                    gameManager.sendTcpMessageToAllClients(new TcpMessage("SERVER",
                            chatMessage, ChatMessage.class));
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }

        tcpMessage.executeHandler();
    }
}
