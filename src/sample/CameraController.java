package sample;

import com.github.sarxos.webcam.Webcam;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import kernel.ImageMat;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class CameraController implements Initializable {


    String detectorPath = "D:\\study\\myPCA\\AcpGUI\\libs\\facedetector\\haarcascade_frontalface_alt.xml";
    @FXML
    private ImageView picTaken;

    @FXML
    private JFXButton takeBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }



    @FXML
    void takeCamPicture(ActionEvent event) throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        BufferedImage bufferedImageTaken = webcam.getImage();
        Random random = new Random();
        String name = "cam-"+Integer.toHexString(random.nextInt()) + ".bmp";

        ImageIO.write(bufferedImageTaken, "BMP", new File("src/userPics/"+name));
        Mat img = Imgcodecs.imread("src/userPics/"+name);

        MatOfRect faces = ImageMat.detectFaces("src/userPics/"+name, detectorPath);
        for (Rect rect:faces.toList()
             ) {
                Imgproc.rectangle(img, new org.opencv.core.Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 0));
                //Rect crop = new Rect(rect.x, rect.y, rect.width, rect.height);

        }

        Imgcodecs.imwrite("src/userPics/"+name, img);
        picTaken.setImage(new Image(new FileInputStream("src/userPics/"+name)));
        webcam.close();
    }
}
