package com.ViktorVano.SpeechRecognitionAI.GUI;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.BooleanFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.FloatFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.IntegerFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.StringFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;

public class AdvancedSettingsMenu {
    public AdvancedSettingsMenu(Stage stageReference)
    {
        final int dialogWidth = 700;
        final int dialogHeight = 640;
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

        ImageView imageView = new ImageView(new Image("/com/ViktorVano/SpeechRecognitionAI/images/word_detection.png"));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(350);
        imageView.setFitHeight(350);
        imageView.setLayoutX(300);
        imageView.setLayoutY(30);
        dialogPane.getChildren().add(imageView);

        Label labelWordDetection =  new Label("Word Detection");
        labelWordDetection.setLayoutX(30);
        labelWordDetection.setLayoutY(10);
        labelWordDetection.setFont(Font.font("Arial", 22));
        labelWordDetection.setStyle("-fx-font-weight: bold");
        dialogPane.getChildren().add(labelWordDetection);

        Label labelStartRecording = new Label("Start Recording");
        labelStartRecording.setLayoutX(30);
        labelStartRecording.setLayoutY(50);
        dialogPane.getChildren().add(labelStartRecording);

        TextField textFieldStartRecording = new TextField();
        textFieldStartRecording.setPromptText(Integer.toString(recorderThreshold));
        textFieldStartRecording.setText(Integer.toString(recorderThreshold));
        textFieldStartRecording.setLayoutX(200);
        textFieldStartRecording.setLayoutY(45);
        textFieldStartRecording.setPrefWidth(60);
        textFieldStartRecording.textProperty().addListener(observable -> {
            if(textFieldStartRecording.getText().length() > 0)
            {
                try{
                    int value = Integer.parseInt(textFieldStartRecording.getText());
                    if(value > 0)
                    {
                        saveIntegerToFile("recorderThreshold.dat", value);
                        recorderThreshold = value;
                    }else
                        textFieldStartRecording.setText("");
                }catch (Exception e)
                {
                    textFieldStartRecording.setText("");
                }
            }
        });
        dialogPane.getChildren().add(textFieldStartRecording);

        Label labelWordThreshold = new Label("Word Threshold");
        labelWordThreshold.setLayoutX(30);
        labelWordThreshold.setLayoutY(91);
        dialogPane.getChildren().add(labelWordThreshold);

        TextField textFieldWordThreshold = new TextField();
        textFieldWordThreshold.setPromptText(Integer.toString(wordThreshold));
        textFieldWordThreshold.setText(Integer.toString(wordThreshold));
        textFieldWordThreshold.setLayoutX(200);
        textFieldWordThreshold.setLayoutY(86);
        textFieldWordThreshold.setPrefWidth(60);
        textFieldWordThreshold.textProperty().addListener(observable -> {
            if(textFieldWordThreshold.getText().length() > 0)
            {
                try{
                    int value = Integer.parseInt(textFieldWordThreshold.getText());
                    if(value > 0)
                    {
                        saveIntegerToFile("wordThreshold.dat", value);
                        wordThreshold = value;
                    }else
                        textFieldWordThreshold.setText("");
                }catch (Exception e)
                {
                    textFieldWordThreshold.setText("");
                }
            }
        });
        dialogPane.getChildren().add(textFieldWordThreshold);

        Label labelPreWordSamples = new Label("Pre-Word Samples");
        labelPreWordSamples.setLayoutX(30);
        labelPreWordSamples.setLayoutY(132);
        dialogPane.getChildren().add(labelPreWordSamples);

        TextField textFieldPreWordSamples = new TextField();
        textFieldPreWordSamples.setPromptText(Integer.toString(preWordSamples));
        textFieldPreWordSamples.setText(Integer.toString(preWordSamples));
        textFieldPreWordSamples.setLayoutX(200);
        textFieldPreWordSamples.setLayoutY(127);
        textFieldPreWordSamples.setPrefWidth(60);
        textFieldPreWordSamples.textProperty().addListener(observable -> {
            if(textFieldPreWordSamples.getText().length() > 0)
            {
                try{
                    int value = Integer.parseInt(textFieldPreWordSamples.getText());
                    if(value > 0)
                    {
                        saveIntegerToFile("preWordSamples.dat", value);
                        preWordSamples = value;
                    }else
                        textFieldPreWordSamples.setText("");

                }catch (Exception e)
                {
                    textFieldPreWordSamples.setText("");
                }
            }
        });
        dialogPane.getChildren().add(textFieldPreWordSamples);

        Label labelWordInertiaThreshold = new Label("Word Inertia Threshold");
        labelWordInertiaThreshold.setLayoutX(30);
        labelWordInertiaThreshold.setLayoutY(173);
        dialogPane.getChildren().add(labelWordInertiaThreshold);

        TextField textFieldWordInertiaThreshold = new TextField();
        textFieldWordInertiaThreshold.setPromptText(Integer.toString(wordInertiaThreshold));
        textFieldWordInertiaThreshold.setText(Integer.toString(wordInertiaThreshold));
        textFieldWordInertiaThreshold.setLayoutX(200);
        textFieldWordInertiaThreshold.setLayoutY(168);
        textFieldWordInertiaThreshold.setPrefWidth(60);
        textFieldWordInertiaThreshold.textProperty().addListener(observable -> {
            if(textFieldWordInertiaThreshold.getText().length() > 0)
            {
                try{
                    int value = Integer.parseInt(textFieldWordInertiaThreshold.getText());
                    if(value > 0)
                    {
                        saveIntegerToFile("wordInertiaThreshold.dat", value);
                        wordInertiaThreshold = value;
                    }else
                        textFieldWordInertiaThreshold.setText("");
                }catch (Exception e)
                {
                    textFieldWordInertiaThreshold.setText("");
                }
            }
        });
        dialogPane.getChildren().add(textFieldWordInertiaThreshold);

        Label labelWordInertiaSamples = new Label("Word Inertia Samples");
        labelWordInertiaSamples.setLayoutX(30);
        labelWordInertiaSamples.setLayoutY(214);
        dialogPane.getChildren().add(labelWordInertiaSamples);

        TextField textFieldWordInertiaSamples = new TextField();
        textFieldWordInertiaSamples.setPromptText(Integer.toString(wordInertiaSamples));
        textFieldWordInertiaSamples.setText(Integer.toString(wordInertiaSamples));
        textFieldWordInertiaSamples.setLayoutX(200);
        textFieldWordInertiaSamples.setLayoutY(209);
        textFieldWordInertiaSamples.setPrefWidth(60);
        textFieldWordInertiaSamples.textProperty().addListener(observable -> {
            if(textFieldWordInertiaSamples.getText().length() > 0)
            {
                try{
                    int value = Integer.parseInt(textFieldWordInertiaSamples.getText());
                    if(value > 0)
                    {
                        saveIntegerToFile("wordInertiaSamples.dat", value);
                        wordInertiaSamples = value;
                    }else
                        textFieldWordInertiaSamples.setText("");
                }catch (Exception e)
                {
                    textFieldWordInertiaSamples.setText("");
                }
            }
        });
        dialogPane.getChildren().add(textFieldWordInertiaSamples);

        Label labelOtherSettings =  new Label("Other Settings");
        labelOtherSettings.setLayoutX(30);
        labelOtherSettings.setLayoutY(291);
        labelOtherSettings.setFont(Font.font("Arial", 22));
        labelOtherSettings.setStyle("-fx-font-weight: bold");
        dialogPane.getChildren().add(labelOtherSettings);

        CheckBox checkBoxPrintToConsole = new CheckBox("Print Neural Network Values To Console");
        checkBoxPrintToConsole.setSelected(printNetworkValues);
        checkBoxPrintToConsole.setOnAction(event -> {
            printNetworkValues = checkBoxPrintToConsole.isSelected();
            saveBooleanToFile("printNetworkValues.dat", printNetworkValues);
        });
        checkBoxPrintToConsole.setLayoutX(30);
        checkBoxPrintToConsole.setLayoutY(331);
        dialogPane.getChildren().add(checkBoxPrintToConsole);

        CheckBox checkBoxPlotNeuralCharts = new CheckBox("Plot Neural Network Charts");
        checkBoxPlotNeuralCharts.setSelected(plotNeuralCharts);
        checkBoxPlotNeuralCharts.setOnAction(event -> {
            plotNeuralCharts = checkBoxPlotNeuralCharts.isSelected();
            saveBooleanToFile("plotNeuralCharts.dat", plotNeuralCharts);
        });
        checkBoxPlotNeuralCharts.setLayoutX(30);
        checkBoxPlotNeuralCharts.setLayoutY(361);
        dialogPane.getChildren().add(checkBoxPlotNeuralCharts);

        CheckBox checkBoxPlotKeepLongWords = new CheckBox("Keep Long Words (But Trim Them)");
        checkBoxPlotKeepLongWords.setSelected(keepLongWords);
        checkBoxPlotKeepLongWords.setOnAction(event -> {
            keepLongWords = checkBoxPlotKeepLongWords.isSelected();
            saveBooleanToFile("keepLongWords.dat", keepLongWords);
        });
        checkBoxPlotKeepLongWords.setLayoutX(30);
        checkBoxPlotKeepLongWords.setLayoutY(391);
        dialogPane.getChildren().add(checkBoxPlotKeepLongWords);

        Label labelIpMic =  new Label("IP Mic App");
        labelIpMic.setLayoutX(30);
        labelIpMic.setLayoutY(440);
        labelIpMic.setFont(Font.font("Arial", 22));
        labelIpMic.setStyle("-fx-font-weight: bold");
        dialogPane.getChildren().add(labelIpMic);

        CheckBox checkBoxUseIpMic = new CheckBox("Use IP Microphone");
        checkBoxUseIpMic.setSelected(useIpMic);
        checkBoxUseIpMic.setDisable(useIpMicOnly);
        checkBoxUseIpMic.setOnAction(event -> {
            useIpMic = checkBoxUseIpMic.isSelected();
            saveBooleanToFile("useIpMic.dat", useIpMic);
        });
        checkBoxUseIpMic.setLayoutX(30);
        checkBoxUseIpMic.setLayoutY(480);
        dialogPane.getChildren().add(checkBoxUseIpMic);

        Label labelToken = new Label("Token");
        labelToken.setLayoutX(30);
        labelToken.setLayoutY(520);
        dialogPane.getChildren().add(labelToken);

        TextField textFieldToken = new TextField();
        textFieldToken.setPromptText(token);
        textFieldToken.setText(token);
        textFieldToken.setLayoutX(80);
        textFieldToken.setLayoutY(516);
        textFieldToken.setPrefWidth(240);
        textFieldToken.textProperty().addListener(observable -> {
            if(textFieldToken.getText().length() > 0)
                try{
                    String value = textFieldToken.getText();
                    if(value.length() > 0)
                    {
                        saveStringToFile("token.dat", value);
                        token = value;
                    }else
                        textFieldToken.setText("");
                }catch (Exception e)
                {
                    textFieldToken.setText("");
                }
        });
        dialogPane.getChildren().add(textFieldToken);

        Label labelMicPort = new Label("Audio Server Port\n(restart required)");
        labelMicPort.setLayoutX(30);
        labelMicPort.setLayoutY(560);
        dialogPane.getChildren().add(labelMicPort);

        Label labelTextPort = new Label("Text Server Port       " + (audioServerPort+1));
        labelTextPort.setLayoutX(30);
        labelTextPort.setLayoutY(595);
        dialogPane.getChildren().add(labelTextPort);

        TextField textFieldPort = new TextField();
        textFieldPort.setPromptText(Integer.toString(audioServerPort));
        textFieldPort.setText(Integer.toString(audioServerPort));
        textFieldPort.setLayoutX(160);
        textFieldPort.setLayoutY(556);
        textFieldPort.setPrefWidth(60);
        textFieldPort.textProperty().addListener(observable -> {
            if(textFieldPort.getText().length() > 0)
                try{
                    int value = Integer.parseInt(textFieldPort.getText());
                    if(value > 0 && value < 65535)
                    {
                        saveIntegerToFile("audioServerPort.dat", value);
                        audioServerPort = value;
                        labelTextPort.setText("Text Server Port       " + (audioServerPort+1));
                    }else
                        textFieldPort.setText("");
                }catch (Exception e)
                {
                    textFieldPort.setText("");
                }
        });
        dialogPane.getChildren().add(textFieldPort);

        Label labelNeuralNetwork =  new Label("Neural Network");
        labelNeuralNetwork.setLayoutX(350);
        labelNeuralNetwork.setLayoutY(370);
        labelNeuralNetwork.setFont(Font.font("Arial", 22));
        labelNeuralNetwork.setStyle("-fx-font-weight: bold");
        dialogPane.getChildren().add(labelNeuralNetwork);

        Label labelVelocity = new Label("Velocity\n" +
                                            "(Eta - [0.0..1.0] overall network training rate)");
        labelVelocity.setLayoutX(350);
        labelVelocity.setLayoutY(410);
        dialogPane.getChildren().add(labelVelocity);

        TextField textFieldVelocity = new TextField();
        textFieldVelocity.setPromptText(Float.toString(velocity));
        textFieldVelocity.setText(Float.toString(velocity));
        textFieldVelocity.setLayoutX(500);
        textFieldVelocity.setLayoutY(400);
        textFieldVelocity.setPrefWidth(60);
        textFieldVelocity.textProperty().addListener(observable -> {
            if(textFieldVelocity.getText().length() > 0)
                try{
                    if(textFieldVelocity.getText().length() > 2)
                    {
                        float value = Float.parseFloat(textFieldVelocity.getText());
                        if(value > 0.0f)
                        {
                            if(value > 1.0f)
                            {
                                value = 1.0f;
                                textFieldVelocity.setText(String.valueOf(value));
                            }
                            saveFloatToFile("velocity.dat", value);
                            velocity = value;
                        }else
                            textFieldVelocity.setText("");
                    }
                }catch (Exception e)
                {
                    textFieldVelocity.setText("");
                }
        });
        dialogPane.getChildren().add(textFieldVelocity);

        Label labelMomentum = new Label("Momentum\n" +
                                            "(Alpha - [0.0..n] multiplier of last weight change)");
        labelMomentum.setLayoutX(350);
        labelMomentum.setLayoutY(474);
        dialogPane.getChildren().add(labelMomentum);

        TextField textFieldMomentum = new TextField();
        textFieldMomentum.setPromptText(Float.toString(momentum));
        textFieldMomentum.setText(Float.toString(momentum));
        textFieldMomentum.setLayoutX(500);
        textFieldMomentum.setLayoutY(461);
        textFieldMomentum.setPrefWidth(60);
        textFieldMomentum.textProperty().addListener(observable -> {
            if(textFieldMomentum.getText().length() > 0)
                try{
                    if(textFieldMomentum.getText().length() > 2)
                    {
                        float value = Float.parseFloat(textFieldMomentum.getText());
                        if(value > 0.0f)
                        {
                            if(value > 1.0f)
                            {
                                value = 1.0f;
                                textFieldMomentum.setText(String.valueOf(value));
                            }
                            saveFloatToFile("momentum.dat", value);
                            momentum = value;
                        }else
                            textFieldMomentum.setText("");
                    }
                }catch (Exception e)
                {
                    textFieldMomentum.setText("");
                }
        });
        dialogPane.getChildren().add(textFieldMomentum);

        Label labelExitTrainingLoss = new Label("Exit Training Loss");
        labelExitTrainingLoss.setLayoutX(350);
        labelExitTrainingLoss.setLayoutY(536);
        dialogPane.getChildren().add(labelExitTrainingLoss);

        TextField textFieldExitTrainingLoss = new TextField();
        textFieldExitTrainingLoss.setPromptText(Float.toString(exitTrainingLoss));
        textFieldExitTrainingLoss.setText(Float.toString(exitTrainingLoss));
        textFieldExitTrainingLoss.setLayoutX(500);
        textFieldExitTrainingLoss.setLayoutY(532);
        textFieldExitTrainingLoss.setPrefWidth(60);
        textFieldExitTrainingLoss.textProperty().addListener(observable -> {
            if(textFieldExitTrainingLoss.getText().length() > 0)
                try{
                    if(textFieldExitTrainingLoss.getText().length() > 2)
                    {
                        float value = Float.parseFloat(textFieldExitTrainingLoss.getText());
                        if(value > 0.0f)
                        {
                            if(value > 1.0f)
                            {
                                value = 1.0f;
                                textFieldExitTrainingLoss.setText(String.valueOf(value));
                            }
                            saveFloatToFile("exitTrainingLoss.dat", value);
                            exitTrainingLoss = value;
                        }else
                            textFieldExitTrainingLoss.setText("");
                    }
                }catch (Exception e)
                {
                    textFieldExitTrainingLoss.setText("");
                }
        });
        dialogPane.getChildren().add(textFieldExitTrainingLoss);

        Label labelClassifierMatch = new Label("Classifier Match [%]");
        labelClassifierMatch.setLayoutX(350);
        labelClassifierMatch.setLayoutY(577);
        dialogPane.getChildren().add(labelClassifierMatch);

        TextField textFieldMatch = new TextField();
        textFieldMatch.setPromptText(Integer.toString((int)(classifierThreshold*100.0f)));
        textFieldMatch.setText(Integer.toString((int)(classifierThreshold*100.0f)));
        textFieldMatch.setLayoutX(500);
        textFieldMatch.setLayoutY(573);
        textFieldMatch.setPrefWidth(60);
        textFieldMatch.textProperty().addListener(observable -> {
            if(textFieldMatch.getText().length() > 0)
                try{
                    int value = Integer.parseInt(textFieldMatch.getText());
                    if(value >= 0)
                    {
                        if(value > 100)
                        {
                            value = 100;
                            textFieldMatch.setText(String.valueOf(value));
                        }
                        classifierThreshold = (float)value/100.0f;
                        saveFloatToFile("classifierThreshold.dat", classifierThreshold);
                    }else
                        textFieldMatch.setText("");
                }catch (Exception e)
                {
                    textFieldMatch.setText("");
                }
        });
        dialogPane.getChildren().add(textFieldMatch);
    }
}
