package sample;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Tomek on 11.04.2017.
 */
public class ThreadsManager {
    private ExecutorService executorService;

    private static ThreadsManager ourInstance = new ThreadsManager();

    public static ThreadsManager getInstance() {
        return ourInstance;
    }

    private ThreadsManager() {
        executorService = Executors.newCachedThreadPool();
    }

    public void addNewThread(Runnable runnable) {
        executorService.execute(runnable);
    }
}
