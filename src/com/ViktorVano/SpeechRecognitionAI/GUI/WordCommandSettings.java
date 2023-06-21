package com.ViktorVano.SpeechRecognitionAI.GUI;

import com.ViktorVano.SpeechRecognitionAI.Tables.Commands.WordCommand;
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
import static com.ViktorVano.SpeechRecognitionAI.Tables.Commands.WordCommandsFile.saveWordCommands;

public class WordCommandSettings {
    private int wordCommandIndex = -1;
    private Button buttonUpdateWordCommand;
    private Button buttonRemoveWordCommand;

    private Button buttonAddWordCommand;
    private TextField txtEditWord;
    private TextField txtEditCommand;
    private TextField txtEditAddress;
    private TextField txtEditPort;

    private final TextField txtNewWord;
    private TextField txtNewCommand;
    private TextField txtNewAddress;
    private TextField txtNewPort;

    public WordCommandSettings(Stage stageReference, ObservableList<WordCommand> wordCommandsDatabase, ListView<String> wordCommandsList)
    {
        final int dialogWidth = 1200;
        final int dialogHeight = 620;
        final Stage dialog = new Stage();
        dialog.setTitle("Word Commands");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stageReference);
        BorderPane borderPane = new BorderPane();

        Color background = new Color(
                ((double)background_red)/255.0,
                ((double)background_green)/255.0,
                ((double)background_blue)/255.0,
                1.0);
        borderPane.setBackground(new Background(new BackgroundFill(background, null, null)));

        ObservableList<String> wordCommandItems = FXCollections.observableArrayList();
        for (WordCommand wordCommand : wordCommandsDatabase)
            wordCommandItems.add(wordCommand.word + "\t\t-->\t\t" +
                    wordCommand.command + "\t\t-->\t\t" +
                    wordCommand.address + "\t\t-->\t\t" +
                    wordCommand.port);
        wordCommandsList.setItems(wordCommandItems);
        wordCommandsList.setOnMouseClicked(event -> {
            if(wordCommandsList.getSelectionModel().getSelectedIndex() != -1)
            {
                wordCommandIndex = wordCommandsList.getSelectionModel().getSelectedIndex();
                buttonRemoveWordCommand.setDisable(false);
                String[] strings = wordCommandsList.getItems().get(wordCommandIndex).split("\t\t-->\t\t");
                txtEditWord.setText(strings[0]);
                txtEditCommand.setText(strings[1]);
                txtEditAddress.setText(strings[2]);
                txtEditPort.setText(strings[3]);
            }else
            {
                wordCommandIndex = -1;
                buttonRemoveWordCommand.setDisable(true);
                txtEditWord.setText("");
                txtEditCommand.setText("");
                txtEditAddress.setText("");
                txtEditPort.setText("");
                buttonUpdateWordCommand.setDisable(true);
            }
        });

        buttonRemoveWordCommand = new Button("Remove Word Command");
        buttonRemoveWordCommand.setDisable(wordCommandIndex == -1);
        buttonRemoveWordCommand.setOnAction(event -> {
            if(wordCommandsList.getSelectionModel().getSelectedIndex() != -1)
            {
                wordCommandsList.getItems().remove(wordCommandIndex);
                wordCommandsDatabase.remove(wordCommandIndex);
                wordCommandIndex = wordCommandsList.getSelectionModel().getSelectedIndex();
                if(wordCommandIndex == -1)
                {
                    txtEditWord.setText("");
                    txtEditCommand.setText("");
                    txtEditAddress.setText("");
                    txtEditPort.setText("");
                    buttonUpdateWordCommand.setDisable(true);
                    buttonRemoveWordCommand.setDisable(true);
                }else {
                    txtEditWord.setText(wordCommandsDatabase.get(wordCommandIndex).word);
                    txtEditCommand.setText(wordCommandsDatabase.get(wordCommandIndex).command);
                    txtEditAddress.setText(wordCommandsDatabase.get(wordCommandIndex).address);
                    txtEditPort.setText(wordCommandsDatabase.get(wordCommandIndex).port);
                    buttonUpdateWordCommand.setDisable(false);
                    buttonRemoveWordCommand.setDisable(false);
                }
                saveWordCommands(wordCommandsDatabase);
            }
        });

        Label labelNewWordCommand = new Label("\n New Word Command \n\n");
        labelNewWordCommand.setFont(Font.font("Arial", 20));
        labelNewWordCommand.setStyle("-fx-font-weight: bold");

