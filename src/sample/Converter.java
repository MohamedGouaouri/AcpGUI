package sample;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Converter {
    @FXML
    private ImageView inputImageView;

    @FXML
    private JFXRadioButton bmp;

    @FXML
    private JFXRadioButton png;

    @FXML
    private JFXRadioButton jpg;

    @FXML
    private JFXRadioButton jpeg;

    @FXML
    private JFXTextField outPath;


    private File inputImage;

    @FXML
    void convert(ActionEvent event) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(inputImage);
        String format;
        if (png.isSelected()){
            format = "png";
        }
        else if (jpeg.isSelected()){
            format = "jpeg";
        }else if (jpg.isSelected()){
            format = "jpg";
        }else {
            format = "bmp";
        }

        if (!outPath.getText().isEmpty()){
            try{

                assert bufferedImage != null;
                ImageIO.write(bufferedImage, format, new FileOutputStream(outPath.getText()));
                alertMsg("Image converted successfully", Alert.AlertType.CONFIRMATION);
            }catch (IOException e){
                alertMsg("Could not convert the image \n Verify your input", Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }else {
            alertMsg("You must specify a path", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void upload(ActionEvent event) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        // TODO: 24/03/2020 Filter selected file
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP", "*.bmp", "*.jpeg", "*.jpg", "*.png"));
        File faceFile = fileChooser.showOpenDialog(null);
        if (faceFile != null){
            inputImage = faceFile;
            inputImageView.setImage(new Image(new FileInputStream(faceFile.getPath())));
        }
    }

    public static void alertMsg(String msg, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();

    }
}
