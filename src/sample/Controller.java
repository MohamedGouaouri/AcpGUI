package sample;


import com.github.sarxos.webcam.Webcam;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSlider;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kernel.Acp;
import kernel.ImageMat;
import kernel.Result;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    private static Acp pca = new Acp(3000);
    private static HashMap<String, Number> distancesMap;
    private boolean trained = false;
    private int eigenfacesNumber;


    @FXML
    private ImageView faceImage;

    @FXML
    private JFXButton trainBtn;

    private String facePath;

    @FXML
    private Label result;

    @FXML
    private JFXSlider thresholdSlider;

    @FXML
    private Button saveTrainBtn;

    @FXML
    private Button loadBtn;

    @FXML
    private JFXButton getStatBtn;

    @FXML
    private Pane leftPane;

    @FXML
    private JFXSlider percentageValue;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    @FXML
    void loadFace(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        // TODO: 24/03/2020 Filter selected file
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP", "*.bmp"));

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null){
            try{
                facePath = selectedFile.getPath();
                faceImage.setImage(new Image(new FileInputStream(selectedFile.getPath())));
            }catch (FileNotFoundException e){
                alertMsg("File not found", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void recognize(ActionEvent event) {
        try{
            if (trained){
                String path_to_orl = facePath;
                pca.setThreshold(thresholdSlider.getValue());
                Result recognition_result = pca.recognize(path_to_orl);
                result.setText(recognition_result.name());
                if (recognition_result == Result.RECONNUE) result.setTextFill(Color.web("#00FF00"));
                if (recognition_result == Result.CONFUSION) result.setTextFill(Color.web("#ffb70f"));
                if (recognition_result == Result.REJETE) result.setTextFill(Color.web("#FF0000"));

                distancesMap = pca.distancesMap;
            }

            else {
                alertMsg("Model must be trained before recognizing", Alert.AlertType.WARNING);
            }
        }catch (Exception e){
            alertMsg("You must specify a correct path to the input image\n or image " +
                    "must have size of 92 x 112", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void trainModel(ActionEvent event) {

        alertMsg("Model is now training\n" +
                "This may take about 20 seconds", Alert.AlertType.INFORMATION);
        new Thread(() -> {
            pca.trainModel(percentageValue.getValue() / 100.0);
            Platform.runLater(() -> {
                Controller.alertMsg("model is now trained you can enter face path and recognize it", Alert.AlertType.CONFIRMATION);
                trained = true;
                loadBtn.setDisable(true);
            });
        }).start();
    }


    public static void alertMsg(String msg, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


    /// PCA serialization
    @FXML
    public void saveTrainState(ActionEvent event){
        if (!trained){
            alertMsg("You must train the model first", Alert.AlertType.ERROR);
            return;
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("pca.ser");
            ObjectOutputStream pcaOut = new ObjectOutputStream(fileOutputStream);
            pcaOut.writeObject(pca);
            pcaOut.close();
            fileOutputStream.close();
            alertMsg("your training is now saved\n second time you open this app just click on load button", Alert.AlertType.INFORMATION);
            trainBtn.setDisable(true);
            saveTrainBtn.setDisable(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void loadTrainState(ActionEvent event){

        try {
            FileInputStream fileInputStream = new FileInputStream("pca.ser");
            ObjectInputStream pcaIn = new ObjectInputStream(fileInputStream);
            pca = (Acp) pcaIn.readObject();
            pcaIn.close();
            fileInputStream.close();
            trained = true;
            trainBtn.setDisable(true);
            saveTrainBtn.setDisable(true);
            alertMsg("model is now trained you can enter face path and recognize it", Alert.AlertType.CONFIRMATION);
        }catch (Exception e){
            alertMsg("An error occurred while loading train model", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


    // get help in browser
    public void getHelp(ActionEvent actionEvent) {

        try {
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }

            desktop.open(new File("D:\\study\\PCAUI\\docs\\index.html"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    @FXML
    void takePicture(ActionEvent event) throws IOException {
        Parent camera = FXMLLoader.load(getClass().getResource("fxml/webcam.fxml"));
        Scene scene = new Scene(camera);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Take picture");
        stage.getIcons().add(new Image(new FileInputStream("src/assets/images/icons/photograph.png")));
        stage.setScene(scene);
        stage.show();
    }


    // take picture section
    @FXML
    private ImageView picTaken;

    @FXML
    void takeCamPicture(ActionEvent event) throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        BufferedImage bufferedImageTaken = webcam.getImage();
        ImageIO.write(bufferedImageTaken, "BMP", new File("src/userPics/image.bmp"));
        ImageMat.resizeImage("src/userPics/image.bmp");
        ImageMat.grayscaleImage("src/userPics/image.bmp");
        picTaken.setImage(new Image(new FileInputStream("src/userPics/image.bmp")));
        webcam.close();
    }



    @FXML
    void showStatistics(ActionEvent event) throws IOException {
        Parent stat = FXMLLoader.load(getClass().getResource("fxml/statistics.fxml"));
        Scene scene = new Scene(stat);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Statistics");
        stage.getIcons().add(new Image(new FileInputStream("src/assets/images/icons/graph.png")));
        stage.setScene(scene);
        stage.show();
    }



    @FXML
    private Pane chartPane;


    @FXML
    public void getStatistics(ActionEvent actionEvent){
        String[] persons = {"PERSON1", "PERSON2","PERSON3","PERSON4","PERSON5","PERSON6", "PERSON7", "PERSON8", "PERSON9", "PERSON10",
                "PERSON11", "PERSON12","PERSON13","PERSON14","PERSON15","PERSON16", "PERSON17", "PERSON18", "PERSON19", "PERSON20",
                "PERSON21", "PERSON22","PERSON23","PERSON24","PERSON25","PERSON26", "PERSON27", "PERSON28", "PERSON29", "PERSON30",
                "PERSON31", "PERSON32","PERSON33","PERSON34","PERSON35","PERSON36", "PERSON37", "PERSON38", "PERSON39", "PERSON40"};
// populating axis
        CategoryAxis xAxis = new CategoryAxis();

        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Persons");
        yAxis.setLabel("Distances");

        BarChart barChart = new BarChart(xAxis, yAxis);
        barChart.setTitle("Distances of the input face");

        //get data
        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        try{

            for (int i = 0; i < persons.length; i++) {
                series.getData().add(new XYChart.Data<>(persons[i], distancesMap.get(persons[i])));

            }
            // add data to bar chart
            barChart.getData().add(series);
            barChart.setBarGap(2);
            chartPane.getChildren().add(barChart);
            barChart.setStyle("CHART_COLOR_1: rgb(2,0,94);");
        }catch (Exception e){
            alertMsg("You must make recognition first", Alert.AlertType.WARNING);
            e.printStackTrace();
        }
    }

    @FXML
    void trainOnMouseClicked(MouseEvent event) {
        MouseButton mouseButton = event.getButton();
        if (mouseButton == MouseButton.SECONDARY){
            showDialog("Train model", "" +
                    "Train model is an operation must be done before the recognizing process\n" +
                    "You can specify a threshold and percentage of precision level\n" +
                    "Note: this operation may take up to 30 seconds (see the online documentation)");
        }
    }
    @FXML
    void recognizeOnMouseClicked(MouseEvent event) {
        MouseButton mouseButton = event.getButton();
        if (mouseButton == MouseButton.SECONDARY){
            showDialog("Recognize", "Recognition is the process of " +
                    "testing if the input face is recognized by the system\n" +
                    "To do so upload the input face and hid the recognition button \n" +
                    "Note: model must be trained before");
        }
    }

    @FXML
    void saveOnMouseClicked(MouseEvent event) {
        MouseButton mouseButton = event.getButton();
        if (mouseButton == MouseButton.SECONDARY){
            showDialog("Save Training", "Save button is used to save the configurations of the last" +
                    "training\n" +
                    "Once you you save the training you can load it and make recognition at any time");
        }
    }
    @FXML
    void loadOnMouseClicked(MouseEvent event) {
        MouseButton mouseButton = event.getButton();
        if (mouseButton == MouseButton.SECONDARY){
            showDialog("Load Training", "Load training helps to load the last training configuration");
        }
    }

    @FXML
    void trainOnKeyPressed(KeyEvent event) {
        switch (event.getCode()){
            case T:trainModel(null);break;
            case R:recognize(null);break;
            case S:saveTrainState(null);break;
            case L:loadTrainState(null);break;
        }
    }


    @FXML
    private StackPane backgroundStackPane;

    // this method is for showing dialog message
    public void showDialog(String heading ,String content){
        // layout of the dialog
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Text(heading));
        dialogLayout.setBody(new Text(content));

        // create the dialog to be shown
        JFXDialog dialog = new JFXDialog(backgroundStackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);

        // set the button
        JFXButton button = new JFXButton();
        button.setText("Close");
        button.setStyle("-fx-text-fill: #2eabff; -fx-font-weight: bold");

        JFXButton help = new JFXButton("Go online");
        help.setStyle("-fx-text-fill: #2eabff; -fx-font-weight: bold");

        button.setOnAction(actionEvent -> dialog.close());

        help.setOnAction(actionEvent -> {
            try {
                getOnlineHelp(new URI("www.google.com"));
                dialog.close();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });

        dialogLayout.setActions(button, help);
        leftPane.toBack();
        dialog.show();
    }

    public void getOnlineHelp(URI uri){
        try {
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }

            desktop.browse(uri);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}