        txtNewWord = new TextField();
        txtNewWord.setPromptText("Word/Phrase");
        txtNewWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWordCommand.setDisable(txtNewWord.getText().length() == 0 ||
                        txtNewCommand.getText().length() == 0 ||
                        txtNewAddress.getText().length() == 0 ||
                        txtNewPort.getText().length() == 0));

        txtNewCommand = new TextField();
        txtNewCommand.setPromptText("Command");
        txtNewCommand.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWordCommand.setDisable(txtNewWord.getText().length() == 0 ||
                        txtNewCommand.getText().length() == 0 ||
                        txtNewAddress.getText().length() == 0 ||
                        txtNewPort.getText().length() == 0));

        txtNewAddress = new TextField();
        txtNewAddress.setPromptText("IP Address/URL");
        txtNewAddress.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWordCommand.setDisable(txtNewWord.getText().length() == 0 ||
                        txtNewCommand.getText().length() == 0 ||
                        txtNewAddress.getText().length() == 0 ||
                        txtNewPort.getText().length() == 0));

        txtNewPort = new TextField();
        txtNewPort.setPromptText("Port");
        txtNewPort.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWordCommand.setDisable(txtNewWord.getText().length() == 0 ||
                        txtNewCommand.getText().length() == 0 ||
                        txtNewAddress.getText().length() == 0 ||
                        txtNewPort.getText().length() == 0));

        buttonAddWordCommand = new Button("Add Word Command");
        buttonAddWordCommand.setDisable(true);
        buttonAddWordCommand.setOnAction(event -> {
            WordCommand tempWordCommand = new WordCommand();
            tempWordCommand.word = txtNewWord.getText();
            tempWordCommand.command = txtNewCommand.getText();
            tempWordCommand.address = txtNewAddress.getText();
            tempWordCommand.port = txtNewPort.getText();
            txtNewWord.setText("");
            txtNewCommand.setText("");
            txtNewAddress.setText("");
            txtNewPort.setText("");
            String tempString =  tempWordCommand.word + "\t\t-->\t\t" +
                    tempWordCommand.command + "\t\t-->\t\t" +
                    tempWordCommand.address + "\t\t-->\t\t" +
                    tempWordCommand.port;
            wordCommandsList.getItems().add(tempString);
            wordCommandsDatabase.add(wordCommandsDatabase.size(), tempWordCommand);
            saveWordCommands(wordCommandsDatabase);
        });

        Label labelEditWordCommand = new Label("\n Edit Word Command \n\n");
        labelEditWordCommand.setFont(Font.font("Arial", 20));
        labelEditWordCommand.setStyle("-fx-font-weight: bold");

        txtEditWord = new TextField();
        txtEditWord.setPromptText("Word/Phrase");
        txtEditWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWordCommand.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditCommand.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 ||
                        txtEditPort.getText().length() == 0 ||
                        wordCommandIndex == -1));

        txtEditCommand = new TextField();
        txtEditCommand.setPromptText("Command");
        txtEditCommand.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWordCommand.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditCommand.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 ||
                        txtEditPort.getText().length() == 0 ||
                        wordCommandIndex == -1));

        txtEditAddress = new TextField();
        txtEditAddress.setPromptText("IP Address/URL");
        txtEditAddress.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWordCommand.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditCommand.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 ||
                        txtEditPort.getText().length() == 0 ||
                        wordCommandIndex == -1));

        txtEditPort = new TextField();
        txtEditPort.setPromptText("Port");
        txtEditPort.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWordCommand.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditCommand.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 ||
                        txtEditPort.getText().length() == 0 ||
                        wordCommandIndex == -1));

        buttonUpdateWordCommand = new Button("Update Word Command");
        buttonUpdateWordCommand.setDisable(true);
        buttonUpdateWordCommand.setOnAction(event -> {
            WordCommand tempWordCommand = new WordCommand();
            tempWordCommand.word = txtEditWord.getText();
            tempWordCommand.command = txtEditCommand.getText();
            tempWordCommand.address = txtEditAddress.getText();
            tempWordCommand.port = txtEditPort.getText();
            buttonUpdateWordCommand.setDisable(true);
            buttonRemoveWordCommand.setDisable(true);
            txtEditWord.setText("");
            txtEditCommand.setText("");
            txtEditAddress.setText("");
            txtEditPort.setText("");
            String tempString =  tempWordCommand.word + "\t\t-->\t\t" +
                    tempWordCommand.command + "\t\t-->\t\t" +
                    tempWordCommand.address + "\t\t-->\t\t" +
                    tempWordCommand.port;
            wordCommandsList.getItems().set(wordCommandIndex, tempString);
            wordCommandsDatabase.set(wordCommandIndex, tempWordCommand);
            saveWordCommands(wordCommandsDatabase);
        });

        StackPane stackPaneCenter = new StackPane();
        stackPaneCenter.getChildren().add(wordCommandsList);

        VBox vBoxRight = new VBox();
        vBoxRight.getChildren().add(labelNewWordCommand);
        vBoxRight.getChildren().add(txtNewWord);
        vBoxRight.getChildren().add(txtNewCommand);
        vBoxRight.getChildren().add(txtNewAddress);
        vBoxRight.getChildren().add(txtNewPort);
        vBoxRight.getChildren().add(buttonAddWordCommand);
        vBoxRight.getChildren().add(labelEditWordCommand);
        vBoxRight.getChildren().add(txtEditWord);
        vBoxRight.getChildren().add(txtEditCommand);
        vBoxRight.getChildren().add(txtEditAddress);
        vBoxRight.getChildren().add(txtEditPort);
        vBoxRight.getChildren().add(buttonUpdateWordCommand);
        vBoxRight.getChildren().add(buttonRemoveWordCommand);

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
