package sample;


import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Injector injector = new Injector();
        ((ThreadsManager) injector.get(ThreadsManager.class)).addNewThread(new ClientsListener());
    }
}
