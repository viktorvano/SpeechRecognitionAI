package com.ViktorVano.SpeechRecognitionAI.GUI;

import com.ViktorVano.SpeechRecognitionAI.Tables.Orchestrator.OrchestratorWorker;
import com.ViktorVano.SpeechRecognitionAI.Tables.Routing.WordRouting;
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

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;
import static com.ViktorVano.SpeechRecognitionAI.Tables.Orchestrator.OrchestratorFile.saveOrchestratorWorkers;

public class OrchestratorSettings {
    private int wordRoutingIndex = -1;
    private Button buttonAddWordRouting, buttonUpdateWordRouting, buttonRemoveWordRouting;
    private Label labelNewWordRouting, labelEditWordRouting;
    private TextField txtNewWord, txtNewAddress, txtNewPort;
    private TextField txtEditWord, txtEditAddress, txtEditPort;
    public OrchestratorSettings(
            Stage stageReference,
            ObservableList<OrchestratorWorker> orchestratorWorkerDatabase,
            ListView<String> wordRoutingList)
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

        ObservableList<String> wordRoutingItem = FXCollections.observableArrayList();
        for (OrchestratorWorker wordRouting : orchestratorWorkerDatabase)
            wordRoutingItem.add(wordRouting.name + "\t\t\t" + wordRouting.address + " : " + wordRouting.port);
        wordRoutingList.setItems(wordRoutingItem);
        wordRoutingList.setOnMouseClicked(event -> {
            if(wordRoutingList.getSelectionModel().getSelectedIndex() != -1)
            {
                wordRoutingIndex = wordRoutingList.getSelectionModel().getSelectedIndex();
                buttonRemoveWordRouting.setDisable(false);
                String[] strings = wordRoutingList.getItems().get(wordRoutingIndex).split("\t\t\t");
                txtEditWord.setText(strings[0]);
                strings = strings[1].split(" : ");
                txtEditAddress.setText(strings[0]);
                txtEditPort.setText(strings[1]);
            }else
            {
                wordRoutingIndex = -1;
                buttonRemoveWordRouting.setDisable(true);
                txtEditWord.setText("");
                txtEditAddress.setText("");
                txtEditPort.setText("");
                buttonUpdateWordRouting.setDisable(true);
            }
        });

        buttonRemoveWordRouting = new Button("Remove Word Routing");
        buttonRemoveWordRouting.setDisable(wordRoutingIndex == -1);
        buttonRemoveWordRouting.setOnAction(event -> {
            if(wordRoutingList.getSelectionModel().getSelectedIndex() != -1)
            {
                wordRoutingList.getItems().remove(wordRoutingIndex);
                orchestratorWorkerDatabase.remove(wordRoutingIndex);
                wordRoutingIndex = wordRoutingList.getSelectionModel().getSelectedIndex();
                if(wordRoutingIndex == -1)
                {
                    txtEditWord.setText("");
                    txtEditAddress.setText("");
                    txtEditPort.setText("");
                    buttonUpdateWordRouting.setDisable(true);
                    buttonRemoveWordRouting.setDisable(true);
                }else {
                    txtEditWord.setText(orchestratorWorkerDatabase.get(wordRoutingIndex).name);
                    txtEditAddress.setText(orchestratorWorkerDatabase.get(wordRoutingIndex).address);
                    txtEditPort.setText(orchestratorWorkerDatabase.get(wordRoutingIndex).port);
                    buttonUpdateWordRouting.setDisable(false);
                    buttonRemoveWordRouting.setDisable(false);
                }
                saveOrchestratorWorkers(orchestratorWorkerDatabase);
            }
        });

        labelNewWordRouting = new Label("\n New\n Worker \n\n");
        labelNewWordRouting.setFont(Font.font("Arial", 20));
        labelNewWordRouting.setStyle("-fx-font-weight: bold");

