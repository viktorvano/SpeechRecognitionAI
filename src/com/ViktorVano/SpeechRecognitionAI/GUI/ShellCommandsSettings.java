package com.ViktorVano.SpeechRecognitionAI.GUI;

import com.ViktorVano.SpeechRecognitionAI.Tables.ShellCommands.ShellCommand;
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
import static com.ViktorVano.SpeechRecognitionAI.Tables.ShellCommands.ShellCommandsFile.saveShellCommands;

public class ShellCommandsSettings {
    private int shellCommandIndex = -1;
    private Button buttonUpdateShellCommand;
    private Button buttonRemoveShellCommand;

    private Button buttonAddShellCommand;
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

        Color background = new Color(
                ((double)background_red)/255.0,
                ((double)background_green)/255.0,
                ((double)background_blue)/255.0,
                1.0);
        borderPane.setBackground(new Background(new BackgroundFill(background, null, null)));

        ObservableList<String> shellCommandItems = FXCollections.observableArrayList();
        for (ShellCommand shellCommand : shellCommandsDatabase)
            shellCommandItems.add(shellCommand.word + "\t\t-->\t\t" + shellCommand.command);
        shellCommandsList.setItems(shellCommandItems);
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
                buttonUpdateShellCommand.setDisable(true);
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
                    buttonUpdateShellCommand.setDisable(true);
                    buttonRemoveShellCommand.setDisable(true);
                }else {
                    txtEditWord.setText(shellCommandsDatabase.get(shellCommandIndex).word);
                    txtEditCommand.setText(shellCommandsDatabase.get(shellCommandIndex).command);
                    buttonUpdateShellCommand.setDisable(false);
                    buttonRemoveShellCommand.setDisable(false);
                }
                saveShellCommands(shellCommandsDatabase);
            }
        });

        Label labelNewShellCommand = new Label("\n New ShellCommand \n\n");
        labelNewShellCommand.setFont(Font.font("Arial", 20));
        labelNewShellCommand.setStyle("-fx-font-weight: bold");

        txtNewWord = new TextField();
        txtNewWord.setPromptText("Word/Phrase");
        txtNewWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddShellCommand.setDisable(txtNewWord.getText().length() == 0 || txtNewCommand.getText().length() == 0));

        txtNewCommand = new TextField();
        txtNewCommand.setPromptText("Shell Command");
        txtNewCommand.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddShellCommand.setDisable(txtNewWord.getText().length() == 0 || txtNewCommand.getText().length() == 0));

        buttonAddShellCommand = new Button("Add Shell Command");
        buttonAddShellCommand.setDisable(true);
        buttonAddShellCommand.setOnAction(event -> {
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

        Label labelEditShellCommand = new Label("\n Edit Shell Command \n\n");
        labelEditShellCommand.setFont(Font.font("Arial", 20));
        labelEditShellCommand.setStyle("-fx-font-weight: bold");

        txtEditWord = new TextField();
        txtEditWord.setPromptText("Word/Phrase");
        txtEditWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateShellCommand.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditCommand.getText().length() == 0 ||
                        shellCommandIndex == -1));

        txtEditCommand = new TextField();
        txtEditCommand.setPromptText("Shell Command");
        txtEditCommand.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateShellCommand.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditCommand.getText().length() == 0 ||
                        shellCommandIndex == -1));

        buttonUpdateShellCommand = new Button("Update Shell Command");
        buttonUpdateShellCommand.setDisable(true);
        buttonUpdateShellCommand.setOnAction(event -> {
            ShellCommand tempShellCommand = new ShellCommand();
            tempShellCommand.word = txtEditWord.getText();
            tempShellCommand.command = txtEditCommand.getText();
            buttonUpdateShellCommand.setDisable(true);
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
        vBoxRight.getChildren().add(labelNewShellCommand);
        vBoxRight.getChildren().add(txtNewWord);
        vBoxRight.getChildren().add(txtNewCommand);
        vBoxRight.getChildren().add(buttonAddShellCommand);
        vBoxRight.getChildren().add(labelEditShellCommand);
        vBoxRight.getChildren().add(txtEditWord);
        vBoxRight.getChildren().add(txtEditCommand);
        vBoxRight.getChildren().add(buttonUpdateShellCommand);
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
