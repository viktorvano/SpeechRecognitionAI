package com.ViktorVano.SpeechRecognitionAI.GUI;

import com.ViktorVano.SpeechRecognitionAI.Tables.Webhooks.Webhook;
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
import static com.ViktorVano.SpeechRecognitionAI.Tables.Webhooks.WebhooksFile.saveWebhooks;

public class WebhookSettings {
    private int webhookIndex = -1;
    private Button buttonUpdateWebhook;
    private Button buttonRemoveWebhook;

    private Button buttonAddWebhook;
    private TextField txtEditWord;
    private TextField txtEditURL;

    private final TextField txtNewWord;
    private TextField txtNewURL;

    public WebhookSettings(Stage stageReference, ObservableList<Webhook> webhooksDatabase, ListView<String> webhooksList)
    {
        final int dialogWidth = 1200;
        final int dialogHeight = 620;
        final Stage dialog = new Stage();
        dialog.setTitle("Webhooks");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stageReference);
        BorderPane borderPane = new BorderPane();

        Color background = new Color(
                ((double)background_red)/255.0,
                ((double)background_green)/255.0,
                ((double)background_blue)/255.0,
                1.0);
        borderPane.setBackground(new Background(new BackgroundFill(background, null, null)));

        ObservableList<String> webhookItems = FXCollections.observableArrayList();
        for (Webhook webhook : webhooksDatabase)
            webhookItems.add(webhook.word + "\t\t-->\t\t" + webhook.url);
        webhooksList.setItems(webhookItems);
        webhooksList.setOnMouseClicked(event -> {
            if(webhooksList.getSelectionModel().getSelectedIndex() != -1)
            {
                webhookIndex = webhooksList.getSelectionModel().getSelectedIndex();
                buttonRemoveWebhook.setDisable(false);
                String[] strings = webhooksList.getItems().get(webhookIndex).split("\t\t-->\t\t");
                txtEditWord.setText(strings[0]);
                txtEditURL.setText(strings[1]);
            }else
            {
                webhookIndex = -1;
                buttonRemoveWebhook.setDisable(true);
                txtEditWord.setText("");
                txtEditURL.setText("");
                buttonUpdateWebhook.setDisable(true);
            }
        });

        buttonRemoveWebhook = new Button("Remove Webhook");
        buttonRemoveWebhook.setDisable(webhookIndex == -1);
        buttonRemoveWebhook.setOnAction(event -> {
            if(webhooksList.getSelectionModel().getSelectedIndex() != -1)
            {
                webhooksList.getItems().remove(webhookIndex);
                webhooksDatabase.remove(webhookIndex);
                webhookIndex = webhooksList.getSelectionModel().getSelectedIndex();
                if(webhookIndex == -1)
                {
                    txtEditWord.setText("");
                    txtEditURL.setText("");
                    buttonUpdateWebhook.setDisable(true);
                    buttonRemoveWebhook.setDisable(true);
                }else {
                    txtEditWord.setText(webhooksDatabase.get(webhookIndex).word);
                    txtEditURL.setText(webhooksDatabase.get(webhookIndex).url);
                    buttonUpdateWebhook.setDisable(false);
                    buttonRemoveWebhook.setDisable(false);
                }
                saveWebhooks(webhooksDatabase);
            }
        });

        Label labelNewWebhook = new Label("\n New Webhook \n\n");
        labelNewWebhook.setFont(Font.font("Arial", 20));
        labelNewWebhook.setStyle("-fx-font-weight: bold");

        txtNewWord = new TextField();
        txtNewWord.setPromptText("Word/Phrase");
        txtNewWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWebhook.setDisable(txtNewWord.getText().length() == 0 || txtNewURL.getText().length() == 0));

        txtNewURL = new TextField();
        txtNewURL.setPromptText("IP Address/URL");
        txtNewURL.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWebhook.setDisable(txtNewWord.getText().length() == 0 || txtNewURL.getText().length() == 0));

        buttonAddWebhook = new Button("Add Webhook");
        buttonAddWebhook.setDisable(true);
        buttonAddWebhook.setOnAction(event -> {
            Webhook tempWebhook = new Webhook();
            tempWebhook.word = txtNewWord.getText();
            tempWebhook.url = txtNewURL.getText();
            txtNewWord.setText("");
            txtNewURL.setText("");
            String tempString =  tempWebhook.word + "\t\t-->\t\t" + tempWebhook.url;
            webhooksList.getItems().add(tempString);
            webhooksDatabase.add(webhooksDatabase.size(), tempWebhook);
            saveWebhooks(webhooksDatabase);
        });

        Label labelEditWebhook = new Label("\n Edit Webhook \n\n");
        labelEditWebhook.setFont(Font.font("Arial", 20));
        labelEditWebhook.setStyle("-fx-font-weight: bold");

        txtEditWord = new TextField();
        txtEditWord.setPromptText("Word/Phrase");
        txtEditWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWebhook.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditURL.getText().length() == 0 ||
                        webhookIndex == -1));

        txtEditURL = new TextField();
        txtEditURL.setPromptText("IP Address/URL");
        txtEditURL.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWebhook.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditURL.getText().length() == 0 ||
                        webhookIndex == -1));

        buttonUpdateWebhook = new Button("Update Webhook");
        buttonUpdateWebhook.setDisable(true);
        buttonUpdateWebhook.setOnAction(event -> {
            Webhook tempWebhook = new Webhook();
            tempWebhook.word = txtEditWord.getText();
            tempWebhook.url = txtEditURL.getText();
            buttonUpdateWebhook.setDisable(true);
            buttonRemoveWebhook.setDisable(true);
            txtEditWord.setText("");
            txtEditURL.setText("");
            String tempString =  tempWebhook.word + "\t\t-->\t\t" + tempWebhook.url;
            webhooksList.getItems().set(webhookIndex, tempString);
            webhooksDatabase.set(webhookIndex, tempWebhook);
            saveWebhooks(webhooksDatabase);
        });

        StackPane stackPaneCenter = new StackPane();
        stackPaneCenter.getChildren().add(webhooksList);

        VBox vBoxRight = new VBox();
        vBoxRight.getChildren().add(labelNewWebhook);
        vBoxRight.getChildren().add(txtNewWord);
        vBoxRight.getChildren().add(txtNewURL);
        vBoxRight.getChildren().add(buttonAddWebhook);
        vBoxRight.getChildren().add(labelEditWebhook);
        vBoxRight.getChildren().add(txtEditWord);
        vBoxRight.getChildren().add(txtEditURL);
        vBoxRight.getChildren().add(buttonUpdateWebhook);
        vBoxRight.getChildren().add(buttonRemoveWebhook);

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
