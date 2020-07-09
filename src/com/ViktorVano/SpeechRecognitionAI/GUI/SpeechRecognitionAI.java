package com.ViktorVano.SpeechRecognitionAI.GUI;

import com.ViktorVano.SpeechRecognitionAI.Audio.AudioCapture;
import com.ViktorVano.SpeechRecognitionAI.Audio.AudioPlayer;
import com.ViktorVano.SpeechRecognitionAI.Audio.RecordedAudio;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.TextField;
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
    private final BorderPane borderPane = new BorderPane();
    private final StackPane stackPaneCenter = new StackPane();
    private final VBox vBoxRight = new VBox();
    private final FlowPane flow = new FlowPane();
    private final HBox hBoxBottom = new HBox();
    private ObservableList<RecordedAudio> database, records;
    private ListView<String> databaseList, recordsList;
    private ObservableList<String> databaseItem, recordItem;
    private final int minWordLength = 3000, maxWordLength = 32000;
    private TextField txtDetectedWord, txtDatabaseWord;
    private int recordedWordIndex = -1, databaseWordIndex = -1;

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
        borderPane.setRight(vBoxRight);
        borderPane.setLeft(flow);

        flow.setPadding(new Insets(30, 20, 30, 20));
        flow.setVgap(4);
        flow.setHgap(4);
        flow.setPrefWrapLength(30); // preferred width allows for two columns
        flow.setStyle("-fx-background-color: DAE6F3;");

        ImageView[] icons = new ImageView[4];
        Label[] labelMenu = new Label[4];
        for (int i=0; i<4; i++)
        {
            icons[i] = new ImageView(new Image("/com/ViktorVano/SpeechRecognitionAI/images/icon"+(i+1)+".png"));
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

        hBoxBottom.setPadding(new Insets(15, 50, 15, 50));
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
        int lastValue = 50;
        for (int i = 0; i < recordedAudio.audioRecordLength; i++)
        {
            if (lastValue != 100 && Math.abs(recordedAudio.audioRecord[i]) > 75)
            {
                if(i>=500)
                {
                    detectedWordsSeries.getData().add(new XYChart.Data<Number, Number>(i-500, 0));
                    detectedWordsSeries.getData().add(new XYChart.Data<Number, Number>(i-499, 100));
                }else
                {
                    detectedWordsSeries.getData().add(new XYChart.Data<Number, Number>(0, 100));
                }
                lastValue = 100;
            }
            else if (lastValue != 0 && Math.abs(recordedAudio.audioRecord[i]) <= 75)
            {
                if(i-1 >= 0)
                    detectedWordsSeries.getData().add(new XYChart.Data<Number, Number>(i-1, 100));
                detectedWordsSeries.getData().add(new XYChart.Data<Number, Number>(i, 0));
                lastValue = 0;
            }

            boolean valueTheSame = true;

            while(valueTheSame)
            {
                valueTheSame = false;
                int x=i;
                for(; x<recordedAudio.audioRecordLength && x<(300+i); x++)
                {
                    if(lastValue == 100 && Math.abs(recordedAudio.audioRecord[x]) > 30)
                        valueTheSame = true;
                }

                if(valueTheSame)
                    i = x;
            }

            if(i == recordedAudio.audioRecordLength-1)
                detectedWordsSeries.getData().add(new XYChart.Data<Number, Number>(i, 0));

        }
        int word = 0;
        for(int i=0; i<detectedWordsSeries.getData().size(); i++)
        {
            //System.out.println(detectedWordsSeries.getData().get(i).toString());
            if(i < detectedWordsSeries.getData().size()-1
            && detectedWordsSeries.getData().get(i).getYValue().intValue() == 100
            && detectedWordsSeries.getData().get(i+1).getYValue().intValue() == 100)
            {
                System.out.println(detectedWordsSeries.getData().get(i).getXValue().toString() + " " +
                        detectedWordsSeries.getData().get(i+1).getXValue().toString());
                int start = detectedWordsSeries.getData().get(i).getXValue().intValue();
                int end = detectedWordsSeries.getData().get(i+1).getXValue().intValue();
                int length = end - start + 1;

                if(length < minWordLength)
                {
                    detectedWordsSeries.getData().remove(i-1, i+2);
                    System.out.println("Removing a short word: " + length);
                    i--;
                }else if(length > maxWordLength)
                {
                    detectedWordsSeries.getData().remove(i-1, i+2);
                    System.out.println("Removing a long word: " + length);
                    i--;
                }else
                {
                    word++;
                    System.out.println("Word length: " + length);
                    RecordedAudio tempRecord = new RecordedAudio();
                    tempRecord.audioRecordLength = length;
                    tempRecord.name = "word" + word;
                    tempRecord.audioRecord = new byte[length];
                    for(int x=start; x<=end; x++)
                    {
                        tempRecord.audioRecord[x - start] = recordedAudio.audioRecord[x + 1];
                    }
                    records.add(tempRecord);
                    recordItem.add(tempRecord.name);
                }
            }
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

    private void initializeDataLayout()
    {
        Button Play = new Button("Play Record");
        Play.setPrefHeight(100);
        Play.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(recordedAudio != null && recordedAudio.audioRecord != null)
                {
                    AudioPlayer audioPlayer = new AudioPlayer(audioCapture, recordedAudio);
                    audioPlayer.start();
                }
            }
        });
        hBoxBottom.getChildren().add(Play);

        Button Record = new Button("Record Audio");
        Record.setPrefHeight(100);
        Record.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(audioCapture.isAudioRecorded())
                {
                    audioCapture.clearRecord();
                    recordItem.clear();
                    records.clear();
                    recordedWordIndex = -1;
                    recordedAudio.audioRecord = null;
                    recordedAudio.audioRecordLength = 0;
                    updateData = true;
                }
            }
        });
        hBoxBottom.getChildren().add(Record);

        database = FXCollections.observableArrayList();
        databaseList = new ListView<String>();
        databaseItem = FXCollections.observableArrayList();
        databaseList.setItems(databaseItem);
        databaseList.setPrefHeight(500);
        databaseList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(databaseList.getSelectionModel().getSelectedIndex() != -1)
                {
                    databaseWordIndex = databaseList.getSelectionModel().getSelectedIndex();
                    txtDatabaseWord.setText(database.get(databaseWordIndex).name);
                }
            }
        });
        vBoxRight.getChildren().add(databaseList);

        Button buttonPlayDatabaseWord = new Button("Play");
        buttonPlayDatabaseWord.setStyle("-fx-font-size:30");
        buttonPlayDatabaseWord.setPrefWidth(250);
        buttonPlayDatabaseWord.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(recordedAudio != null && recordedAudio.audioRecord != null && databaseWordIndex !=-1)
                {
                    AudioPlayer audioPlayer = new AudioPlayer(audioCapture, database.get(databaseWordIndex));
                    audioPlayer.start();
                }
            }
        });
        vBoxRight.getChildren().add(buttonPlayDatabaseWord);

        Button buttonRemoveDatabaseWord = new Button("Remove");
        vBoxRight.getChildren().add(buttonRemoveDatabaseWord);

        txtDatabaseWord = new TextField();
        txtDatabaseWord.setPromptText("Name a word");
        txtDatabaseWord.setPrefWidth(150);
        txtDatabaseWord.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(databaseWordIndex != -1)
                {
                    database.get(databaseWordIndex).name = txtDatabaseWord.getText();
                    databaseList.getItems().set(databaseWordIndex, database.get(databaseWordIndex).name);
                }
            }
        });
        vBoxRight.getChildren().add(txtDatabaseWord);

        records = FXCollections.observableArrayList();
        recordsList = new ListView<String>();
        recordItem = FXCollections.observableArrayList();
        recordsList.setItems(recordItem);
        recordsList.setPrefHeight(100);
        recordsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(recordsList.getSelectionModel().getSelectedIndex() != -1)
                {
                    recordedWordIndex = recordsList.getSelectionModel().getSelectedIndex();
                    txtDetectedWord.setText(records.get(recordedWordIndex).name);
                }
            }
        });
        hBoxBottom.getChildren().add(recordsList);

        txtDetectedWord = new TextField();
        txtDetectedWord.setPromptText("Name a word");
        txtDetectedWord.setPrefWidth(150);
        txtDetectedWord.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(recordedWordIndex != -1)
                {
                    records.get(recordedWordIndex).name = txtDetectedWord.getText();
                    recordsList.getItems().set(recordedWordIndex, records.get(recordedWordIndex).name);
                }
            }
        });
        hBoxBottom.getChildren().add(txtDetectedWord);

        Button PlayWord = new Button("Play");
        PlayWord.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(recordedAudio != null && recordedAudio.audioRecord != null && recordedWordIndex !=-1)
                {
                    AudioPlayer audioPlayer = new AudioPlayer(audioCapture, records.get(recordedWordIndex));
                    audioPlayer.start();
                }
            }
        });
        hBoxBottom.getChildren().add(PlayWord);

        Button RemoveWord = new Button("Remove");
        RemoveWord.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(recordedWordIndex != -1)
                {
                    txtDetectedWord.setText("");
                    recordsList.getItems().remove(recordedWordIndex);
                    records.remove(recordedWordIndex);
                    recordedWordIndex = -1;
                }
            }
        });
        hBoxBottom.getChildren().add(RemoveWord);

        Button AddWord = new Button("Add to Database");
        AddWord.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(recordedWordIndex != -1)
                {
                    database.add(records.get(recordedWordIndex));
                    databaseItem.add(records.get(recordedWordIndex).name);
                    txtDetectedWord.setText("");
                    recordsList.getItems().remove(recordedWordIndex);
                    records.remove(recordedWordIndex);
                    recordedWordIndex = -1;
                    database.get(database.size()-1).name = databaseItem.get(databaseItem.size()-1);
                }
            }
        });
        hBoxBottom.getChildren().add(AddWord);

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

        timelineUpdateData = new Timeline(new KeyFrame(Duration.millis(50), new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {

                if (updateData && audioCapture.isAudioRecorded())
                {
                    updateData = false;
                    recordedAudio.audioRecord = audioCapture.getRecord();
                    if (recordedAudio.audioRecord != null)
                    {
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
