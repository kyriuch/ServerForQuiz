package sample;

import java.io.IOException;
import java.net.ServerSocket;

public class ClientsListener implements Runnable {
    private boolean isRunning;

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
                Injector injector = new Injector();
                ClientsManager clientsManager = (ClientsManager) injector.get("ClientsManager");
                ThreadsManager threadsManager = (ThreadsManager) injector.get("ThreadsManager");
                ClientSocket clientSocket = new ClientSocket(socket.accept());
                clientsManager.addClient(clientSocket);
                threadsManager.addNewThread(clientSocket);
            } catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        isRunning = false;
    }
}
