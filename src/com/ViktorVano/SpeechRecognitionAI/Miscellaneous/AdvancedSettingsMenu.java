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

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.IntegerFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.PlotNeuralChartsFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.PrintToConsoleFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;

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
        textFieldStartRecording.setPromptText(Integer.toString(recorderThreshold));
        textFieldStartRecording.setText(Integer.toString(recorderThreshold));
        textFieldStartRecording.setLayoutX(200);
        textFieldStartRecording.setLayoutY(45);
        textFieldStartRecording.setPrefWidth(60);
        textFieldStartRecording.textProperty().addListener(observable -> {
            if(textFieldStartRecording.getText().length() > 0)
            try{
                int value = Integer.parseInt(textFieldStartRecording.getText());
                recorderThreshold = value;
                saveIntegerToFile("recorderThreshold.dat", value);
            }catch (Exception e)
            {
                textFieldStartRecording.setText("");
            }
        });
        dialogPane.getChildren().add(textFieldStartRecording);

        TextField textFieldWordThreshold = new TextField();
        textFieldWordThreshold.setPromptText(Integer.toString(wordThreshold));
        textFieldWordThreshold.setText(Integer.toString(wordThreshold));
        textFieldWordThreshold.setLayoutX(200);
        textFieldWordThreshold.setLayoutY(86);
        textFieldWordThreshold.setPrefWidth(60);
        textFieldWordThreshold.textProperty().addListener(observable -> {
            if(textFieldWordThreshold.getText().length() > 0)
            try{
                int value = Integer.parseInt(textFieldWordThreshold.getText());
                wordThreshold = value;
                saveIntegerToFile("wordThreshold.dat", value);
            }catch (Exception e)
            {
                textFieldWordThreshold.setText("");
            }
        });
        dialogPane.getChildren().add(textFieldWordThreshold);

        TextField textFieldPreWordSamples = new TextField();
        textFieldPreWordSamples.setPromptText(Integer.toString(preWordSamples));
        textFieldPreWordSamples.setText(Integer.toString(preWordSamples));
        textFieldPreWordSamples.setLayoutX(200);
        textFieldPreWordSamples.setLayoutY(127);
        textFieldPreWordSamples.setPrefWidth(60);
        textFieldPreWordSamples.textProperty().addListener(observable -> {
            if(textFieldPreWordSamples.getText().length() > 0)
            try{
                int value = Integer.parseInt(textFieldPreWordSamples.getText());
                preWordSamples = value;
                saveIntegerToFile("preWordSamples.dat", value);
            }catch (Exception e)
            {
                textFieldPreWordSamples.setText("");
            }
        });
        dialogPane.getChildren().add(textFieldPreWordSamples);

        TextField textFieldWordInertiaSamples = new TextField();
        textFieldWordInertiaSamples.setPromptText(Integer.toString(wordInertiaSamples));
        textFieldWordInertiaSamples.setText(Integer.toString(wordInertiaSamples));
        textFieldWordInertiaSamples.setLayoutX(200);
        textFieldWordInertiaSamples.setLayoutY(168);
        textFieldWordInertiaSamples.setPrefWidth(60);
        textFieldWordInertiaSamples.textProperty().addListener(observable -> {
            if(textFieldWordInertiaSamples.getText().length() > 0)
            try{
                int value = Integer.parseInt(textFieldWordInertiaSamples.getText());
                wordInertiaSamples = value;
                saveIntegerToFile("wordInertiaSamples.dat", value);
            }catch (Exception e)
            {
                textFieldWordInertiaSamples.setText("");
            }
        });
        dialogPane.getChildren().add(textFieldWordInertiaSamples);

        TextField textFieldWordInertiaThreshold = new TextField();
        textFieldWordInertiaThreshold.setPromptText(Integer.toString(wordInertiaThreshold));
        textFieldWordInertiaThreshold.setText(Integer.toString(wordInertiaThreshold));
        textFieldWordInertiaThreshold.setLayoutX(200);
        textFieldWordInertiaThreshold.setLayoutY(209);
        textFieldWordInertiaThreshold.setPrefWidth(60);
        textFieldWordInertiaThreshold.textProperty().addListener(observable -> {
            if(textFieldWordInertiaThreshold.getText().length() > 0)
            try{
                int value = Integer.parseInt(textFieldWordInertiaThreshold.getText());
                wordInertiaThreshold = value;
                saveIntegerToFile("wordInertiaSamples.dat", value);
            }catch (Exception e)
            {
                textFieldWordInertiaThreshold.setText("");
            }
        });
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

        plotNeuralCharts = loadPlotNeuralCharts();
        CheckBox checkBoxPlotNeuralCharts = new CheckBox("Plot Neural Network Charts");
        checkBoxPlotNeuralCharts.setSelected(plotNeuralCharts);
        checkBoxPlotNeuralCharts.setOnAction(event -> {
            plotNeuralCharts = checkBoxPlotNeuralCharts.isSelected();
            savePlotNeuralCharts(plotNeuralCharts);
        });
        checkBoxPlotNeuralCharts.setLayoutX(30);
        checkBoxPlotNeuralCharts.setLayoutY(361);
        dialogPane.getChildren().add(checkBoxPlotNeuralCharts);
    }
}
