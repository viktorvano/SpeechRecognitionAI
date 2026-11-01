package com.ViktorVano.SpeechRecognitionAI.GUI;

import com.ViktorVano.SpeechRecognitionAI.Tables.Orchestrator.OrchestratorWorker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Parameters.*;
import static com.ViktorVano.SpeechRecognitionAI.Tables.Orchestrator.OrchestratorFile.saveOrchestratorWorkers;

public class OrchestratorSettings {
    private int workerIndex = -1;
    private Button buttonAddWorker, buttonUpdateWorker, buttonRemoveWorker;
    private Label labelNewWorker, labelEditWorker;
    private TextField txtNewWorker, txtNewAddress, txtNewPort;
    private TextField txtEditWorker, txtEditAddress, txtEditPort;
    public OrchestratorSettings(
            Stage stageReference,
            ObservableList<OrchestratorWorker> orchestratorWorkerDatabase,
            ListView<String> workersList)
    {
        final int dialogWidth = 1200;
        final int dialogHeight = 620;
        final Stage dialog = new Stage();
        dialog.setTitle("Orchestrator Workers");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stageReference);
        BorderPane borderPane = new BorderPane();

        Color background = new Color(
                ((double)background_red)/255.0,
                ((double)background_green)/255.0,
                ((double)background_blue)/255.0,
                1.0);
        borderPane.setBackground(new Background(new BackgroundFill(background, null, null)));

        ObservableList<String> workerItem = FXCollections.observableArrayList();
        for (OrchestratorWorker worker : orchestratorWorkerDatabase)
            workerItem.add(worker.name + "\t\t\t" + worker.address + " : " + worker.port);
        workersList.setItems(workerItem);
        workersList.setOnMouseClicked(event -> {
            if(workersList.getSelectionModel().getSelectedIndex() != -1)
            {
                workerIndex = workersList.getSelectionModel().getSelectedIndex();
                buttonRemoveWorker.setDisable(false);
                String[] strings = workersList.getItems().get(workerIndex).split("\t\t\t");
                txtEditWorker.setText(strings[0]);
                strings = strings[1].split(" : ");
                txtEditAddress.setText(strings[0]);
                txtEditPort.setText(strings[1]);
            }else
            {
                workerIndex = -1;
                buttonRemoveWorker.setDisable(true);
                txtEditWorker.setText("");
                txtEditAddress.setText("");
                txtEditPort.setText("");
                buttonUpdateWorker.setDisable(true);
            }
        });

        buttonRemoveWorker = new Button("Remove Worker");
        buttonRemoveWorker.setDisable(workerIndex == -1);
        buttonRemoveWorker.setOnAction(event -> {
            if(workersList.getSelectionModel().getSelectedIndex() != -1)
            {
                workersList.getItems().remove(workerIndex);
                orchestratorWorkerDatabase.remove(workerIndex);
                workerIndex = workersList.getSelectionModel().getSelectedIndex();
                if(workerIndex == -1)
                {
                    txtEditWorker.setText("");
                    txtEditAddress.setText("");
                    txtEditPort.setText("");
                    buttonUpdateWorker.setDisable(true);
                    buttonRemoveWorker.setDisable(true);
                }else {
                    txtEditWorker.setText(orchestratorWorkerDatabase.get(workerIndex).name);
                    txtEditAddress.setText(orchestratorWorkerDatabase.get(workerIndex).address);
                    txtEditPort.setText(orchestratorWorkerDatabase.get(workerIndex).port);
                    buttonUpdateWorker.setDisable(false);
                    buttonRemoveWorker.setDisable(false);
                }
                saveOrchestratorWorkers(orchestratorWorkerDatabase);
            }
        });

        labelNewWorker = new Label("\n New\n Worker \n\n");
        labelNewWorker.setFont(Font.font("Arial", 20));
        labelNewWorker.setStyle("-fx-font-weight: bold");

