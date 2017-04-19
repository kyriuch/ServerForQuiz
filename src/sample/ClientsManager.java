package sample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomek on 11.04.2017.
 */
public class ClientsManager {
    private List<ClientSocket> list;

    public ClientsManager() {
        list = new ArrayList<>();
    }

    public void addClient(ClientSocket clientSocket) {
        System.out.println("NEW CLIENT");
        list.add(clientSocket);
    }

    public void removeClient(ClientSocket clientSocket) {
        list.remove(clientSocket);
    }

    public List<ClientSocket> getList() {
        return list;
    }
}