        txtNewWord = new TextField();
        txtNewWord.setPromptText("Word/Phrase");
        txtNewWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWordRouting.setDisable(txtNewWord.getText().length() == 0 ||
                        txtNewAddress.getText().length() == 0 || txtNewPort.getText().length() == 0));

        txtNewAddress = new TextField();
        txtNewAddress.setPromptText("IP Address/URL");
        txtNewAddress.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWordRouting.setDisable(txtNewWord.getText().length() == 0 ||
                        txtNewAddress.getText().length() == 0 || txtNewPort.getText().length() == 0));

        txtNewPort = new TextField();
        txtNewPort.setPromptText("Port");
        txtNewPort.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWordRouting.setDisable(txtNewWord.getText().length() == 0 ||
                        txtNewAddress.getText().length() == 0 || txtNewPort.getText().length() == 0));

        buttonAddWordRouting = new Button("Add Worker");
        buttonAddWordRouting.setDisable(true);
        buttonAddWordRouting.setOnAction(event -> {
            OrchestratorWorker tempWordRouting = new OrchestratorWorker();
            tempWordRouting.name = txtNewWord.getText();
            tempWordRouting.address = txtNewAddress.getText();
            tempWordRouting.port = txtNewPort.getText();
            txtNewWord.setText("");
            txtNewAddress.setText("");
            txtNewPort.setText("");
            String tempString =  tempWordRouting.name + "\t\t\t" +
                    tempWordRouting.address + " : " + tempWordRouting.port;
            wordRoutingList.getItems().add(tempString);
            orchestratorWorkerDatabase.add(orchestratorWorkerDatabase.size(), tempWordRouting);
            saveOrchestratorWorkers(orchestratorWorkerDatabase);
        });

        labelEditWordRouting = new Label("\n Edit\n Worker \n\n");
        labelEditWordRouting.setFont(Font.font("Arial", 20));
        labelEditWordRouting.setStyle("-fx-font-weight: bold");

        txtEditWord = new TextField();
        txtEditWord.setPromptText("Worker Name");
        txtEditWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWordRouting.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 || txtEditPort.getText().length() == 0 ||
                        wordRoutingIndex == -1));

        txtEditAddress = new TextField();
        txtEditAddress.setPromptText("IP Address/URL");
        txtEditAddress.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWordRouting.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 || txtEditPort.getText().length() == 0 ||
                        wordRoutingIndex == -1));

        txtEditPort = new TextField();
        txtEditPort.setPromptText("Port");
        txtEditPort.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWordRouting.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 || txtEditPort.getText().length() == 0 ||
                        wordRoutingIndex == -1));

        buttonUpdateWordRouting = new Button("Update Worker");
        buttonUpdateWordRouting.setDisable(true);
        buttonUpdateWordRouting.setOnAction(event -> {
            OrchestratorWorker tempWordRouting = new OrchestratorWorker();
            tempWordRouting.name = txtEditWord.getText();
            tempWordRouting.address = txtEditAddress.getText();
            tempWordRouting.port = txtEditPort.getText();
            buttonUpdateWordRouting.setDisable(true);
            buttonRemoveWordRouting.setDisable(true);
            txtEditWord.setText("");
            txtEditAddress.setText("");
            txtEditPort.setText("");
            String tempString =  tempWordRouting.name + "\t\t\t" +
                    tempWordRouting.address + " : " + tempWordRouting.port;
            wordRoutingList.getItems().set(wordRoutingIndex, tempString);
            orchestratorWorkerDatabase.set(wordRoutingIndex, tempWordRouting);
            saveOrchestratorWorkers(orchestratorWorkerDatabase);
        });

        StackPane stackPaneCenter = new StackPane();
        stackPaneCenter.getChildren().add(wordRoutingList);

        VBox vBoxRight = new VBox();
        vBoxRight.getChildren().add(labelNewWordRouting);
        vBoxRight.getChildren().add(txtNewWord);
        vBoxRight.getChildren().add(txtNewAddress);
        vBoxRight.getChildren().add(txtNewPort);
        vBoxRight.getChildren().add(buttonAddWordRouting);
        vBoxRight.getChildren().add(labelEditWordRouting);
        vBoxRight.getChildren().add(txtEditWord);
        vBoxRight.getChildren().add(txtEditAddress);
        vBoxRight.getChildren().add(txtEditPort);
        vBoxRight.getChildren().add(buttonUpdateWordRouting);
        vBoxRight.getChildren().add(buttonRemoveWordRouting);

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
