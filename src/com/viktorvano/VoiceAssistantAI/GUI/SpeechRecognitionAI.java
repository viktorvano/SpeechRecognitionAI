package com.viktorvano.VoiceAssistantAI.GUI;

import com.viktorvano.VoiceAssistantAI.Audio.AudioCapture;
import com.viktorvano.VoiceAssistantAI.Audio.RecordedAudio;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SpeechRecognitionAI extends Application {
    private AudioCapture audioCapture;
    private RecordedAudio recordedAudio;
    private XYChart.Series<Number, Number> displayedSeries;
    private Timeline timelineUpdateData;

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
        Pane pane = new Pane();
        Scene scene = new Scene(pane, width, height);

        stage.setTitle("Speech Recognition AI");
        stage.setScene(scene);
        stage.show();
        stage.setMaxWidth(stage.getWidth());
        stage.setMinWidth(stage.getWidth());
        stage.setMaxHeight(stage.getHeight());
        stage.setMinHeight(stage.getHeight());
        stage.setResizable(false);

        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        //creating the chart
        final LineChart<Number,Number> lineChart = new LineChart<Number,Number>(xAxis,yAxis);

        lineChart.setTitle("Audio Data");
        //defining a series
        displayedSeries = new XYChart.Series<Number, Number>();
        displayedSeries.setName("Recorded Audio");
        lineChart.setCreateSymbols(false);
        //populating the series with data
        lineChart.getData().add(displayedSeries);
        lineChart.setAnimated(false);
        pane.getChildren().add(lineChart);


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
        pane.getChildren().add(Play);

        Button Clear = new Button("Clear");
        Clear.setLayoutX(300);
        Clear.setLayoutY(400);
        Clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(audioCapture.isAudioRecorded())
                {
                    audioCapture.clearRecord();
                    recordedAudio.audioRecord = null;
                    recordedAudio.audioRecordLength = 0;
                }
            }
        });
        pane.getChildren().add(Clear);

        //TODO: Try to use Threads.
        Timeline timelineMain = new Timeline(new KeyFrame(Duration.millis(500), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)
            {
                try {
                    Thread.sleep(400);
                }catch (Exception e)
                {
                    System.out.println("Main timeline had a problem.");
                }
                audioCapture.playRecord(recordedAudio);
                audioCapture.clearRecord();// activates listening again
            }
        }));
        timelineMain.setCycleCount(1);

        timelineUpdateData = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (audioCapture.isAudioRecorded()) {
                    recordedAudio.audioRecord = audioCapture.getRecord();
                    if (recordedAudio.audioRecord != null) {
                        displayedSeries.getData().clear();
                        recordedAudio.audioRecordLength = audioCapture.getRecordLength();
                        for (int i = 0; i < recordedAudio.audioRecordLength; i++) {
                            if (i % 10 == 0)
                                displayedSeries.getData().add(new XYChart.Data<Number, Number>(i, recordedAudio.audioRecord[i]));
                        }
                    }
                }
            }
        }));
        timelineUpdateData.setCycleCount(1);

        Timeline timelineCheckData = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (audioCapture.isAudioRecorded()) {
                    timelineUpdateData.play();
                    //timelineMain.play();
                }
            }
        }));
        timelineCheckData.setCycleCount(Timeline.INDEFINITE);
        timelineCheckData.play();
        /*CheckData checkData = new CheckData();
        checkData.start();*/
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
            //audioCapture.clearRecord();// activates listening again
        }
    }
}
