package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.FileInputStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Parent root = FXMLLoader.load(getClass().getResource("fxml/sample.fxml"));
        primaryStage.setTitle("PCA face recognition");
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(new FileInputStream("src/assets/images/icons/face-recognition.png")));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
