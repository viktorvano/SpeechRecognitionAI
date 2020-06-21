package com.viktorvano.SpeechRecognitionAI.GUI;

import com.viktorvano.SpeechRecognitionAI.Audio.AudioCapture;
import com.viktorvano.SpeechRecognitionAI.Audio.RecordedAudio;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;


public class SpeechRecognitionAI extends Application {
    private AudioCapture audioCapture;
    private RecordedAudio recordedAudio;
    private XYChart.Series<Number, Number> displayedSeries, detectedWordsSeries;
    private Timeline timelineUpdateData;
    private boolean updateData = true;
    BorderPane borderPane = new BorderPane();
    StackPane stackPaneCenter = new StackPane();
    StackPane stackPaneRight = new StackPane();
    FlowPane flow = new FlowPane();
    HBox hBoxBottom = new HBox();
    ObservableList<RecordedAudio> recordedAudios;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        audioCapture = new AudioCapture();
        recordedAudio = new RecordedAudio();

        final int width = 1200;
        final int height = 750;

        borderPane.setBottom(hBoxBottom);
        borderPane.setCenter(stackPaneCenter);
        borderPane.setRight(stackPaneRight);
        borderPane.setLeft(flow);

        flow.setPadding(new Insets(30, 20, 30, 20));
        flow.setVgap(4);
        flow.setHgap(4);
        flow.setPrefWrapLength(30); // preferred width allows for two columns
        flow.setStyle("-fx-background-color: DAE6F3;");

        ImageView[] icons = new ImageView[4];
        Label[] labelMenu = new Label[4];
        for (int i=0; i<4; i++) {
            icons[i] = new ImageView(new Image("com\\viktorvano\\SpeechRecognitionAI\\images\\icon"+(i+1)+".jpg"));
            icons[i].setPreserveRatio(true);
            icons[i].setFitWidth(96);
            icons[i].setFitHeight(96);
            labelMenu[i] = new Label();
            flow.getChildren().add(icons[i]);
            flow.getChildren().add(labelMenu[i]);
        }
        labelMenu[0].setText("Training Data\n ");
        labelMenu[1].setText("Train AI\n ");
        labelMenu[2].setText("Speech Recognition\n ");
        labelMenu[3].setText("Settings");
        labelMenu[0].setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("training data label clicked....");
            }
        });

        recordedAudios = FXCollections.observableArrayList();
        ListView<String> list = new ListView<String>();
        ObservableList<String> items = FXCollections.observableArrayList();
        list.setItems(items);
        for(int i=0; i<60; i++)
        {
            recordedAudios.add(new RecordedAudio());
            recordedAudios.get(i).name = randomString() + " " + i;
            items.add(recordedAudios.get(i).name);
        }
        stackPaneRight.getChildren().add(list);


        hBoxBottom.setPadding(new Insets(50, 12, 50, 12));
        hBoxBottom.setSpacing(30);
        hBoxBottom.setStyle("-fx-background-color: #336699;");

        initializeDataLayout();

        Scene scene = new Scene(borderPane, width, height);

        stage.setTitle("Speech Recognition AI");
        stage.setScene(scene);
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        Image icon =  new Image("com\\viktorvano\\SpeechRecognitionAI\\images\\neural-network-icon.jpg");
        stage.getIcons().add(icon);
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

    public String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
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

    private void initializeDataLayout()
    {
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

        timelineUpdateData = new Timeline(new KeyFrame(Duration.millis(5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (updateData && audioCapture.isAudioRecorded()) {
                    updateData = false;
                    recordedAudio.audioRecord = audioCapture.getRecord();
                    if (recordedAudio.audioRecord != null) {
                        displayedSeries.getData().clear();
                        recordedAudio.audioRecordLength = audioCapture.getRecordLength();
                        for (int i = 0; i < recordedAudio.audioRecordLength; i++)
                        {
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

    private void initializeTrainingLayout()
    {

    }

    private void initializeRecognitionLayout()
    {

    }

    private void initializeSettingsLayout()
    {

    }
}
