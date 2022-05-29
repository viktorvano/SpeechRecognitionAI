package com.ViktorVano.SpeechRecognitionAI.GUI;

import com.ViktorVano.SpeechRecognitionAI.Miscellaneous.WordResponse;
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

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.WordResponsesFile.*;

public class WordResponseSettings {
    private int wordResponseIndex = -1;
    private Button buttonUpdateWordResponse;
    private Button buttonRemoveWordResponse;

    private Button buttonAddWordResponse;
    private TextField txtEditWord;
    private TextField txtEditResponse;

    private final TextField txtNewWord;
    private TextField txtNewResponse;

    public WordResponseSettings(Stage stageReference)
    {
        final int dialogWidth = 700;
        final int dialogHeight = 620;
        final Stage dialog = new Stage();
        dialog.setTitle("Word Responses");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stageReference);
        BorderPane borderPane = new BorderPane();

        ObservableList<WordResponse> wordResponsesDatabase = loadWordResponses();
        ListView<String> wordResponsesList = new ListView<>();
        ObservableList<String> wordResponseItem = FXCollections.observableArrayList();
        for (WordResponse wordResponse : wordResponsesDatabase)
            wordResponseItem.add(wordResponse.word + "\t\t-->\t\t" + wordResponse.response);
        wordResponsesList.setItems(wordResponseItem);
        wordResponsesList.setOnMouseClicked(event -> {
            if(wordResponsesList.getSelectionModel().getSelectedIndex() != -1)
            {
                wordResponseIndex = wordResponsesList.getSelectionModel().getSelectedIndex();
                buttonRemoveWordResponse.setDisable(false);
                String[] strings = wordResponsesList.getItems().get(wordResponseIndex).split("\t\t-->\t\t");
                txtEditWord.setText(strings[0]);
                txtEditResponse.setText(strings[1]);
            }else
            {
                wordResponseIndex = -1;
                buttonRemoveWordResponse.setDisable(true);
                txtEditWord.setText("");
                txtEditResponse.setText("");
                buttonUpdateWordResponse.setDisable(true);
            }
        });

        buttonRemoveWordResponse = new Button("Remove Word Response");
        buttonRemoveWordResponse.setDisable(wordResponseIndex == -1);
        buttonRemoveWordResponse.setOnAction(event -> {
            if(wordResponsesList.getSelectionModel().getSelectedIndex() != -1)
            {
                wordResponsesList.getItems().remove(wordResponseIndex);
                wordResponsesDatabase.remove(wordResponseIndex);
                wordResponseIndex = wordResponsesList.getSelectionModel().getSelectedIndex();
                buttonRemoveWordResponse.setDisable(wordResponseIndex == -1);
                if(wordResponseIndex == -1)
                {
                    txtEditWord.setText("");
                    txtEditResponse.setText("");
                    buttonUpdateWordResponse.setDisable(true);
                }
                saveWordResponses(wordResponsesDatabase);
            }
        });

        Label labelNewWordResponse = new Label("\n New Word Response \n\n");
        labelNewWordResponse.setFont(Font.font("Arial", 20));
        labelNewWordResponse.setStyle("-fx-font-weight: bold");

        txtNewWord = new TextField();
        txtNewWord.setPromptText("Word/Phrase");
        txtNewWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWordResponse.setDisable(txtNewWord.getText().length() == 0 ||
                        txtNewResponse.getText().length() == 0));

        txtNewResponse = new TextField();
        txtNewResponse.setPromptText("Response");
        txtNewResponse.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWordResponse.setDisable(txtNewWord.getText().length() == 0 ||
                        txtNewResponse.getText().length() == 0));

        buttonAddWordResponse = new Button("Add Word Response");
        buttonAddWordResponse.setDisable(true);
        buttonAddWordResponse.setOnAction(event -> {
            WordResponse tempWordResponse = new WordResponse();
            tempWordResponse.word = txtNewWord.getText();
            tempWordResponse.response = txtNewResponse.getText();
            txtNewWord.setText("");
            txtNewResponse.setText("");
            String tempString =  tempWordResponse.word + "\t\t-->\t\t" +
                    tempWordResponse.response;
            wordResponsesList.getItems().add(tempString);
            wordResponsesDatabase.add(wordResponsesDatabase.size(), tempWordResponse);
            saveWordResponses(wordResponsesDatabase);
        });

        Label labelEditWordResponse = new Label("\n Edit Word Response \n\n");
        labelEditWordResponse.setFont(Font.font("Arial", 20));
        labelEditWordResponse.setStyle("-fx-font-weight: bold");

        txtEditWord = new TextField();
        txtEditWord.setPromptText("Word/Phrase");
        txtEditWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWordResponse.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditResponse.getText().length() == 0 ||
                        wordResponseIndex == -1));

        txtEditResponse = new TextField();
        txtEditResponse.setPromptText("Response");
        txtEditResponse.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWordResponse.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditResponse.getText().length() == 0 ||
                        wordResponseIndex == -1));

        buttonUpdateWordResponse = new Button("Update Word Response");
        buttonUpdateWordResponse.setDisable(true);
        buttonUpdateWordResponse.setOnAction(event -> {
            WordResponse tempWordResponse = new WordResponse();
            tempWordResponse.word = txtEditWord.getText();
            tempWordResponse.response = txtEditResponse.getText();
            txtEditWord.setText("");
            txtEditResponse.setText("");
            String tempString =  tempWordResponse.word + "\t\t-->\t\t" +
                    tempWordResponse.response;
            wordResponsesList.getItems().set(wordResponseIndex, tempString);
            wordResponsesDatabase.set(wordResponseIndex, tempWordResponse);
            saveWordResponses(wordResponsesDatabase);
        });

        StackPane stackPaneCenter = new StackPane();
        stackPaneCenter.getChildren().add(wordResponsesList);

        VBox vBoxRight = new VBox();
        vBoxRight.getChildren().add(labelNewWordResponse);
        vBoxRight.getChildren().add(txtNewWord);
        vBoxRight.getChildren().add(txtNewResponse);
        vBoxRight.getChildren().add(buttonAddWordResponse);
        vBoxRight.getChildren().add(labelEditWordResponse);
        vBoxRight.getChildren().add(txtEditWord);
        vBoxRight.getChildren().add(txtEditResponse);
        vBoxRight.getChildren().add(buttonUpdateWordResponse);
        vBoxRight.getChildren().add(buttonRemoveWordResponse);

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
