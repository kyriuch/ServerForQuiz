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

    public ClientSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        isRunning = true;

        try {
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        printWriter.println("Witaj");

        while(isRunning) {
            try {
                System.out.println(bufferedReader.readLine());
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
}
