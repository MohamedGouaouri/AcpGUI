package sample;


import com.jfoenix.controls.JFXSlider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import kernel.Acp;
import kernel.Performance;
import kernel.Result;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class PerformanceController implements Initializable {

    Acp pca = Controller.getPCA();
    private boolean tableCreated = false;
    @FXML
    private Pane performancePane;
    private TableView performanceTable = new TableView();
    @FXML
    private JFXSlider percentageSlider;

    @FXML
    private JFXSlider thresholdSlider;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TableColumn<String, Double> percentageCol = new TableColumn<>("Percentage");
        percentageCol.setCellValueFactory(new PropertyValueFactory<>("percentage"));

        TableColumn<String, Double> thresholdCol = new TableColumn<>("Threshold");
        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("threshold"));

        TableColumn<String, Double> recognizedCol = new TableColumn<>("Recognized");
        recognizedCol.setCellValueFactory(new PropertyValueFactory<>("recognized"));

        TableColumn<String, Double> confusionCol = new TableColumn<>("Confusion");
        confusionCol.setCellValueFactory(new PropertyValueFactory<>("confusion"));

        TableColumn<String, Double> rejectedCol = new TableColumn<>("Rejected");
        rejectedCol.setCellValueFactory(new PropertyValueFactory<>("rejected"));

        performanceTable.getColumns().addAll(
                percentageCol,
                thresholdCol,
                recognizedCol,
                confusionCol,
                rejectedCol
        );


        performanceTable.setPrefSize(performancePane.getPrefWidth(), performancePane.getPrefHeight());
        performancePane.getChildren().add(performanceTable);



    }


    @FXML
    void getPerformance(ActionEvent event) {

        try{
            Map<Result,Integer> rate = Controller.getRate("src/sample/db/orl/", thresholdSlider.getValue());
            performanceTable.getItems().add(
                    new Performance(0.9, pca.getThreshold(), rate.get(Result.RECONNUE) * 100 / 200.0, rate.get(Result.CONFUSION) * 100/200.0, rate.get(Result.REJETE) * 100 / 200.0));

        }catch (Exception ignored){
            alertMsg("You must train the model first", Alert.AlertType.ERROR);
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
