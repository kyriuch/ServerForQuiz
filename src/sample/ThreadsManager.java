package sample;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadsManager {
    private ExecutorService executorService;

    public ThreadsManager() {
        executorService = Executors.newCachedThreadPool();
    }

    public void addNewThread(Runnable runnable) {
        executorService.execute(runnable);
    }
}
