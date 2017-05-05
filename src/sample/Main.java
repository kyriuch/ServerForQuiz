package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Server for Quiz");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();

        Injector injector = new Injector();

        ((ThreadsManager) injector.get(ThreadsManager.class)).addNewThread(new ClientsListener());
    }


    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
