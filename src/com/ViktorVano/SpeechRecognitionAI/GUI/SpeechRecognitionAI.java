package com.ViktorVano.SpeechRecognitionAI.GUI;

import com.ViktorVano.SpeechRecognitionAI.Audio.AudioCapture;
import com.ViktorVano.SpeechRecognitionAI.Audio.AudioPlayer;
import com.ViktorVano.SpeechRecognitionAI.Audio.RecordedAudio;
import com.ViktorVano.SpeechRecognitionAI.FFNN.NeuralNetworkThread;
import com.ViktorVano.SpeechRecognitionAI.FFNN.TrainingThread;
import com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Classifier;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.ViktorVano.SpeechRecognitionAI.Audio.AudioDatabase.loadDatabase;
import static com.ViktorVano.SpeechRecognitionAI.Audio.AudioDatabase.saveDatabase;
import static com.ViktorVano.SpeechRecognitionAI.FFNN.TopologyFile.loadTopology;
import static com.ViktorVano.SpeechRecognitionAI.FFNN.TopologyFile.saveTopology;
import static com.ViktorVano.SpeechRecognitionAI.FFNN.Variables.*;


public class SpeechRecognitionAI extends Application {
    private AudioCapture audioCapture;
    private RecordedAudio recordedAudio;
    private XYChart.Series<Number, Number> displayedSeries, detectedWordsSeries;
    private Timeline timelineUpdateData, timelineTrainingLabelUpdate;
    private boolean updateData = true, sameWordCount = false;
    private final BorderPane borderPane = new BorderPane();
    private final StackPane stackPaneCenter = new StackPane();
    private final VBox vBoxRight = new VBox();
    private final FlowPane flow = new FlowPane();
    private final HBox hBoxBottom = new HBox();
    private ObservableList<RecordedAudio> database, records;
    private ListView<String> databaseList, recordsList, trainingList, topologyList;
    private ObservableList<String> databaseItem, recordItem, trainingItem, topologyItem;
    private final int minWordLength = 2000, maxWordLength = 18000;
    private TextField txtDetectedWord, txtDatabaseWord, txtHiddenLayer;
    private int recordedWordIndex = -1, databaseWordIndex = -1;
    private LineChart<Number,Number> lineChart;
    private Button Play, Record, buttonPlayDatabaseWord, buttonRemoveDatabaseWord, PlayWord, RemoveWord, AddWord;
    private Button Train, RemoveTopologyLayer, AddHiddenLayer;
    private int displayedLayout = -1, textFieldTopologyValue = -1;
    private ArrayList<Classifier> classifier;
    private Label labelHiddenTopology, labelNewHiddenLayer, labelTopology, labelTrainingStatus;
    private ImageView[] icons;
    private Label[] labelMenu;
    private NeuralNetworkThread neuralNetworkThread;
    private Label speechRecognitionStatus, speechRecognitionOutput;
    private boolean wordsDetected = false;

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

