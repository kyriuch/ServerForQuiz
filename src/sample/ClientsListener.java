package sample;

import java.io.IOException;
import java.net.ServerSocket;

public class ClientsListener implements Runnable {
    private boolean isRunning;
    private GameManager gameManager;
    private ClientsManager clientsManager;

    public ClientsListener(GameManager gameManager, ClientsManager clientsManager) {
        this.gameManager = gameManager;
        this.clientsManager = clientsManager;
    }

    @Override
    public void run() {
        isRunning = true;

        ServerSocket socket = null;

        try {
            socket = new ServerSocket(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(socket == null) {
            return;
        }

        while(isRunning) {
            try {
                ClientSocket clientSocket = new ClientSocket(socket.accept());
                clientSocket.setClientsManager(clientsManager);
                clientSocket.setGameManager(gameManager);
                clientsManager.addClient(clientSocket);
                ThreadsManager.getInstance().addNewThread(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        isRunning = false;
    }
}
