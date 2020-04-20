package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import kernel.ImageMat;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import java.io.*;

public class Main extends Application{

    public static  Stage window;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxml/welcome.fxml"));
        window = primaryStage;
        window.setTitle("PCA face recognition");
        window.setResizable(false);
        window.getIcons().add(new Image(new FileInputStream("src/assets/images/icons/face-recognition.png")));
        window.setScene(new Scene(root));
        window.show();
    }


    public static void main(String[] args) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
