package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void start(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Main.class.getResource("fxml/main.fxml"));
        Main.window.setTitle("PCA face recognition");
        Main.window.setResizable(false);
        Main.window.getIcons().add(new Image(new FileInputStream("src/assets/images/icons/face-recognition.png")));
        Main.window.setScene(new Scene(root));
    }

    public void getDoc(ActionEvent actionEvent) {
        try {
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }

            desktop.open(new File("D:\\study\\myPCA\\AcpGUI\\docs\\index.html"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void getHelp(ActionEvent actionEvent) {
    }
}