        txtNewWorker = new TextField();
        txtNewWorker.setPromptText("Worker Name");
        txtNewWorker.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWorker.setDisable(txtNewWorker.getText().length() == 0 ||
                        txtNewAddress.getText().length() == 0 || txtNewPort.getText().length() == 0));

        txtNewAddress = new TextField();
        txtNewAddress.setPromptText("IP Address/URL");
        txtNewAddress.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWorker.setDisable(txtNewWorker.getText().length() == 0 ||
                        txtNewAddress.getText().length() == 0 || txtNewPort.getText().length() == 0));

        txtNewPort = new TextField();
        txtNewPort.setPromptText("Port");
        txtNewPort.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWorker.setDisable(txtNewWorker.getText().length() == 0 ||
                        txtNewAddress.getText().length() == 0 || txtNewPort.getText().length() == 0));

        buttonAddWorker = new Button("Add Worker");
        buttonAddWorker.setDisable(true);
        buttonAddWorker.setOnAction(event -> {
            OrchestratorWorker tempWorker = new OrchestratorWorker();
            tempWorker.name = txtNewWorker.getText();
            tempWorker.address = txtNewAddress.getText();
            tempWorker.port = txtNewPort.getText();
            txtNewWorker.setText("");
            txtNewAddress.setText("");
            txtNewPort.setText("");
            String tempString =  tempWorker.name + "\t\t\t" +
                    tempWorker.address + " : " + tempWorker.port;
            workersList.getItems().add(tempString);
            orchestratorWorkerDatabase.add(orchestratorWorkerDatabase.size(), tempWorker);
            saveOrchestratorWorkers(orchestratorWorkerDatabase);
        });

        labelEditWorker = new Label("\n Edit\n Worker \n\n");
        labelEditWorker.setFont(Font.font("Arial", 20));
        labelEditWorker.setStyle("-fx-font-weight: bold");

        txtEditWorker = new TextField();
        txtEditWorker.setPromptText("Worker Name");
        txtEditWorker.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWorker.setDisable(txtEditWorker.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 || txtEditPort.getText().length() == 0 ||
                        workerIndex == -1));

        txtEditAddress = new TextField();
        txtEditAddress.setPromptText("IP Address/URL");
        txtEditAddress.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWorker.setDisable(txtEditWorker.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 || txtEditPort.getText().length() == 0 ||
                        workerIndex == -1));

        txtEditPort = new TextField();
        txtEditPort.setPromptText("Port");
        txtEditPort.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWorker.setDisable(txtEditWorker.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 || txtEditPort.getText().length() == 0 ||
                        workerIndex == -1));

        buttonUpdateWorker = new Button("Update Worker");
        buttonUpdateWorker.setDisable(true);
        buttonUpdateWorker.setOnAction(event -> {
            OrchestratorWorker tempWorker = new OrchestratorWorker();
            tempWorker.name = txtEditWorker.getText();
            tempWorker.address = txtEditAddress.getText();
            tempWorker.port = txtEditPort.getText();
            buttonUpdateWorker.setDisable(true);
            buttonRemoveWorker.setDisable(true);
            txtEditWorker.setText("");
            txtEditAddress.setText("");
            txtEditPort.setText("");
            String tempString =  tempWorker.name + "\t\t\t" +
                    tempWorker.address + " : " + tempWorker.port;
            workersList.getItems().set(workerIndex, tempString);
            orchestratorWorkerDatabase.set(workerIndex, tempWorker);
            saveOrchestratorWorkers(orchestratorWorkerDatabase);
        });

        StackPane stackPaneCenter = new StackPane();
        stackPaneCenter.getChildren().add(workersList);

        VBox vBoxRight = new VBox();
        vBoxRight.getChildren().add(labelNewWorker);
        vBoxRight.getChildren().add(txtNewWorker);
        vBoxRight.getChildren().add(txtNewAddress);
        vBoxRight.getChildren().add(txtNewPort);
        vBoxRight.getChildren().add(buttonAddWorker);
        vBoxRight.getChildren().add(labelEditWorker);
        vBoxRight.getChildren().add(txtEditWorker);
        vBoxRight.getChildren().add(txtEditAddress);
        vBoxRight.getChildren().add(txtEditPort);
        vBoxRight.getChildren().add(buttonUpdateWorker);
        vBoxRight.getChildren().add(buttonRemoveWorker);

        borderPane.setCenter(stackPaneCenter);
        borderPane.setRight(vBoxRight);
        Scene dialogScene = new Scene(borderPane, dialogWidth, dialogHeight);
        dialog.setMinWidth(borderPane.getWidth());
        dialog.setMinHeight(borderPane.getHeight());
        dialog.setResizable(true);
        dialog.setScene(dialogScene);
        dialog.show();

        try
        {
            Image icon = new Image(getClass().getResourceAsStream("../images/icon3.png"));
            dialog.getIcons().add(icon);
            System.out.println("Icon loaded from IDE...");
        }catch(Exception e)
        {
            try
            {
                Image icon = new Image("com/ViktorVano/SpeechRecognitionAI/images/icon3.png");
                dialog.getIcons().add(icon);
                System.out.println("Icon loaded from exported JAR...");
            }catch(Exception e1)
            {
                System.out.println("Icon failed to load...");
            }
        }
    }
}
