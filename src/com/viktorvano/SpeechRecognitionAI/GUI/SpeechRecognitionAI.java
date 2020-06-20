package com.viktorvano.SpeechRecognitionAI.GUI;

import com.viktorvano.SpeechRecognitionAI.Audio.AudioCapture;
import com.viktorvano.SpeechRecognitionAI.Audio.RecordedAudio;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;


public class SpeechRecognitionAI extends Application {
    private AudioCapture audioCapture;
    private RecordedAudio recordedAudio;
    private XYChart.Series<Number, Number> displayedSeries, detectedWordsSeries;
    private Timeline timelineUpdateData;
    private boolean updateData = true;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        audioCapture = new AudioCapture();
        recordedAudio = new RecordedAudio();

        final int width = 750;
        final int height = 750;

        BorderPane borderPane = new BorderPane();
        StackPane stackPaneCenter = new StackPane();
        HBox hBoxBottom = new HBox();
        //borderPane.setTop(hbox);
        borderPane.setBottom(hBoxBottom);
        borderPane.setCenter(stackPaneCenter);
        //borderPane.setRight(addFlowPane());
        //borderPane.setLeft();

        hBoxBottom.setPadding(new Insets(50, 12, 50, 12));
        hBoxBottom.setSpacing(30);
        hBoxBottom.setStyle("-fx-background-color: #336699;");

        Scene scene = new Scene(borderPane, width, height);

        stage.setTitle("Speech Recognition AI");
        stage.setScene(scene);
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        Image icon =  new Image("com\\viktorvano\\SpeechRecognitionAI\\images\\neural-network-icon.jpg");
        stage.getIcons().add(icon);

        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.setTitle("Audio Data");
        //defining a series
        displayedSeries = new XYChart.Series<Number, Number>();
        displayedSeries.setName("Recorded Audio");
        detectedWordsSeries = new XYChart.Series<Number, Number>();
        detectedWordsSeries.setName("Detected Words");
        lineChart.setCreateSymbols(false);
        //populating the series with data
        lineChart.getData().add(displayedSeries);
        lineChart.getData().add(detectedWordsSeries);
        lineChart.setAnimated(false);
        stackPaneCenter.getChildren().add(lineChart);


        Button Play = new Button("Play");
        Play.setLayoutX(200);
        Play.setLayoutY(400);
        Play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(recordedAudio != null && recordedAudio.audioRecord != null)
                {
                    ReplayThread replayThread = new ReplayThread();
                    replayThread.start();
                }
            }
        });
        hBoxBottom.getChildren().add(Play);

        Button Listen = new Button("Listen");
        Listen.setLayoutX(300);
        Listen.setLayoutY(400);
        Listen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(audioCapture.isAudioRecorded())
                {
                    audioCapture.clearRecord();
                    recordedAudio.audioRecord = null;
                    recordedAudio.audioRecordLength = 0;
                    updateData = true;
                }
            }
        });
        hBoxBottom.getChildren().add(Listen);

        timelineUpdateData = new Timeline(new KeyFrame(Duration.millis(5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (updateData && audioCapture.isAudioRecorded()) {
                    updateData = false;
                    recordedAudio.audioRecord = audioCapture.getRecord();
                    if (recordedAudio.audioRecord != null) {
                        displayedSeries.getData().clear();
                        recordedAudio.audioRecordLength = audioCapture.getRecordLength();
                        for (int i = 0; i < recordedAudio.audioRecordLength; i++) {
                            if (i % 10 == 0)
                                displayedSeries.getData().add(new XYChart.Data<Number, Number>(i, recordedAudio.audioRecord[i]));
                        }
                        detectWords();
                    }
                }
            }
        }));
        timelineUpdateData.setCycleCount(Timeline.INDEFINITE);
        timelineUpdateData.play();
    }

    private void detectWords()
    {
        detectedWordsSeries.getData().clear();
        for (int i = 0; i < recordedAudio.audioRecordLength; i++) {
            if (i % 10 == 0 && Math.abs(recordedAudio.audioRecord[i]) > 75)
                detectedWordsSeries.getData().add(new XYChart.Data<Number, Number>(i, 100));
            else if (i % 10 == 0)
                detectedWordsSeries.getData().add(new XYChart.Data<Number, Number>(i, 0));
        }
    }

    @Override
    public void stop()
    {
        System.out.println("Leaving the app...");
        System.exit(0);
    }

    class ReplayThread extends Thread
    {
        @Override
        public void run() {
            super.run();
            audioCapture.playRecord(recordedAudio);
        }
    }
}
