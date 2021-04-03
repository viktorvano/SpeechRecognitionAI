package com.ViktorVano.SpeechRecognitionAI.GUI;

import com.ViktorVano.SpeechRecognitionAI.Audio.AudioCapture;
import com.ViktorVano.SpeechRecognitionAI.Audio.AudioPlayer;
import com.ViktorVano.SpeechRecognitionAI.Audio.RecordedAudio;
import com.ViktorVano.SpeechRecognitionAI.FFNN.NeuralNetworkThread;
import com.ViktorVano.SpeechRecognitionAI.FFNN.TrainingThread;
import com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Classifier;
import com.ViktorVano.SpeechRecognitionAI.Miscellaneous.WordRouter;
import com.ViktorVano.SpeechRecognitionAI.Miscellaneous.WordRouting;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.ViktorVano.SpeechRecognitionAI.Audio.AudioDatabase.*;
import static com.ViktorVano.SpeechRecognitionAI.FFNN.TopologyFile.*;
import static com.ViktorVano.SpeechRecognitionAI.FFNN.Variables.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.PrintToConsoleFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.WordRoutingFile.*;


public class SpeechRecognitionAI extends Application {
    private AudioCapture audioCapture;
    private RecordedAudio recordedAudio;
    private XYChart.Series<Number, Number> displayedSeries, detectedWordsSeries;
    private Timeline timelineTrainingLabelUpdate;
    private boolean updateData = true, sameWordCount = false;
    private final BorderPane borderPane = new BorderPane();
    private final StackPane stackPaneCenter = new StackPane();
    private final VBox vBoxRight = new VBox();
    private final FlowPane flow = new FlowPane();
    private final HBox hBoxBottom = new HBox();
    private ObservableList<RecordedAudio> database, records;
    private ObservableList<WordRouting> wordRoutingDatabase;
    private ListView<String> databaseList, recordsList, trainingList, topologyList, wordRoutingList;
    private ObservableList<String> databaseItem, recordItem, trainingItem, topologyItem;
    private TextField txtDetectedWord, txtDatabaseWord, txtHiddenLayer;
    private int recordedWordIndex = -1, databaseWordIndex = -1, wordRoutingIndex = -1;
    private LineChart<Number,Number> lineChart;
    private Button buttonPlay, buttonRecord, buttonPlayDatabaseWord, buttonRemoveDatabaseWord;
    private Button buttonPlayWord, buttonRemoveWord, buttonAddWord;
    private Button buttonTrain, buttonStopTraining, buttonRemoveTopologyLayer, buttonAddHiddenLayer;
    private Button buttonAddWordRouting, buttonUpdateWordRouting, buttonRemoveWordRouting;
    private int displayedLayout = -1, textFieldTopologyValue = -1, displayMessageCounter = -1;
    private ArrayList<Classifier> classifier;
    private Label labelHiddenTopology, labelNewHiddenLayer, labelTopology, labelTrainingStatus;
    private ImageView[] icons;
    private Label[] labelMenu;
    private NeuralNetworkThread neuralNetworkThread;
    private Label speechRecognitionStatus, speechRecognitionOutput;
    private Label labelNewWordRouting, labelEditWordRouting;
    private TextField txtNewWord, txtNewAddress, txtNewPort;
    private TextField txtEditWord, txtEditAddress, txtEditPort;
    private boolean wordsDetected = false;
    private TrainingThread trainingThread;
    private CheckBox checkBoxPrintToConsole;

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
        final int height = 700;

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
            icons[i].setFitWidth(80);
            icons[i].setFitHeight(80);
            labelMenu[i] = new Label();
            flow.getChildren().add(icons[i]);
            flow.getChildren().add(labelMenu[i]);
            int layoutIndex = i;
            labelMenu[i].setOnMouseClicked(event -> displayLayout(layoutIndex));
            icons[i].setOnMouseClicked(event -> displayLayout(layoutIndex));
            labelMenu[i].setDisable(true);
            icons[i].setDisable(true);
        }
        labelMenu[0].setText("Training Data\n ");
        labelMenu[1].setText("    Train AI\n ");
        labelMenu[2].setText("   Speech\nRecognition\n ");
        labelMenu[3].setText("   Settings");

        hBoxBottom.setPadding(new Insets(15, 50, 15, 50));
        hBoxBottom.setSpacing(30);
        hBoxBottom.setStyle("-fx-background-color: #336699;");

        classifier = new ArrayList<>();

        initializeDataLayout();
        initializeTrainingLayout();
        initializeRecognitionLayout();
        initializeSettingsLayout();

        Scene scene = new Scene(borderPane, width, height);

        stage.setTitle("Speech Recognition AI - developed by Viktor Vano (20210403)");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
        try
        {
            Image icon = new Image(getClass().getResourceAsStream("../images/icon3.png"));
            stage.getIcons().add(icon);
            System.out.println("Icon loaded from IDE...");
        }catch(Exception e)
        {
            try
            {
                Image icon = new Image("com/ViktorVano/SpeechRecognitionAI/images/icon3.png");
                stage.getIcons().add(icon);
                System.out.println("Icon loaded from exported JAR...");
            }catch(Exception e1)
            {
                System.out.println("Icon failed to load...");
            }
        }
        displayLayout(2);//Speech Recognition Layout
    }

    private void detectWords()
    {
        detectedWordsSeries.getData().clear();
        final int detectedValue = 3000;
        final int wordThreshold = 500;
        final int preWordSamples = 1200;
        final int wordInertiaSamples = 250;
        final int wordInertiaThreshold = 300;
        int lastValue = 1500;
        int audioSample;
        for (int i = 0; i < recordedAudio.audioRecordLength; i+=2)
        {
            audioSample = recordedAudio.audioRecord[i] + recordedAudio.audioRecord[i+1]*256;
            if (lastValue != detectedValue && Math.abs(audioSample) > wordThreshold)
            {
                if(i>=600)
                {
                    detectedWordsSeries.getData().add(new XYChart.Data<>(i-preWordSamples, 0));
                    detectedWordsSeries.getData().add(new XYChart.Data<>(i-(preWordSamples-1), detectedValue));
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
                for(; x<recordedAudio.audioRecordLength && x<(wordInertiaSamples+i); x+=2)
                {
                    if(lastValue == detectedValue && Math.abs(recordedAudio.audioRecord[x] + recordedAudio.audioRecord[x+1]*256) > wordInertiaThreshold)
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
            if(i < detectedWordsSeries.getData().size()-1
            && detectedWordsSeries.getData().get(i).getYValue().intValue() == detectedValue
            && detectedWordsSeries.getData().get(i+1).getYValue().intValue() == detectedValue)
            {
                System.out.println(detectedWordsSeries.getData().get(i).getXValue().toString() + " " +
                        detectedWordsSeries.getData().get(i+1).getXValue().toString());
                int start = detectedWordsSeries.getData().get(i).getXValue().intValue();
                int end = detectedWordsSeries.getData().get(i+1).getXValue().intValue();
                if(start < 0)
                    start = 0;

                int length = end - start + 1;
                if(start == 0 && length%2==0)
                {
                    start++;
                    end++;
                    System.out.println("Shifting by a byte.");
                }

                if(length < minWordLength)
                {
                    if(i == 0)
                        detectedWordsSeries.getData().remove(0, i+2);
                    else
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
                    try
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
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
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
        if(neuralNetworkThread.getState() == Thread.State.NEW)
        {
            neuralNetworkThread.start();
        }
        neuralNetworkThread.startAnalysis();
        displayMessageCounter = 0;
    }

    private void initializeDataLayout()
    {
        buttonPlay = new Button("Play Record");
        buttonPlay.setPrefHeight(90);
        buttonPlay.setOnAction(event -> {
            if(recordedAudio != null && recordedAudio.audioRecord != null)
            {
                AudioPlayer audioPlayer = new AudioPlayer(audioCapture, recordedAudio);
                audioPlayer.start();
            }
        });

        buttonRecord = new Button("Record Audio");
        buttonRecord.setPrefHeight(90);
        buttonRecord.setOnAction(event -> captureAudio());

        database = loadDatabase();
        databaseList = new ListView<>();
        databaseItem = FXCollections.observableArrayList();
        for (RecordedAudio audio : database) databaseItem.add(audio.name);
        databaseList.setItems(databaseItem);
        databaseList.setPrefHeight(500);
        databaseList.setOnMouseClicked(event -> {
            if(databaseList.getSelectionModel().getSelectedIndex() != -1)
            {
                databaseWordIndex = databaseList.getSelectionModel().getSelectedIndex();
                txtDatabaseWord.setText(database.get(databaseWordIndex).name);
                AudioPlayer audioPlayer = new AudioPlayer(audioCapture, database.get(databaseWordIndex));
                audioPlayer.start();
            }
        });

        buttonPlayDatabaseWord = new Button("Play");
        buttonPlayDatabaseWord.setStyle("-fx-font-size:30");
        buttonPlayDatabaseWord.setPrefWidth(250);
        buttonPlayDatabaseWord.setOnAction(event -> {
            if(recordedAudio != null && recordedAudio.audioRecord != null && databaseWordIndex !=-1)
            {
                AudioPlayer audioPlayer = new AudioPlayer(audioCapture, database.get(databaseWordIndex));
                audioPlayer.start();
            }
        });

        buttonRemoveDatabaseWord = new Button("Remove");
        buttonRemoveDatabaseWord.setOnAction(event -> {
            if(databaseWordIndex != -1)
            {
                txtDatabaseWord.setText("");
                if(database.size() == databaseWordIndex)
                    databaseWordIndex--;
                if(databaseWordIndex != -1)
                {
                    databaseList.getItems().remove(databaseWordIndex);
                    database.remove(databaseWordIndex);
                }
                try{
                    databaseList.getSelectionModel().select(databaseWordIndex);
                }catch (Exception e)
                {
                    databaseWordIndex = -1;
                }
                saveDatabase(database);
            }
        });

        txtDatabaseWord = new TextField();
        txtDatabaseWord.setPromptText("Name a word");
        txtDatabaseWord.setPrefWidth(150);
        txtDatabaseWord.textProperty().addListener((observable, oldValue, newValue) -> {
            if(databaseWordIndex != -1)
            {
                database.get(databaseWordIndex).name = txtDatabaseWord.getText();
                databaseList.getItems().set(databaseWordIndex, database.get(databaseWordIndex).name);
                saveDatabase(database);
            }
        });

        records = FXCollections.observableArrayList();
        recordsList = new ListView<>();
        recordItem = FXCollections.observableArrayList();
        recordsList.setItems(recordItem);
        recordsList.setPrefHeight(90);
        recordsList.setOnMouseClicked(event -> {
            if(recordsList.getSelectionModel().getSelectedIndex() != -1)
            {
                recordedWordIndex = recordsList.getSelectionModel().getSelectedIndex();
                txtDetectedWord.setText(records.get(recordedWordIndex).name);
                AudioPlayer audioPlayer = new AudioPlayer(audioCapture, records.get(recordedWordIndex));
                audioPlayer.start();
            }
        });

        txtDetectedWord = new TextField();
        txtDetectedWord.setPromptText("Name a word");
        txtDetectedWord.setPrefWidth(150);
        txtDetectedWord.textProperty().addListener((observable, oldValue, newValue) -> {
            if(recordedWordIndex != -1)
            {
                records.get(recordedWordIndex).name = txtDetectedWord.getText();
                recordsList.getItems().set(recordedWordIndex, records.get(recordedWordIndex).name);
            }
        });

        buttonPlayWord = new Button("Play");
        buttonPlayWord.setOnAction(event -> {
            if(recordedAudio != null && recordedAudio.audioRecord != null && recordedWordIndex !=-1)
            {
                AudioPlayer audioPlayer = new AudioPlayer(audioCapture, records.get(recordedWordIndex));
                audioPlayer.start();
            }
        });

        buttonRemoveWord = new Button("Remove");
        buttonRemoveWord.setOnAction(event -> {
            if(recordedWordIndex != -1)
            {
                txtDetectedWord.setText("");
                recordsList.getItems().remove(recordedWordIndex);
                records.remove(recordedWordIndex);
                recordedWordIndex = -1;
            }
        });

        buttonAddWord = new Button("Add to Database");
        buttonAddWord.setOnAction(event -> {
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

        //Also sets displayMessageCounter to 0
        //How long it should keep the displayed message. X*0.25s
        Timeline timelineUpdateData = new Timeline(new KeyFrame(Duration.millis(250), event -> {

            if (updateData && audioCapture.isAudioRecorded()) {
                updateData = false;
                recordedAudio.audioRecord = audioCapture.getRecord();
                int modulo = recordedAudio.audioRecord.length/20000;//To achieve constant data points in the chart.
                displayedSeries.getData().clear();
                recordedAudio.audioRecordLength = audioCapture.getRecordLength();
                for (int i = 0; i < recordedAudio.audioRecordLength - 1; i+=2) {
                    if (i % modulo == 0)
                        displayedSeries.getData().add(new XYChart.Data<>(i, recordedAudio.audioRecord[i] + recordedAudio.audioRecord[i + 1] * 256));
                }
                detectWords();
                wordsDetected = true;
            }

            if (weightsLoaded && wordsDetected && displayedLayout == 2) {
                if (neuralNetworkThread.isFinished() && displayMessageCounter == -1) {
                    neuralNetworkRoutine();//Also sets displayMessageCounter to 0
                } else if (!neuralNetworkThread.isFinished() && displayMessageCounter == 0) {
                    speechRecognitionStatus.setText("Speech being processed.");
                    speechRecognitionOutput.setText(neuralNetworkThread.getRecognizedMessage());
                } else if (neuralNetworkThread.isFinished() && displayMessageCounter != -1) {
                    if (displayMessageCounter < 2)//How long it should keep the displayed message. X*0.25s
                        displayMessageCounter++;
                    else {
                        new WordRouter(wordRoutingDatabase, neuralNetworkThread.getRecognizedMessage());
                        displayMessageCounter = -1;
                        wordsDetected = false;
                        speechRecognitionStatus.setText("Listening...");
                        captureAudio();
                    }
                    speechRecognitionOutput.setText(neuralNetworkThread.getRecognizedMessage());
                }
            } else if (weights.size() != 0 && !weightsLoaded) {
                if (loadingStep == 1)
                    speechRecognitionStatus.setText("Loading weights from a file[" + neuronIndex + " / " + weights.size() + "]: "
                            + Math.round(((double) neuronIndex * 100.0) / (double) weights.size()) + "%\t\tStep: " + loadingStep + " / 2");
                else
                    speechRecognitionStatus.setText("Setting weights in neurons[" + neuronIndex + " / " + weights.size() + "]: "
                            + Math.round(((double) neuronIndex * 100.0) / (double) weights.size()) + "%\t\tStep: " + loadingStep + " / 2");
            } else if (weightsLoaded && !wordsDetected && displayedLayout == 2 && loadingStep != 3) {
                speechRecognitionStatus.setText("Loading weights from a file[" + neuronIndex + " / " + weights.size() + "]: "
                        + Math.round(((double) neuronIndex * 100.0) / (double) weights.size()) + "%\t\tDone.\t\tListening...");
                loadingStep = 3;
                for (int i = 0; i < 4; i++) {
                    labelMenu[i].setDisable(false);
                    icons[i].setDisable(false);
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

        timelineTrainingLabelUpdate = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
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
                if (currentTrainingErrorLabel < minimumTrainingError && trainingPassLabel > minimumTrainingCycles
                    || buttonStopTraining.isDisabled()){
                    trainingIsRunning = false;
                    labelTrainingStatus.setText("Training pass: " + trainingPassLabel
                            + "\t\tAverage: " + averageError
                            + "\t\tTraining DONE");
                    buttonTrain.setDisable(false);
                    topologyList.setDisable(false);
                    txtHiddenLayer.setDisable(false);
                }else
                {
                    labelTrainingStatus.setText("Training pass: " + trainingPassLabel
                            + "\t\tSample[" + trainingLineLabel + "]=\"" + database.get(trainingLineLabel).name
                            + "\"\t\tError: " + currentTrainingErrorLabel
                            + "\t\tAverage: " + averageError);
                }

                updateTrainingLabel = false;
            }
        }));
        timelineTrainingLabelUpdate.setCycleCount(Timeline.INDEFINITE);

        buttonTrain = new Button("Train");
        buttonTrain.setOnAction(event -> {
            buttonTrain.setDisable(true);
            for(int i=0; i<labelMenu.length; i++)
            {
                icons[i].setDisable(true);
                labelMenu[i].setDisable(true);
            }
            trainingThread = new TrainingThread(database, classifier);
            trainingThread.start();
            trainingIsRunning = true;
            labelTrainingStatus.setText("Training just started.");
            timelineTrainingLabelUpdate.play();
            topologyList.setDisable(true);
            txtHiddenLayer.setDisable(true);
            buttonRemoveTopologyLayer.setDisable(true);
            buttonAddHiddenLayer.setDisable(true);
            buttonStopTraining.setDisable(false);
        });

        buttonStopTraining = new Button("Stop");
        buttonStopTraining.setDisable(true);
        buttonStopTraining.setOnAction(event -> {
            trainingThread.stopTraining();
            buttonStopTraining.setDisable(true);
            buttonTrain.setDisable(false);
            topologyList.setDisable(false);
            txtHiddenLayer.setDisable(false);
        });

        labelTrainingStatus = new Label();
        labelTrainingStatus.setFont(Font.font("Arial", 20));

        labelHiddenTopology = new Label("\n Topology of hidden layers ");
        labelHiddenTopology.setFont(Font.font("Arial", 24));

        topologyList = new ListView<>();
        topologyItem = FXCollections.observableArrayList();
        ArrayList<Integer> tempTopology = loadTopology();
        for (Integer integer : tempTopology)
            topologyItem.add(integer.toString());
        topologyList.setItems(topologyItem);
        topologyList.setPrefHeight(200);
        topologyList.setOnMouseClicked(event -> {
            buttonAddHiddenLayer.setDisable(textFieldTopologyValue < minimumLayerSize ||
                    topology.size() >= maximumTopologySize);
            buttonRemoveTopologyLayer.setDisable(topologyItem.size() == 0);
        });

        buttonRemoveTopologyLayer = new Button("Remove Hidden Layer");
        buttonRemoveTopologyLayer.setDisable(true);
        buttonRemoveTopologyLayer.setOnAction(event -> {
            if(topologyList.getSelectionModel().getSelectedIndex() != -1)
                topologyItem.remove(topologyList.getSelectionModel().getSelectedIndex());
            buttonRemoveTopologyLayer.setDisable(topologyItem.size() == 0);
            calculateTopology();
            saveTopology(topology);
        });

        labelNewHiddenLayer = new Label("\n New hidden layer");
        labelNewHiddenLayer.setFont(Font.font("Arial", 24));

        txtHiddenLayer = new TextField();
        txtHiddenLayer.setPromptText("Hidden Layer");
        txtHiddenLayer.textProperty().addListener((observable, oldValue, newValue) -> {
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
            buttonAddHiddenLayer.setDisable(textFieldTopologyValue < minimumLayerSize || topology.size() >= maximumTopologySize);

            buttonRemoveTopologyLayer.setDisable(topologyItem.size() == 0);
        });

        buttonAddHiddenLayer = new Button("Add Hidden Layer");
        buttonAddHiddenLayer.setDisable(true);
        buttonAddHiddenLayer.setOnAction(event -> {
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
        });

        labelTopology = new Label();
        labelTopology.setFont(Font.font("Arial", 16));
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
        speechRecognitionOutput.setTextFill(Color.web("#ffffff"));
    }

    private void initializeSettingsLayout()
    {
        wordRoutingDatabase = loadWordRouting();
        wordRoutingList = new ListView<>();
        ObservableList<String> wordRoutingItem = FXCollections.observableArrayList();
        for (WordRouting wordRouting : wordRoutingDatabase)
            wordRoutingItem.add(wordRouting.word + "\t\t\t" + wordRouting.address + " : " + wordRouting.port);
        wordRoutingList.setItems(wordRoutingItem);
        wordRoutingList.setOnMouseClicked(event -> {
            if(wordRoutingList.getSelectionModel().getSelectedIndex() != -1)
            {
                wordRoutingIndex = wordRoutingList.getSelectionModel().getSelectedIndex();
                buttonRemoveWordRouting.setDisable(false);
                String[] strings = wordRoutingList.getItems().get(wordRoutingIndex).split("\t\t\t");
                txtEditWord.setText(strings[0]);
                strings = strings[1].split(" : ");
                txtEditAddress.setText(strings[0]);
                txtEditPort.setText(strings[1]);
            }else
            {
                wordRoutingIndex = -1;
                buttonRemoveWordRouting.setDisable(true);
                txtEditWord.setText("");
                txtEditAddress.setText("");
                txtEditPort.setText("");
                buttonUpdateWordRouting.setDisable(true);
            }
        });

        buttonRemoveWordRouting = new Button("Remove Word Routing");
        buttonRemoveWordRouting.setDisable(wordRoutingIndex == -1);
        buttonRemoveWordRouting.setOnAction(event -> {
            if(wordRoutingList.getSelectionModel().getSelectedIndex() != -1)
            {
                wordRoutingList.getItems().remove(wordRoutingIndex);
                wordRoutingDatabase.remove(wordRoutingIndex);
                wordRoutingIndex = wordRoutingList.getSelectionModel().getSelectedIndex();
                buttonRemoveWordRouting.setDisable(wordRoutingIndex == -1);
                if(wordRoutingIndex == -1)
                {
                    txtEditWord.setText("");
                    txtEditAddress.setText("");
                    txtEditPort.setText("");
                    buttonUpdateWordRouting.setDisable(true);
                }
                saveWordRouting(wordRoutingDatabase);
            }
        });

        labelNewWordRouting = new Label("\n New Word Routing \n\n");
        labelNewWordRouting.setFont(Font.font("Arial", 20));

        txtNewWord = new TextField();
        txtNewWord.setPromptText("Word/Phrase");
        txtNewWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWordRouting.setDisable(txtNewWord.getText().length() == 0 ||
                txtNewAddress.getText().length() == 0 || txtNewPort.getText().length() == 0));

        txtNewAddress = new TextField();
        txtNewAddress.setPromptText("IP Address/URL");
        txtNewAddress.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWordRouting.setDisable(txtNewWord.getText().length() == 0 ||
                txtNewAddress.getText().length() == 0 || txtNewPort.getText().length() == 0));

        txtNewPort = new TextField();
        txtNewPort.setPromptText("Port");
        txtNewPort.textProperty().addListener((observable, oldValue, newValue) ->
                buttonAddWordRouting.setDisable(txtNewWord.getText().length() == 0 ||
                txtNewAddress.getText().length() == 0 || txtNewPort.getText().length() == 0));

        buttonAddWordRouting = new Button("Add Word Routing");
        buttonAddWordRouting.setDisable(true);
        buttonAddWordRouting.setOnAction(event -> {
            WordRouting tempWordRouting = new WordRouting();
            tempWordRouting.word = txtNewWord.getText();
            tempWordRouting.address = txtNewAddress.getText();
            tempWordRouting.port = txtNewPort.getText();
            txtNewWord.setText("");
            txtNewAddress.setText("");
            txtNewPort.setText("");
            String tempString =  tempWordRouting.word + "\t\t\t" +
                    tempWordRouting.address + " : " + tempWordRouting.port;
            wordRoutingList.getItems().add(tempString);
            wordRoutingDatabase.add(wordRoutingDatabase.size(), tempWordRouting);
            saveWordRouting(wordRoutingDatabase);
        });

        labelEditWordRouting = new Label("\n Edit\n Word Routing \n\n");
        labelEditWordRouting.setFont(Font.font("Arial", 20));

        txtEditWord = new TextField();
        txtEditWord.setPromptText("Word/Phrase");
        txtEditWord.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWordRouting.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 || txtEditPort.getText().length() == 0 ||
                        wordRoutingIndex == -1));

        txtEditAddress = new TextField();
        txtEditAddress.setPromptText("IP Address/URL");
        txtEditAddress.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWordRouting.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 || txtEditPort.getText().length() == 0 ||
                        wordRoutingIndex == -1));

        txtEditPort = new TextField();
        txtEditPort.setPromptText("Port");
        txtEditPort.textProperty().addListener((observable, oldValue, newValue) ->
                buttonUpdateWordRouting.setDisable(txtEditWord.getText().length() == 0 ||
                        txtEditAddress.getText().length() == 0 || txtEditPort.getText().length() == 0 ||
                        wordRoutingIndex == -1));

        buttonUpdateWordRouting = new Button("Update Word Routing");
        buttonUpdateWordRouting.setDisable(true);
        buttonUpdateWordRouting.setOnAction(event -> {
            WordRouting tempWordRouting = new WordRouting();
            tempWordRouting.word = txtEditWord.getText();
            tempWordRouting.address = txtEditAddress.getText();
            tempWordRouting.port = txtEditPort.getText();
            txtEditWord.setText("");
            txtEditAddress.setText("");
            txtEditPort.setText("");
            String tempString =  tempWordRouting.word + "\t\t\t" +
                    tempWordRouting.address + " : " + tempWordRouting.port;
            wordRoutingList.getItems().set(wordRoutingIndex, tempString);
            wordRoutingDatabase.set(wordRoutingIndex, tempWordRouting);
            saveWordRouting(wordRoutingDatabase);
        });

        printNetworkValues = loadPrintToConsole();
        checkBoxPrintToConsole = new CheckBox("Print to console");
        checkBoxPrintToConsole.setSelected(printNetworkValues);
        checkBoxPrintToConsole.setOnAction(event -> {
            printNetworkValues = checkBoxPrintToConsole.isSelected();
            savePrintToConsole(printNetworkValues);
        });
    }

    private void displayDataLayout()
    {
        hBoxBottom.getChildren().add(buttonPlay);
        hBoxBottom.getChildren().add(buttonRecord);
        vBoxRight.getChildren().add(databaseList);
        vBoxRight.getChildren().add(buttonPlayDatabaseWord);
        vBoxRight.getChildren().add(buttonRemoveDatabaseWord);
        vBoxRight.getChildren().add(txtDatabaseWord);
        hBoxBottom.getChildren().add(recordsList);
        hBoxBottom.getChildren().add(txtDetectedWord);
        hBoxBottom.getChildren().add(buttonPlayWord);
        hBoxBottom.getChildren().add(buttonRemoveWord);
        hBoxBottom.getChildren().add(buttonAddWord);
        stackPaneCenter.getChildren().add(lineChart);
        displayedLayout = 0;
        System.out.println("Data Layout displayed.");
    }

    private void hideDataLayout()
    {
        hBoxBottom.getChildren().remove(buttonPlay);
        hBoxBottom.getChildren().remove(buttonRecord);
        vBoxRight.getChildren().remove(databaseList);
        vBoxRight.getChildren().remove(buttonPlayDatabaseWord);
        vBoxRight.getChildren().remove(buttonRemoveDatabaseWord);
        vBoxRight.getChildren().remove(txtDatabaseWord);
        hBoxBottom.getChildren().remove(recordsList);
        hBoxBottom.getChildren().remove(txtDetectedWord);
        hBoxBottom.getChildren().remove(buttonPlayWord);
        hBoxBottom.getChildren().remove(buttonRemoveWord);
        hBoxBottom.getChildren().remove(buttonAddWord);
        stackPaneCenter.getChildren().remove(lineChart);
    }

    private void displayTrainingLayout()
    {
        countWords();
        calculateTopology();
        stackPaneCenter.getChildren().add(trainingList);
        buttonTrain.setDisable(!sameWordCount || topology.size() < 3);
        hBoxBottom.getChildren().add(buttonTrain);
        buttonStopTraining.setDisable(true);
        hBoxBottom.getChildren().add(buttonStopTraining);
        hBoxBottom.getChildren().add(labelTrainingStatus);
        vBoxRight.getChildren().add(labelHiddenTopology);
        vBoxRight.getChildren().add(topologyList);
        vBoxRight.getChildren().add(buttonRemoveTopologyLayer);
        vBoxRight.getChildren().add(labelNewHiddenLayer);
        vBoxRight.getChildren().add(txtHiddenLayer);
        vBoxRight.getChildren().add(buttonAddHiddenLayer);
        vBoxRight.getChildren().add(labelTopology);
        displayedLayout = 1;
        System.out.println("Training Layout displayed.");
    }

    private void hideTrainingLayout()
    {
        stackPaneCenter.getChildren().remove(trainingList);
        hBoxBottom.getChildren().remove(buttonTrain);
        hBoxBottom.getChildren().remove(buttonStopTraining);
        hBoxBottom.getChildren().remove(labelTrainingStatus);
        vBoxRight.getChildren().remove(labelHiddenTopology);
        vBoxRight.getChildren().remove(topologyList);
        vBoxRight.getChildren().remove(buttonRemoveTopologyLayer);
        vBoxRight.getChildren().remove(labelNewHiddenLayer);
        vBoxRight.getChildren().remove(txtHiddenLayer);
        vBoxRight.getChildren().remove(buttonAddHiddenLayer);
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
        stackPaneCenter.getChildren().add(wordRoutingList);
        vBoxRight.getChildren().add(labelNewWordRouting);
        vBoxRight.getChildren().add(txtNewWord);
        vBoxRight.getChildren().add(txtNewAddress);
        vBoxRight.getChildren().add(txtNewPort);
        vBoxRight.getChildren().add(buttonAddWordRouting);
        vBoxRight.getChildren().add(labelEditWordRouting);
        vBoxRight.getChildren().add(txtEditWord);
        vBoxRight.getChildren().add(txtEditAddress);
        vBoxRight.getChildren().add(txtEditPort);
        vBoxRight.getChildren().add(buttonUpdateWordRouting);
        hBoxBottom.getChildren().add(buttonRemoveWordRouting);
        hBoxBottom.getChildren().add(checkBoxPrintToConsole);
        displayedLayout = 3;
        System.out.println("Settings Layout displayed.");
    }

    private void hideSettingsLayout()
    {
        stackPaneCenter.getChildren().remove(wordRoutingList);
        vBoxRight.getChildren().remove(labelNewWordRouting);
        vBoxRight.getChildren().remove(txtNewWord);
        vBoxRight.getChildren().remove(txtNewAddress);
        vBoxRight.getChildren().remove(txtNewPort);
        vBoxRight.getChildren().remove(buttonAddWordRouting);
        vBoxRight.getChildren().remove(labelEditWordRouting);
        vBoxRight.getChildren().remove(txtEditWord);
        vBoxRight.getChildren().remove(txtEditAddress);
        vBoxRight.getChildren().remove(txtEditPort);
        vBoxRight.getChildren().remove(buttonUpdateWordRouting);
        hBoxBottom.getChildren().remove(buttonRemoveWordRouting);
        hBoxBottom.getChildren().remove(checkBoxPrintToConsole);
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
        for (Classifier value : classifier)
            if (value.getCount() > maximum)
                maximum = value.getCount();
        trainingItem.clear();
        for (Classifier value : classifier) {
            if (value.getCount() == maximum)
                trainingItem.add(value.getName() + "\t\t\t\tcount: " + value.getCount() + "\t\t\tOK");
            else {
                trainingItem.add(value.getName() + "\t\t\t\tcount: " + value.getCount() + "\t\t\tMore specimens required!");
                sameWordCount = false;
            }
        }
    }

    private void calculateTopology()
    {
        topology.clear();
        topology.add(maxWordLength/2 + maxWordLength/4);//16bit samples + fft resolution (samples/2)
        for (String s : topologyItem) topology.add(Integer.parseInt(s));
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
            buttonTrain.setDisable(!sameWordCount);
        } else
        {
            labelTopology.setText("\nTopology:\nAdd more layers!");
            buttonTrain.setDisable(true);
        }
    }
}
