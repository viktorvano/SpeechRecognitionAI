package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.PrintToConsoleFile.loadPrintToConsole;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.PrintToConsoleFile.savePrintToConsole;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.printNetworkValues;

public class AdvancedSettingsMenu {
    public AdvancedSettingsMenu(Stage stageReference)
    {
        final int dialogWidth = 900;
        final int dialogHeight = 600;
        final Stage dialog = new Stage();
        dialog.setTitle("Advanced Settings");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stageReference);
        Pane dialogPane = new Pane();
        Scene dialogScene = new Scene(dialogPane, dialogWidth, dialogHeight);
        dialog.setMinWidth(dialogPane.getWidth());
        dialog.setMinHeight(dialogPane.getHeight());
        dialog.setResizable(false);
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

        Label labelWordDetection =  new Label("Word Detection");
        labelWordDetection.setLayoutX(30);
        labelWordDetection.setLayoutY(10);
        labelWordDetection.setFont(Font.font("Arial", 22));
        labelWordDetection.setStyle("-fx-font-weight: bold");
        dialogPane.getChildren().add(labelWordDetection);

        Label labelsForWordDetection = new Label(
                "Start Recording\n\n" +
                    "Word Threshold\n\n" +
                    "Pre-Word Samples\n\n" +
                    "Word Inertia Samples\n\n" +
                    "Word Inertia Threshold");
        labelsForWordDetection.setLayoutX(30);
        labelsForWordDetection.setLayoutY(50);
        dialogPane.getChildren().add(labelsForWordDetection);

        TextField textFieldStartRecording = new TextField();
        textFieldStartRecording.setPromptText("500");
        textFieldStartRecording.setLayoutX(200);
        textFieldStartRecording.setLayoutY(45);
        textFieldStartRecording.setPrefWidth(60);
        dialogPane.getChildren().add(textFieldStartRecording);

        TextField textFieldWordThreshold = new TextField();
        textFieldWordThreshold.setPromptText("500");
        textFieldWordThreshold.setLayoutX(200);
        textFieldWordThreshold.setLayoutY(86);
        textFieldWordThreshold.setPrefWidth(60);
        dialogPane.getChildren().add(textFieldWordThreshold);

        TextField textFieldPreWordSamples = new TextField();
        textFieldPreWordSamples.setPromptText("1200");
        textFieldPreWordSamples.setLayoutX(200);
        textFieldPreWordSamples.setLayoutY(127);
        textFieldPreWordSamples.setPrefWidth(60);
        dialogPane.getChildren().add(textFieldPreWordSamples);

        TextField textFieldWordInertiaSamples = new TextField();
        textFieldWordInertiaSamples.setPromptText("250");
        textFieldWordInertiaSamples.setLayoutX(200);
        textFieldWordInertiaSamples.setLayoutY(168);
        textFieldWordInertiaSamples.setPrefWidth(60);
        dialogPane.getChildren().add(textFieldWordInertiaSamples);

        TextField textFieldWordInertiaThreshold = new TextField();
        textFieldWordInertiaThreshold.setPromptText("300");
        textFieldWordInertiaThreshold.setLayoutX(200);
        textFieldWordInertiaThreshold.setLayoutY(209);
        textFieldWordInertiaThreshold.setPrefWidth(60);
        dialogPane.getChildren().add(textFieldWordInertiaThreshold);

        Label labelOtherSettings =  new Label("Other Settings");
        labelOtherSettings.setLayoutX(30);
        labelOtherSettings.setLayoutY(291);
        labelOtherSettings.setFont(Font.font("Arial", 22));
        labelOtherSettings.setStyle("-fx-font-weight: bold");
        dialogPane.getChildren().add(labelOtherSettings);

        printNetworkValues = loadPrintToConsole();
        CheckBox checkBoxPrintToConsole = new CheckBox("Print to console");
        checkBoxPrintToConsole.setSelected(printNetworkValues);
        checkBoxPrintToConsole.setOnAction(event -> {
            printNetworkValues = checkBoxPrintToConsole.isSelected();
            savePrintToConsole(printNetworkValues);
        });
        checkBoxPrintToConsole.setLayoutX(30);
        checkBoxPrintToConsole.setLayoutY(331);
        dialogPane.getChildren().add(checkBoxPrintToConsole);
    }
}