        icons = new ImageView[4];
        labelMenu = new Label[4];
        for (int i=0; i<4; i++)
        {
            icons[i] = new ImageView(new Image("/com/ViktorVano/SpeechRecognitionAI/images/icon"+(i+1)+".png"));
            icons[i].setPreserveRatio(true);
            icons[i].setFitWidth(96);
            icons[i].setFitHeight(96);
            labelMenu[i] = new Label();
            flow.getChildren().add(icons[i]);
            flow.getChildren().add(labelMenu[i]);
            int layoutIndex = i;
            labelMenu[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    displayLayout(layoutIndex);
                }
            });
            icons[i].setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    displayLayout(layoutIndex);
                }
            });
        }
        labelMenu[0].setText("     Training Data\n ");
        labelMenu[1].setText("        Train AI\n ");
        labelMenu[2].setText("Speech Recognition\n ");
        labelMenu[3].setText("        Settings");

        hBoxBottom.setPadding(new Insets(15, 50, 15, 50));
        hBoxBottom.setSpacing(30);
        hBoxBottom.setStyle("-fx-background-color: #336699;");

        classifier = new ArrayList<>();

        initializeDataLayout();
        initializeTrainingLayout();
        initializeRecognitionLayout();
        initializeSettingsLayout();

        Scene scene = new Scene(borderPane, width, height);

        stage.setTitle("Speech Recognition AI");
        stage.setScene(scene);
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        Image icon =  new Image("com\\ViktorVano\\SpeechRecognitionAI\\images\\neural-network-icon.jpg");
        stage.getIcons().add(icon);
        displayLayout(2);//Speech Recognition Layout
    }

    private void detectWords()
    {
        detectedWordsSeries.getData().clear();
        final int detectedValue = 3000;
        final int wordThreshold = 350;
        int lastValue = 1500;
        int audioSample;
        for (int i = 0; i < recordedAudio.audioRecordLength; i+=2)
        {
            audioSample = recordedAudio.audioRecord[i] + recordedAudio.audioRecord[i+1]*256;
            if (lastValue != detectedValue && Math.abs(audioSample) > wordThreshold)
            {
                if(i>=600)
                {
                    detectedWordsSeries.getData().add(new XYChart.Data<>(i-600, 0));
                    detectedWordsSeries.getData().add(new XYChart.Data<>(i-599, detectedValue));
                }else
                {
                    detectedWordsSeries.getData().add(new XYChart.Data<>(0, detectedValue));
                }
                lastValue = detectedValue;
            }
            else if (lastValue != 0 && Math.abs(audioSample) <= wordThreshold)
            {
                if(i-1 >= 0)
                    detectedWordsSeries.getData().add(new XYChart.Data<>(i-1, detectedValue));
                detectedWordsSeries.getData().add(new XYChart.Data<>(i, 0));
                lastValue = 0;
            }

            boolean valueTheSame = true;

            while(valueTheSame)
            {
                valueTheSame = false;
                int x=i;
                for(; x<recordedAudio.audioRecordLength && x<(600+i); x+=2)
                {
                    if(lastValue == detectedValue && Math.abs(recordedAudio.audioRecord[x] + recordedAudio.audioRecord[x+1]*256) > 280)
                        valueTheSame = true;
                }

                if(valueTheSame)
                    i = x;
            }

            if(i == recordedAudio.audioRecordLength-2)
                detectedWordsSeries.getData().add(new XYChart.Data<>(i, 0));

        }
        int word = 0;
        for(int i=0; i<detectedWordsSeries.getData().size(); i++)
        {
            //System.out.println(detectedWordsSeries.getData().get(i).toString());
            if(i < detectedWordsSeries.getData().size()-1
            && detectedWordsSeries.getData().get(i).getYValue().intValue() == detectedValue
            && detectedWordsSeries.getData().get(i+1).getYValue().intValue() == detectedValue)
            {
                System.out.println(detectedWordsSeries.getData().get(i).getXValue().toString() + " " +
                        detectedWordsSeries.getData().get(i+1).getXValue().toString());
                int start = detectedWordsSeries.getData().get(i).getXValue().intValue();
                int end = detectedWordsSeries.getData().get(i+1).getXValue().intValue();
                int length = end - start + 1;
                if(start == 0 && length%2==0)
                {
                    start++;
                    end++;
                    System.out.println("Shifting by a byte.");
                }

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

    @Override
    public void stop()
    {
        System.out.println("Leaving the app...");
        System.exit(0);
    }

    private void captureAudio()
    {
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

    private void neuralNetworkRoutine()
    {
        neuralNetworkThread.setRecords(records);
        neuralNetworkThread.start();
    }

    private void initializeDataLayout()
    {
        Play = new Button("Play Record");
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

        Record = new Button("Record Audio");
        Record.setPrefHeight(100);
        Record.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                captureAudio();
            }
        });

        database = loadDatabase();
        databaseList = new ListView<>();
        databaseItem = FXCollections.observableArrayList();
        for(int i=0; i<database.size(); i++)
            databaseItem.add(database.get(i).name);
        databaseList.setItems(databaseItem);
        databaseList.setPrefHeight(500);
        databaseList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(databaseList.getSelectionModel().getSelectedIndex() != -1)
                {
                    databaseWordIndex = databaseList.getSelectionModel().getSelectedIndex();
                    txtDatabaseWord.setText(database.get(databaseWordIndex).name);
                    AudioPlayer audioPlayer = new AudioPlayer(audioCapture, database.get(databaseWordIndex));
                    audioPlayer.start();
                }
            }
        });

        buttonPlayDatabaseWord = new Button("Play");
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

        buttonRemoveDatabaseWord = new Button("Remove");
        buttonRemoveDatabaseWord.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(databaseWordIndex != -1)
                {
                    txtDatabaseWord.setText("");
                    databaseList.getItems().remove(databaseWordIndex);
                    database.remove(databaseWordIndex);
                    databaseWordIndex = -1;
                    saveDatabase(database);
                }
            }
        });

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
                    saveDatabase(database);
                }
            }
        });

        records = FXCollections.observableArrayList();
        recordsList = new ListView<>();
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
                    AudioPlayer audioPlayer = new AudioPlayer(audioCapture, records.get(recordedWordIndex));
                    audioPlayer.start();
                }
            }
        });

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

        PlayWord = new Button("Play");
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

        RemoveWord = new Button("Remove");
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

        AddWord = new Button("Add to Database");
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
                    saveDatabase(database);
                }
            }
        });

        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        //creating the chart
        lineChart = new LineChart<>(xAxis,yAxis);

        lineChart.setTitle("Audio Data");
        //defining a series
        displayedSeries = new XYChart.Series<>();
        displayedSeries.setName("Recorded Audio");
        detectedWordsSeries = new XYChart.Series<>();
        detectedWordsSeries.setName("Detected Words");
        lineChart.setCreateSymbols(false);
        //populating the series with data
        lineChart.getData().add(displayedSeries);
        lineChart.getData().add(detectedWordsSeries);
        lineChart.setAnimated(false);

        timelineUpdateData = new Timeline(new KeyFrame(Duration.millis(200), new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {

                if (updateData && audioCapture.isAudioRecorded())
                {
                    updateData = false;
                    recordedAudio.audioRecord = audioCapture.getRecord();
                    if (recordedAudio.audioRecord != null) {
                        displayedSeries.getData().clear();
                        recordedAudio.audioRecordLength = audioCapture.getRecordLength();
                        for (int i = 0; i < recordedAudio.audioRecordLength - 1; i++) {
                            if (i % 10 == 0)
                                displayedSeries.getData().add(new XYChart.Data<>(i, recordedAudio.audioRecord[i] + recordedAudio.audioRecord[i + 1] * 256));
                        }
                        detectWords();
                        wordsDetected = true;
                    }
                }

                if (weightsLoaded && wordsDetected && displayedLayout == 2)
                {
                    if (!wordsRecognizedFlag && !neuralNetworkThread.isAlive()) {
                        neuralNetworkRoutine();
                    }

                    if (wordsRecognizedFlag && !neuralNetworkThread.isAlive()) {
                        wordsDetected = false;
                        wordsRecognizedFlag = false;
                        captureAudio();
                    }

                    if(!wordsRecognizedFlag && neuralNetworkThread.isAlive())
                    {
                        speechRecognitionStatus.setText("Speech recognized.");
                        speechRecognitionOutput.setText(recognizedMessage);
                    }
                }else
                {
                    if(weights.size()!=0)
                    {
                        speechRecognitionStatus.setText("Loading weights[" + neuronIndex + " / " + weights.size() +"]: "
                        + ((neuronIndex*100)/weights.size()) + "%\t\tStep: " + loadingStep + " / 2");
                    }
                }
            }
        }));
        timelineUpdateData.setCycleCount(Timeline.INDEFINITE);
        timelineUpdateData.play();
    }

    private void initializeTrainingLayout()
    {
        trainingList = new ListView<>();
        trainingItem = FXCollections.observableArrayList();
        trainingList.setItems(trainingItem);

        timelineTrainingLabelUpdate = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event)
            {
                if(!trainingIsRunning)
                {
                    for(int i=0; i<labelMenu.length; i++)
                    {
                        icons[i].setDisable(false);
                        labelMenu[i].setDisable(false);
                    }
                    timelineTrainingLabelUpdate.stop();
                }else if(updateTrainingLabel)
                {
                    if (currentTrainingErrorLabel < minimumTrainingError && trainingPassLabel > minimumTrainingCycles){
                        trainingIsRunning = false;
                        labelTrainingStatus.setText("Training pass: " + trainingPassLabel
                                + "\"\t\tError: " + currentTrainingErrorLabel
                                + "\t\tTraining DONE");
                        Train.setDisable(false);
                    }else
                    {
                        labelTrainingStatus.setText("Training pass: " + trainingPassLabel
                                + "\t\tTargets[" + trainingLineLabel + "]=\"" + database.get(trainingLineLabel).name
                                + "\"\t\tError: " + currentTrainingErrorLabel);
                    }

                    updateTrainingLabel = false;
                }
            }
        }));
        timelineTrainingLabelUpdate.setCycleCount(Timeline.INDEFINITE);

        Train = new Button("Train");
        Train.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Train.setDisable(true);
                for(int i=0; i<labelMenu.length; i++)
                {
                    icons[i].setDisable(true);
                    labelMenu[i].setDisable(true);
                }
                TrainingThread trainingThread = new TrainingThread(database, classifier);
                trainingThread.start();
                trainingIsRunning = true;
                labelTrainingStatus.setText("Training just started.");
                timelineTrainingLabelUpdate.play();
            }
        });

        labelTrainingStatus = new Label();
        labelTrainingStatus.setFont(Font.font("Arial", 20));

        labelHiddenTopology = new Label("\n Topology of hidden layers ");
        labelHiddenTopology.setFont(Font.font("Arial", 26));

        topologyList = new ListView<>();
        topologyItem = FXCollections.observableArrayList();
        LinkedList<Integer> tempTopology = loadTopology();
        for (Integer integer : tempTopology)
            topologyItem.add(integer.toString());
        topologyList.setItems(topologyItem);
        topologyList.setPrefHeight(200);
        topologyList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                AddHiddenLayer.setDisable(textFieldTopologyValue < minimumLayerSize || topology.size() >= maximumTopologySize);

                RemoveTopologyLayer.setDisable(topologyItem.size() == 0);
            }
        });

        RemoveTopologyLayer = new Button("Remove Hidden Layer");
        RemoveTopologyLayer.setDisable(true);
        RemoveTopologyLayer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(topologyList.getSelectionModel().getSelectedIndex() != -1)
                    topologyItem.remove(topologyList.getSelectionModel().getSelectedIndex());
                RemoveTopologyLayer.setDisable(topologyItem.size() == 0);
                calculateTopology();
                saveTopology(topology);
            }
        });

        labelNewHiddenLayer = new Label("\n New hidden layer");
        labelNewHiddenLayer.setFont(Font.font("Arial", 26));

        txtHiddenLayer = new TextField();
        txtHiddenLayer.setPromptText("Hidden Layer");
        txtHiddenLayer.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.length() == 0)
                {
                    textFieldTopologyValue = -1;
                }else
                {
                    try
                    {
                        textFieldTopologyValue = Integer.parseInt(newValue);
                    }catch (Exception e)
                    {
                        textFieldTopologyValue = -1;
                    }
                }
                AddHiddenLayer.setDisable(textFieldTopologyValue < minimumLayerSize || topology.size() >= maximumTopologySize);

                RemoveTopologyLayer.setDisable(topologyItem.size() == 0);
            }
        });

        AddHiddenLayer = new Button("Add Hidden Layer");
        AddHiddenLayer.setDisable(true);
        AddHiddenLayer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(topologyItem.size() != 0
                    && topologyList.getSelectionModel().getSelectedIndex() != -1
                    && textFieldTopologyValue >= minimumLayerSize)
                    topologyItem.add(topologyList.getSelectionModel().getSelectedIndex() +1 , String.valueOf(textFieldTopologyValue));
                else if(textFieldTopologyValue >= minimumLayerSize)
                    topologyItem.add(String.valueOf(textFieldTopologyValue));

                textFieldTopologyValue = -1;
                txtHiddenLayer.setText("");
                calculateTopology();
                saveTopology(topology);
            }
        });

        labelTopology = new Label();
        labelTopology.setFont(Font.font("Arial", 20));
        countWords();
        calculateTopology();
    }

    private void initializeRecognitionLayout()
    {
        neuralNetworkThread = new NeuralNetworkThread(classifier);
        speechRecognitionStatus = new Label();
        speechRecognitionStatus.setFont(Font.font("Arial", 20));
        speechRecognitionOutput = new Label();
        speechRecognitionOutput.setFont(Font.font("Arial", 20));
    }

    private void initializeSettingsLayout()
    {

    }

    private void displayDataLayout()
    {
        hBoxBottom.getChildren().add(Play);
        hBoxBottom.getChildren().add(Record);
        vBoxRight.getChildren().add(databaseList);
        vBoxRight.getChildren().add(buttonPlayDatabaseWord);
        vBoxRight.getChildren().add(buttonRemoveDatabaseWord);
        vBoxRight.getChildren().add(txtDatabaseWord);
        hBoxBottom.getChildren().add(recordsList);
        hBoxBottom.getChildren().add(txtDetectedWord);
        hBoxBottom.getChildren().add(PlayWord);
        hBoxBottom.getChildren().add(RemoveWord);
        hBoxBottom.getChildren().add(AddWord);
        stackPaneCenter.getChildren().add(lineChart);
        displayedLayout = 0;
        System.out.println("Data Layout displayed.");
    }

    private void hideDataLayout()
    {
        hBoxBottom.getChildren().remove(Play);
        hBoxBottom.getChildren().remove(Record);
        vBoxRight.getChildren().remove(databaseList);
        vBoxRight.getChildren().remove(buttonPlayDatabaseWord);
        vBoxRight.getChildren().remove(buttonRemoveDatabaseWord);
        vBoxRight.getChildren().remove(txtDatabaseWord);
        hBoxBottom.getChildren().remove(recordsList);
        hBoxBottom.getChildren().remove(txtDetectedWord);
        hBoxBottom.getChildren().remove(PlayWord);
        hBoxBottom.getChildren().remove(RemoveWord);
        hBoxBottom.getChildren().remove(AddWord);
        stackPaneCenter.getChildren().remove(lineChart);
    }

    private void displayTrainingLayout()
    {
        countWords();
        stackPaneCenter.getChildren().add(trainingList);
        Train.setDisable(!sameWordCount || topology.size() < 3);
        hBoxBottom.getChildren().add(Train);
        hBoxBottom.getChildren().add(labelTrainingStatus);
        vBoxRight.getChildren().add(labelHiddenTopology);
        vBoxRight.getChildren().add(topologyList);
        vBoxRight.getChildren().add(RemoveTopologyLayer);
        vBoxRight.getChildren().add(labelNewHiddenLayer);
        vBoxRight.getChildren().add(txtHiddenLayer);
        vBoxRight.getChildren().add(AddHiddenLayer);
        vBoxRight.getChildren().add(labelTopology);
        displayedLayout = 1;
        System.out.println("Training Layout displayed.");
    }

    private void hideTrainingLayout()
    {
        stackPaneCenter.getChildren().remove(trainingList);
        hBoxBottom.getChildren().remove(Train);
        hBoxBottom.getChildren().remove(labelTrainingStatus);
        vBoxRight.getChildren().remove(labelHiddenTopology);
        vBoxRight.getChildren().remove(topologyList);
        vBoxRight.getChildren().remove(RemoveTopologyLayer);
        vBoxRight.getChildren().remove(labelNewHiddenLayer);
        vBoxRight.getChildren().remove(txtHiddenLayer);
        vBoxRight.getChildren().remove(AddHiddenLayer);
        vBoxRight.getChildren().remove(labelTopology);
    }

    private void displayRecognitionLayout()
    {
        stackPaneCenter.getChildren().add(lineChart);
        hBoxBottom.getChildren().add(speechRecognitionStatus);
        hBoxBottom.getChildren().add(speechRecognitionOutput);
        displayedLayout = 2;
        System.out.println("Recognition Layout displayed.");
    }

    private void hideRecognitionLayout()
    {
        stackPaneCenter.getChildren().remove(lineChart);
        hBoxBottom.getChildren().remove(speechRecognitionStatus);
        hBoxBottom.getChildren().remove(speechRecognitionOutput);
    }

    private void displaySettingsLayout()
    {
        displayedLayout = 3;
        System.out.println("Settings Layout displayed.");
    }

    private void hideSettingsLayout()
    {

    }

    private void displayLayout(int layoutIndex)
    {
        if(displayedLayout == 0 && layoutIndex != 0)
            hideDataLayout();
        else if(displayedLayout == 1 && layoutIndex != 1)
            hideTrainingLayout();
        else if(displayedLayout == 2 && layoutIndex != 2)
            hideRecognitionLayout();
        else if(displayedLayout == 3 && layoutIndex != 3)
            hideSettingsLayout();

        if(layoutIndex == 0 && displayedLayout != 0)
            displayDataLayout();
        else if(layoutIndex == 1 && displayedLayout != 1)
            displayTrainingLayout();
        else if(layoutIndex == 2 && displayedLayout != 2)
            displayRecognitionLayout();
        else if(layoutIndex == 3 && displayedLayout != 3)
            displaySettingsLayout();
    }

    private void countWords()
    {
        classifier.clear();
        for(int i=0; i<databaseList.getItems().size(); i++)
        {
            if(classifier.size()==0)
                classifier.add(new Classifier(databaseList.getItems().get(i)));
            else
                for (int x=0; x<classifier.size(); x++)
                {
                    if(classifier.get(x).getName().equals(databaseList.getItems().get(i)))
                    {
                        classifier.get(x).incrementCount();
                        break;
                    }
                    else if(x==classifier.size()-1)
                    {
                        classifier.add(new Classifier(databaseList.getItems().get(i)));
                        break;
                    }
                }
        }

        int maximum = -1;
        sameWordCount = true;
        for(int i=0; i<classifier.size(); i++)
            if(classifier.get(i).getCount() > maximum)
                maximum = classifier.get(i).getCount();
        trainingItem.clear();
        for(int i=0; i<classifier.size(); i++)
        {
            if(classifier.get(i).getCount() == maximum)
                trainingItem.add(classifier.get(i).getName() + "\t\tcount: " + classifier.get(i).getCount() + "\t\tOK");
            else
            {
                trainingItem.add(classifier.get(i).getName() + "\t\tcount: " + classifier.get(i).getCount() + "\t\tMore specimens required!");
                sameWordCount = false;
            }
        }
    }

    private void calculateTopology()
    {
        topology.clear();
        topology.add(maxWordLength/2);
        for(int i=0; i<topologyItem.size(); i++)
            topology.add(Integer.parseInt(topologyItem.get(i)));
        topology.add(classifier.size());

        if(topology.size() >= 3)
        {
            String text = "\n Topology:";
            for(int i=0; i<topology.size(); i++)
            {
                if(i == 0)
                    text += "\n   Input Layer: ";
                else if(i == topology.size()-1)
                    text += "\n   Output Layer: ";
                else
                    text += "\n   Hidden Layer: ";

                text += topology.get(i);
            }
            labelTopology.setText(text);
            Train.setDisable(!sameWordCount);
        } else
        {
            labelTopology.setText("\nTopology:\nAdd more layers!");
            Train.setDisable(true);
        }
    }
}
