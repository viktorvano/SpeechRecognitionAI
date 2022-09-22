package com.ViktorVano.SpeechRecognitionAI.GUI;

import com.ViktorVano.SpeechRecognitionAI.Miscellaneous.ShellCommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.ShellCommandsFile.saveShellCommands;

public class ShellCommandsSettings {
    private int shellCommandIndex = -1;
    private Button buttonUpdateWebhook;
    private Button buttonRemoveShellCommand;

    private Button buttonAddWebhook;
    private TextField txtEditWord;
    private TextField txtEditCommand;

    private final TextField txtNewWord;
    private TextField txtNewCommand;

    public ShellCommandsSettings(Stage stageReference, ObservableList<ShellCommand> shellCommandsDatabase, ListView<String> shellCommandsList)
    {
        final int dialogWidth = 1200;
        final int dialogHeight = 620;
        final Stage dialog = new Stage();
        dialog.setTitle("Shell Commands");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stageReference);
        BorderPane borderPane = new BorderPane();

        ObservableList<String> webhookItems = FXCollections.observableArrayList();
        for (ShellCommand shellCommand : shellCommandsDatabase)
            webhookItems.add(shellCommand.word + "\t\t-->\t\t" + shellCommand.command);
        shellCommandsList.setItems(webhookItems);
        shellCommandsList.setOnMouseClicked(event -> {
            if(shellCommandsList.getSelectionModel().getSelectedIndex() != -1)
            {
                shellCommandIndex = shellCommandsList.getSelectionModel().getSelectedIndex();
                buttonRemoveShellCommand.setDisable(false);
                String[] strings = shellCommandsList.getItems().get(shellCommandIndex).split("\t\t-->\t\t");
                txtEditWord.setText(strings[0]);
                txtEditCommand.setText(strings[1]);
            }else
            {
                shellCommandIndex = -1;
                buttonRemoveShellCommand.setDisable(true);
                txtEditWord.setText("");
                txtEditCommand.setText("");
                buttonUpdateWebhook.setDisable(true);
            }
        });

        buttonRemoveShellCommand = new Button("Remove Shell Command");
        buttonRemoveShellCommand.setDisable(shellCommandIndex == -1);
        buttonRemoveShellCommand.setOnAction(event -> {
            if(shellCommandsList.getSelectionModel().getSelectedIndex() != -1)
            {
                shellCommandsList.getItems().remove(shellCommandIndex);
                shellCommandsDatabase.remove(shellCommandIndex);
                shellCommandIndex = shellCommandsList.getSelectionModel().getSelectedIndex();
                if(shellCommandIndex == -1)
                {
                    txtEditWord.setText("");
                    txtEditCommand.setText("");
                    buttonUpdateWebhook.setDisable(true);
                    buttonRemoveShellCommand.setDisable(true);
                }else {
                    txtEditWord.setText(shellCommandsDatabase.get(shellCommandIndex).word);
                    txtEditCommand.setText(shellCommandsDatabase.get(shellCommandIndex).command);
                    buttonUpdateWebhook.setDisable(false);
                    buttonRemoveShellCommand.setDisable(false);
                }
                saveShellCommands(shellCommandsDatabase);
            }
        });

        Label labelNewWebhook = new Label("\n New Webhook \n\n");
        labelNewWebhook.setFont(Font.font("Arial", 20));
        labelNewWebhook.setStyle("-fx-font-weight: bold");

        txtNewWord = new TextField();
        txtNewWord.setPromptText("Word/Phrase");
        txtNewWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWebhook.setDisable(txtNewWord.getText().length() == 0 || txtNewCommand.getText().length() == 0));

        txtNewCommand = new TextField();
        txtNewCommand.setPromptText("Shell Command");
        txtNewCommand.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWebhook.setDisable(txtNewWord.getText().length() == 0 || txtNewCommand.getText().length() == 0));

        buttonAddWebhook = new Button("Add Shell Command");
        buttonAddWebhook.setDisable(true);
        buttonAddWebhook.setOnAction(event -> {
            ShellCommand tempShellCommand = new ShellCommand();
            tempShellCommand.word = txtNewWord.getText();
            tempShellCommand.command = txtNewCommand.getText();
            txtNewWord.setText("");
            txtNewCommand.setText("");
            String tempString =  tempShellCommand.word + "\t\t-->\t\t" + tempShellCommand.command;
            shellCommandsList.getItems().add(tempString);
            shellCommandsDatabase.add(shellCommandsDatabase.size(), tempShellCommand);
            saveShellCommands(shellCommandsDatabase);
        });

        Label labelEditWebhook = new Label("\n Edit Webhook \n\n");
        labelEditWebhook.setFont(Font.font("Arial", 20));
        labelEditWebhook.setStyle("-fx-font-weight: bold");

        txtEditWord = new TextField();
        txtEditWord.setPromptText("Word/Phrase");
        txtEditWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWebhook.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditCommand.getText().length() == 0 ||
                        shellCommandIndex == -1));

        txtEditCommand = new TextField();
        txtEditCommand.setPromptText("Shell Command");
        txtEditCommand.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWebhook.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditCommand.getText().length() == 0 ||
                        shellCommandIndex == -1));

        buttonUpdateWebhook = new Button("Update Shell Command");
        buttonUpdateWebhook.setDisable(true);
        buttonUpdateWebhook.setOnAction(event -> {
            ShellCommand tempShellCommand = new ShellCommand();
            tempShellCommand.word = txtEditWord.getText();
            tempShellCommand.command = txtEditCommand.getText();
            buttonUpdateWebhook.setDisable(true);
            buttonRemoveShellCommand.setDisable(true);
            txtEditWord.setText("");
            txtEditCommand.setText("");
            String tempString =  tempShellCommand.word + "\t\t-->\t\t" + tempShellCommand.command;
            shellCommandsList.getItems().set(shellCommandIndex, tempString);
            shellCommandsDatabase.set(shellCommandIndex, tempShellCommand);
            saveShellCommands(shellCommandsDatabase);
        });

        StackPane stackPaneCenter = new StackPane();
        stackPaneCenter.getChildren().add(shellCommandsList);

        VBox vBoxRight = new VBox();
        vBoxRight.getChildren().add(labelNewWebhook);
        vBoxRight.getChildren().add(txtNewWord);
        vBoxRight.getChildren().add(txtNewCommand);
        vBoxRight.getChildren().add(buttonAddWebhook);
        vBoxRight.getChildren().add(labelEditWebhook);
        vBoxRight.getChildren().add(txtEditWord);
        vBoxRight.getChildren().add(txtEditCommand);
        vBoxRight.getChildren().add(buttonUpdateWebhook);
        vBoxRight.getChildren().add(buttonRemoveShellCommand);

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
