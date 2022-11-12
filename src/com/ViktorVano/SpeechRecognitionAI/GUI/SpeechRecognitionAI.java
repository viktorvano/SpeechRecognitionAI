package com.ViktorVano.SpeechRecognitionAI.GUI;

import com.ViktorVano.SpeechRecognitionAI.Audio.AudioCapture;
import com.ViktorVano.SpeechRecognitionAI.Audio.AudioPlayer;
import com.ViktorVano.SpeechRecognitionAI.Audio.AudioServer;
import com.ViktorVano.SpeechRecognitionAI.Audio.RecordedAudio;
import com.ViktorVano.SpeechRecognitionAI.FFNN.NeuralNetworkThread;
import com.ViktorVano.SpeechRecognitionAI.FFNN.TrainingThread;
import com.ViktorVano.SpeechRecognitionAI.Miscellaneous.*;
import com.sun.istack.internal.NotNull;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.*;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.BooleanFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.FloatFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.General.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.IntegerFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.StringFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Audio.AudioDatabase.*;
import static com.ViktorVano.SpeechRecognitionAI.FFNN.TopologyFile.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.WebhooksFile.loadWebhooks;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.WordCommandsFile.loadWordCommands;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.WordResponsesFile.loadWordResponses;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.ShellCommandsFile.loadShellCommands;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.WordRoutingFile.*;


public class SpeechRecognitionAI extends Application {
    private Stage stageReference;
    private AudioCapture audioCapture;
    private RecordedAudio recordedAudio;
    private XYChart.Series<Number, Number> displayedSeries, detectedWordsSeries;
    private Timeline timelineTrainingLabelUpdate, timelineDisplayNeuralChart;
    private boolean updateData = true, goodWordCount = false;
    private final BorderPane borderPane = new BorderPane();
    private final StackPane stackPaneCenter = new StackPane();
    private final VBox vBoxRight = new VBox();
    private final FlowPane flow = new FlowPane();
    private final HBox hBoxBottom = new HBox();
    private final Pane settingsPane = new Pane();
    private ObservableList<RecordedAudio> database, records;
    private ObservableList<WordRouting> wordRoutingDatabase;
    private ListView<String> databaseList, recordsList, trainingList, topologyList, wordRoutingList;
    private ObservableList<String> databaseItem, recordItem, trainingItem, topologyItem;
    private TextField txtDetectedWord, txtDatabaseWord, txtHiddenLayer;
    private int recordedWordIndex = -1, databaseWordIndex = -1;
    private LineChart<Number,Number> lineChartAudio, lineChartLoss;
    private Button buttonPlay, buttonRecord, buttonPlayDatabaseWord, buttonRemoveDatabaseWord;
    private Button buttonPlayWord, buttonRemoveWord, buttonAddWord;
    private Button buttonTrain, buttonStopTraining, buttonRemoveTopologyLayer, buttonAddHiddenLayer;
    private int displayedLayout = -1, textFieldTopologyValue = -1, displayMessageCounter = -1;
    private ArrayList<Classifier> classifier;
    private Label labelHiddenTopology, labelNewHiddenLayer, labelTopology, labelTrainingStatus;
    private ImageView[] icons;
    private Label[] labelMenu;
    private NeuralNetworkThread neuralNetworkThread;
    private Label speechRecognitionStatus, speechRecognitionOutput;
    private boolean wordsDetected = false;
    private TrainingThread trainingThread;
    private Button buttonWordRoutingSettings, buttonWordCommands, buttonWordResponses, buttonWebhooks, buttonShellCommands;
    private AudioServer audioServer;
    private TextServer textServer;
    private ObservableList<WordResponse> wordResponsesDatabase;
    private ObservableList<Webhook> webhooksDatabase;
    private ObservableList<ShellCommand> shellCommandsDatabase;
    private ListView<String> wordResponsesList;
    private ObservableList<WordCommand> wordCommandsDatabase;
    private ListView<String> wordCommandsList;
    private ListView<String> webhooksList;
    private ListView<String> shellCommandsList;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        stageReference = stage;
        audioCapture = new AudioCapture();
        recordedAudio = new RecordedAudio();
        createDirectoryIfNotExist("res");
        recorderThreshold = loadIntegerFromFile("recorderThreshold.dat", recorderThreshold);
        wordThreshold = loadIntegerFromFile("wordThreshold.dat", wordThreshold);
        preWordSamples = loadIntegerFromFile("preWordSamples.dat", preWordSamples);
        wordInertiaThreshold = loadIntegerFromFile("wordInertiaThreshold.dat", wordInertiaThreshold);
        wordInertiaSamples = loadIntegerFromFile("wordInertiaSamples.dat", wordInertiaSamples);
        keepLongWords = loadBooleanFromFile("keepLongWords.dat", keepLongWords);
        plotNeuralCharts = loadBooleanFromFile("plotNeuralCharts.dat", plotNeuralCharts);
        printNetworkValues = loadBooleanFromFile("printNetworkValues.dat", printNetworkValues);
        velocity = loadFloatFromFile("velocity.dat", velocity);
        momentum = loadFloatFromFile("momentum.dat", momentum);
        exitTrainingLoss = loadFloatFromFile("exitTrainingLoss.dat", exitTrainingLoss);
        classifierThreshold = loadFloatFromFile("classifierThreshold.dat", classifierThreshold);
        audioServerPort = loadIntegerFromFile("audioServerPort.dat", audioServerPort);
        token = loadStringFromFile("token.dat", token);
        useIpMic = loadBooleanFromFile("useIpMic.dat", useIpMic);

        final int width = 1200;
        final int height = 690;

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

        audioServer = new AudioServer(
                this,
                wordResponsesDatabase,
                audioCapture,
                neuralNetworkThread,
                audioServerPort);
        audioServer.start();

        textServer = new TextServer(
                this.wordRoutingDatabase,
                this.wordCommandsDatabase,
                this.wordResponsesDatabase,
                this.webhooksDatabase,
                this.shellCommandsDatabase,
                audioServerPort+1);
        textServer.start();

        Scene scene = new Scene(borderPane, width, height);

        stage.setTitle("Speech Recognition AI - developed by Viktor Vano (" + versionNumber + ")");
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

        timelineDisplayNeuralChart = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            if(displayNeuralChart && !neuralChartBusy)
            {
                neuralChartBusy = true;
                new NeuralCharts(stageReference, neuralChartSeries, classifier, chartClassifierName, chartClassifierMatch);
                displayNeuralChart = false;
                neuralChartBusy = false;
            }
        }));
        timelineDisplayNeuralChart.setCycleCount(Timeline.INDEFINITE);
        timelineDisplayNeuralChart.play();
    }

    private void detectWords()
    {
        detectedWordsSeries.getData().clear();
        final int detectedValue = 3000;
        int lastValue = 1500;
        int audioSample;
        for (int i = 0; i < recordedAudio.audioRecordLength; i+=2)
        {
            audioSample = recordedAudio.audioRecord[i] + recordedAudio.audioRecord[i+1]*256;
            if (lastValue != detectedValue && Math.abs(audioSample) > wordThreshold)
            {
                if(i>=preWordSamples)
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
                    if(keepLongWords)
                    {
                        try
                        {
                            word++;
                            length = maxWordLength;
                            end = start + maxWordLength - 1;
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
                    }else
                    {
                        detectedWordsSeries.getData().remove(i-1, i+2);
                        System.out.println("Removing a long word: " + length);
                        i--;
                    }
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
        this.textServer.stopServer();
        this.audioServer.stopServer();
        this.audioCapture.stopAudioCapture();
        System.exit(0);
    }

    public void captureAudio()
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
        buttonPlay.setPrefHeight(80);
        buttonPlay.setOnAction(event -> {
            if(recordedAudio != null && recordedAudio.audioRecord != null)
            {
                AudioPlayer audioPlayer = new AudioPlayer(audioCapture, recordedAudio);
                audioPlayer.start();
            }
        });

        buttonRecord = new Button("Record Audio");
        buttonRecord.setPrefHeight(80);
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
        txtDatabaseWord.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue)
                {
                    sortDatabase();
                    saveDatabase(database);
                    databaseWordIndex = -1;
                    txtDatabaseWord.setText("");
                }
            }
        });

        records = FXCollections.observableArrayList();
        recordsList = new ListView<>();
        recordItem = FXCollections.observableArrayList();
        recordsList.setItems(recordItem);
        recordsList.setPrefHeight(80);
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
                sortDatabase();
                saveDatabase(database);
            }
        });

        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        //creating the chart
        lineChartAudio = new LineChart<>(xAxis,yAxis);

        lineChartAudio.setTitle("Audio Data");
        //defining a series
        displayedSeries = new XYChart.Series<>();
        displayedSeries.setName("Recorded Audio");
        detectedWordsSeries = new XYChart.Series<>();
        detectedWordsSeries.setName("Detected Words");
        lineChartAudio.setCreateSymbols(false);
        //populating the series with data
        lineChartAudio.getData().add(displayedSeries);
        lineChartAudio.getData().add(detectedWordsSeries);
        lineChartAudio.setAnimated(false);

        //Also sets displayMessageCounter to 0
        //How long it should keep the displayed message. X*0.25s
        Timeline timelineUpdateData = new Timeline(new KeyFrame(Duration.millis(250), event -> {

            if (updateData && audioCapture.isAudioRecorded()) {
                updateData = false;
                recordedAudio.audioRecord = audioCapture.getRecord();
                int step = recordedAudio.audioRecord.length/5000;//To achieve constant data points in the chart.
                displayedSeries.getData().clear();
                recordedAudio.audioRecordLength = audioCapture.getRecordLength();
                for (int i = 0; i < recordedAudio.audioRecordLength - 1; i+=step) {
                    if(i%2 == 1)
                        i++;

                    if (i < recordedAudio.audioRecordLength - 1)
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
                        new WordCommandRouter(wordCommandsDatabase, neuralNetworkThread.getRecognizedMessage());
                        new WebhookRouter(webhooksDatabase, neuralNetworkThread.getRecognizedMessage());
                        new ShellCommander(shellCommandsDatabase, neuralNetworkThread.getRecognizedMessage());
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
                if (averageLoss < exitTrainingLoss && trainingPassLabel > minimumTrainingCycles
                    || buttonStopTraining.isDisabled()){
                    trainingIsRunning = false;
                    labelTrainingStatus.setText("Training pass: " + trainingPassLabel
                            + "\t\tAverage: " + averageLoss
                            + "\t\tTraining DONE");
                    buttonTrain.setDisable(false);
                    topologyList.setDisable(false);
                    txtHiddenLayer.setDisable(false);
                }else
                {
                    labelTrainingStatus.setText("Training pass: " + trainingPassLabel
                            + "\t\tSample[" + trainingLineLabel + "]=\"" + database.get(trainingLineLabel).name
                            + "\"\t\tLoss: " + currentTrainingLossLabel
                            + "\t\tAverage: " + averageLoss);
                }
                if(savingWeightsPopUp)
                {
                    savingWeightsPopUp = false;
                    customPrompt("Saving Weights", "Average training loss: " + averageLoss, Alert.AlertType.INFORMATION);
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
            stackPaneCenter.getChildren().remove(trainingList);
            stackPaneCenter.getChildren().add(lineChartLoss);
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
        labelTrainingStatus.setFont(Font.font("Arial", 18));

        labelHiddenTopology = new Label("\n Topology of hidden layers ");
        labelHiddenTopology.setFont(Font.font("Arial", 22));
        labelHiddenTopology.setStyle("-fx-font-weight: bold");

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
        labelNewHiddenLayer.setFont(Font.font("Arial", 22));
        labelNewHiddenLayer.setStyle("-fx-font-weight: bold");

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
        labelTopology.setFont(Font.font("Arial", 14));
        countWords();
        calculateTopology();

        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(true);
        //creating the chart
        lineChartLoss = new LineChart<>(xAxis,yAxis);

        lineChartLoss.setTitle("Training Loss");
        //defining a series
        currentLossSeries = new XYChart.Series<>();
        currentLossSeries.setName("Current Loss");
        averageLossSeries = new XYChart.Series<>();
        averageLossSeries.setName("Average Loss");
        lineChartLoss.setCreateSymbols(false);
        //populating the series with data
        lineChartLoss.getData().add(currentLossSeries);
        lineChartLoss.getData().add(averageLossSeries);
        lineChartLoss.setAnimated(false);
    }

    private void initializeRecognitionLayout()
    {
        neuralNetworkThread = new NeuralNetworkThread(classifier);
        speechRecognitionStatus = new Label();
        speechRecognitionStatus.setFont(Font.font("Arial", 20));
        speechRecognitionOutput = new Label();
        speechRecognitionOutput.setFont(Font.font("Arial", 20));
        speechRecognitionOutput.setTextFill(Color.web("#ffffff"));
        speechRecognitionOutput.setStyle("-fx-font-weight: bold");
    }

    private void initializeSettingsLayout()
    {
        wordRoutingDatabase = loadWordRouting();
        wordRoutingList = new ListView<>();

        double paneWidth = stackPaneCenter.getWidth();
        double paneHeight = stackPaneCenter.getHeight();

        ImageView imageView = new ImageView(new Image("/com/ViktorVano/SpeechRecognitionAI/images/word_detection.png"));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(350);
        imageView.setFitHeight(350);
        imageView.setLayoutX(paneWidth * 0.3);
        imageView.setLayoutY(paneHeight * 0.02);
        settingsPane.getChildren().add(imageView);

        Label labelWordDetection =  new Label("Word Detection");
        labelWordDetection.setLayoutX(paneWidth * 0.03);
        labelWordDetection.setLayoutY(paneHeight * 0.01);
        labelWordDetection.setFont(Font.font("Arial", 22));
        labelWordDetection.setStyle("-fx-font-weight: bold");
        settingsPane.getChildren().add(labelWordDetection);

        Label labelStartRecording = new Label("Start Recording");
        labelStartRecording.setLayoutX(paneWidth * 0.03);
        labelStartRecording.setLayoutY(paneHeight * 0.08);
        settingsPane.getChildren().add(labelStartRecording);

        TextField textFieldStartRecording = new TextField();
        textFieldStartRecording.setPromptText(Integer.toString(recorderThreshold));
        textFieldStartRecording.setText(Integer.toString(recorderThreshold));
        textFieldStartRecording.setLayoutX(paneWidth * 0.185);
        textFieldStartRecording.setLayoutY(paneHeight * 0.07);
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
        settingsPane.getChildren().add(textFieldStartRecording);

        Label labelWordThreshold = new Label("Word Threshold");
        labelWordThreshold.setLayoutX(paneWidth * 0.03);
        labelWordThreshold.setLayoutY(paneHeight * 0.15);
        settingsPane.getChildren().add(labelWordThreshold);

        TextField textFieldWordThreshold = new TextField();
        textFieldWordThreshold.setPromptText(Integer.toString(wordThreshold));
        textFieldWordThreshold.setText(Integer.toString(wordThreshold));
        textFieldWordThreshold.setLayoutX(paneWidth * 0.185);
        textFieldWordThreshold.setLayoutY(paneHeight * 0.14);
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
        settingsPane.getChildren().add(textFieldWordThreshold);

        Label labelPreWordSamples = new Label("Pre-Word Samples");
        labelPreWordSamples.setLayoutX(paneWidth * 0.03);
        labelPreWordSamples.setLayoutY(paneHeight * 0.22);
        settingsPane.getChildren().add(labelPreWordSamples);

        TextField textFieldPreWordSamples = new TextField();
        textFieldPreWordSamples.setPromptText(Integer.toString(preWordSamples));
        textFieldPreWordSamples.setText(Integer.toString(preWordSamples));
        textFieldPreWordSamples.setLayoutX(paneWidth * 0.185);
        textFieldPreWordSamples.setLayoutY(paneHeight * 0.21);
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
        settingsPane.getChildren().add(textFieldPreWordSamples);

        Label labelWordInertiaThreshold = new Label("Word Inertia Threshold");
        labelWordInertiaThreshold.setLayoutX(paneWidth * 0.03);
        labelWordInertiaThreshold.setLayoutY(paneHeight * 0.29);
        settingsPane.getChildren().add(labelWordInertiaThreshold);

        TextField textFieldWordInertiaThreshold = new TextField();
        textFieldWordInertiaThreshold.setPromptText(Integer.toString(wordInertiaThreshold));
        textFieldWordInertiaThreshold.setText(Integer.toString(wordInertiaThreshold));
        textFieldWordInertiaThreshold.setLayoutX(paneWidth * 0.185);
        textFieldWordInertiaThreshold.setLayoutY(paneHeight * 0.28);
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
        settingsPane.getChildren().add(textFieldWordInertiaThreshold);

        Label labelWordInertiaSamples = new Label("Word Inertia Samples");
        labelWordInertiaSamples.setLayoutX(paneWidth * 0.03);
        labelWordInertiaSamples.setLayoutY(paneHeight * 0.36);
        settingsPane.getChildren().add(labelWordInertiaSamples);

        TextField textFieldWordInertiaSamples = new TextField();
        textFieldWordInertiaSamples.setPromptText(Integer.toString(wordInertiaSamples));
        textFieldWordInertiaSamples.setText(Integer.toString(wordInertiaSamples));
        textFieldWordInertiaSamples.setLayoutX(paneWidth * 0.185);
        textFieldWordInertiaSamples.setLayoutY(paneHeight * 0.35);
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
        settingsPane.getChildren().add(textFieldWordInertiaSamples);

        Label labelOtherSettings =  new Label("Other Settings");
        labelOtherSettings.setLayoutX(paneWidth * 0.65);
        labelOtherSettings.setLayoutY(paneHeight * 0.01);
        labelOtherSettings.setFont(Font.font("Arial", 22));
        labelOtherSettings.setStyle("-fx-font-weight: bold");
        settingsPane.getChildren().add(labelOtherSettings);

        CheckBox checkBoxPrintToConsole = new CheckBox("Print Neural Network Values To Console");
        checkBoxPrintToConsole.setSelected(printNetworkValues);
        checkBoxPrintToConsole.setOnAction(event -> {
            printNetworkValues = checkBoxPrintToConsole.isSelected();
            saveBooleanToFile("printNetworkValues.dat", printNetworkValues);
        });
        checkBoxPrintToConsole.setLayoutX(paneWidth * 0.65);
        checkBoxPrintToConsole.setLayoutY(paneHeight * 0.08);
        settingsPane.getChildren().add(checkBoxPrintToConsole);

        CheckBox checkBoxPlotNeuralCharts = new CheckBox("Plot Neural Network Charts");
        checkBoxPlotNeuralCharts.setSelected(plotNeuralCharts);
        checkBoxPlotNeuralCharts.setOnAction(event -> {
            plotNeuralCharts = checkBoxPlotNeuralCharts.isSelected();
            saveBooleanToFile("plotNeuralCharts.dat", plotNeuralCharts);
        });
        checkBoxPlotNeuralCharts.setLayoutX(paneWidth * 0.65);
        checkBoxPlotNeuralCharts.setLayoutY(paneHeight * 0.15);
        settingsPane.getChildren().add(checkBoxPlotNeuralCharts);

        CheckBox checkBoxPlotKeepLongWords = new CheckBox("Keep Long Words (But Trim Them)");
        checkBoxPlotKeepLongWords.setSelected(keepLongWords);
        checkBoxPlotKeepLongWords.setOnAction(event -> {
            keepLongWords = checkBoxPlotKeepLongWords.isSelected();
            saveBooleanToFile("keepLongWords.dat", keepLongWords);
        });
        checkBoxPlotKeepLongWords.setLayoutX(paneWidth * 0.65);
        checkBoxPlotKeepLongWords.setLayoutY(paneHeight * 0.22);
        settingsPane.getChildren().add(checkBoxPlotKeepLongWords);

        Label labelIpMic =  new Label("IP Mic App");
        labelIpMic.setLayoutX(paneWidth * 0.65);
        labelIpMic.setLayoutY(paneHeight * 0.5);
        labelIpMic.setFont(Font.font("Arial", 22));
        labelIpMic.setStyle("-fx-font-weight: bold");
        settingsPane.getChildren().add(labelIpMic);

        CheckBox checkBoxUseIpMic = new CheckBox("Use IP Microphone");
        checkBoxUseIpMic.setSelected(useIpMic);
        checkBoxUseIpMic.setDisable(useIpMicOnly);
        checkBoxUseIpMic.setOnAction(event -> {
            useIpMic = checkBoxUseIpMic.isSelected();
            saveBooleanToFile("useIpMic.dat", useIpMic);
        });
        checkBoxUseIpMic.setLayoutX(paneWidth * 0.65);
        checkBoxUseIpMic.setLayoutY(paneHeight * 0.57);
        settingsPane.getChildren().add(checkBoxUseIpMic);

        Label labelToken = new Label("Token");
        labelToken.setLayoutX(paneWidth * 0.65);
        labelToken.setLayoutY(paneHeight * 0.64);
        settingsPane.getChildren().add(labelToken);

        TextField textFieldToken = new TextField();
        textFieldToken.setPromptText(token);
        textFieldToken.setText(token);
        textFieldToken.setLayoutX(paneWidth * 0.7);
        textFieldToken.setLayoutY(paneHeight * 0.63);
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
        settingsPane.getChildren().add(textFieldToken);

        Label labelMicPort = new Label("Audio Server Port\n(restart required)");
        labelMicPort.setLayoutX(paneWidth * 0.65);
        labelMicPort.setLayoutY(paneHeight * 0.71);
        settingsPane.getChildren().add(labelMicPort);

        Label labelTextPort = new Label("Text Server Port       " + (audioServerPort+1));
        labelTextPort.setLayoutX(paneWidth * 0.65);
        labelTextPort.setLayoutY(paneHeight * 0.78);
        settingsPane.getChildren().add(labelTextPort);

        TextField textFieldPort = new TextField();
        textFieldPort.setPromptText(Integer.toString(audioServerPort));
        textFieldPort.setText(Integer.toString(audioServerPort));
        textFieldPort.setLayoutX(paneWidth * 0.78);
        textFieldPort.setLayoutY(paneHeight * 0.7);
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
        settingsPane.getChildren().add(textFieldPort);

        Label labelNeuralNetwork =  new Label("Neural Network");
        labelNeuralNetwork.setLayoutX(paneWidth * 0.03);
        labelNeuralNetwork.setLayoutY(paneHeight * 0.5);
        labelNeuralNetwork.setFont(Font.font("Arial", 22));
        labelNeuralNetwork.setStyle("-fx-font-weight: bold");
        settingsPane.getChildren().add(labelNeuralNetwork);

        Label labelVelocity = new Label("Velocity\n" +
                "(Eta - [0.0..1.0] overall network training rate)");
        labelVelocity.setLayoutX(paneWidth * 0.03);
        labelVelocity.setLayoutY(paneHeight * 0.57);
        settingsPane.getChildren().add(labelVelocity);

        TextField textFieldVelocity = new TextField();
        textFieldVelocity.setPromptText(Float.toString(velocity));
        textFieldVelocity.setText(Float.toString(velocity));
        textFieldVelocity.setLayoutX(paneWidth * 0.185);
        textFieldVelocity.setLayoutY(paneHeight * 0.56);
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
        settingsPane.getChildren().add(textFieldVelocity);

        Label labelMomentum = new Label("Momentum\n" +
                "(Alpha - [0.0..n] multiplier of last weight change)");
        labelMomentum.setLayoutX(paneWidth * 0.03);
        labelMomentum.setLayoutY(paneHeight * 0.66);
        settingsPane.getChildren().add(labelMomentum);

        TextField textFieldMomentum = new TextField();
        textFieldMomentum.setPromptText(Float.toString(momentum));
        textFieldMomentum.setText(Float.toString(momentum));
        textFieldMomentum.setLayoutX(paneWidth * 0.185);
        textFieldMomentum.setLayoutY(paneHeight * 0.65);
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
        settingsPane.getChildren().add(textFieldMomentum);

        Label labelExitTrainingLoss = new Label("Exit Training Loss");
        labelExitTrainingLoss.setLayoutX(paneWidth * 0.03);
        labelExitTrainingLoss.setLayoutY(paneHeight * 0.75);
        settingsPane.getChildren().add(labelExitTrainingLoss);

        TextField textFieldExitTrainingLoss = new TextField();
        textFieldExitTrainingLoss.setPromptText(Float.toString(exitTrainingLoss));
        textFieldExitTrainingLoss.setText(Float.toString(exitTrainingLoss));
        textFieldExitTrainingLoss.setLayoutX(paneWidth * 0.185);
        textFieldExitTrainingLoss.setLayoutY(paneHeight * 0.74);
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
        settingsPane.getChildren().add(textFieldExitTrainingLoss);

        Label labelClassifierMatch = new Label("Classifier Match [%]");
        labelClassifierMatch.setLayoutX(paneWidth * 0.03);
        labelClassifierMatch.setLayoutY(paneHeight * 0.84);
        settingsPane.getChildren().add(labelClassifierMatch);

        TextField textFieldMatch = new TextField();
        textFieldMatch.setPromptText(Integer.toString((int)(classifierThreshold*100.0f)));
        textFieldMatch.setText(Integer.toString((int)(classifierThreshold*100.0f)));
        textFieldMatch.setLayoutX(paneWidth * 0.185);
        textFieldMatch.setLayoutY(paneHeight * 0.83);
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
        settingsPane.getChildren().add(textFieldMatch);

        stackPaneCenter.widthProperty().addListener((observable, oldValue, newValue) -> {
            double paneWidth1 = stackPaneCenter.getWidth();

            imageView.setLayoutX(paneWidth1 * 0.3);
            labelWordDetection.setLayoutX(paneWidth1 * 0.03);
            labelStartRecording.setLayoutX(paneWidth1 * 0.03);
            textFieldStartRecording.setLayoutX(paneWidth1 * 0.185);
            labelWordThreshold.setLayoutX(paneWidth1 * 0.03);
            textFieldWordThreshold.setLayoutX(paneWidth1 * 0.185);
            labelPreWordSamples.setLayoutX(paneWidth1 * 0.03);
            textFieldPreWordSamples.setLayoutX(paneWidth1 * 0.185);
            labelWordInertiaThreshold.setLayoutX(paneWidth1 * 0.03);
            textFieldWordInertiaThreshold.setLayoutX(paneWidth1 * 0.185);
            labelWordInertiaSamples.setLayoutX(paneWidth1 * 0.03);
            textFieldWordInertiaSamples.setLayoutX(paneWidth1 * 0.185);

            labelOtherSettings.setLayoutX(paneWidth1 * 0.65);
            checkBoxPrintToConsole.setLayoutX(paneWidth1 * 0.65);
            checkBoxPlotNeuralCharts.setLayoutX(paneWidth1 * 0.65);
            checkBoxPlotKeepLongWords.setLayoutX(paneWidth1 * 0.65);

            labelIpMic.setLayoutX(paneWidth1 * 0.65);
            checkBoxUseIpMic.setLayoutX(paneWidth1 * 0.65);
            labelToken.setLayoutX(paneWidth1 * 0.65);
            textFieldToken.setLayoutX(paneWidth1 * 0.7);
            labelMicPort.setLayoutX(paneWidth1 * 0.65);
            labelTextPort.setLayoutX(paneWidth1 * 0.65);
            textFieldPort.setLayoutX(paneWidth1 * 0.78);

            labelNeuralNetwork.setLayoutX(paneWidth1 * 0.03);
            labelVelocity.setLayoutX(paneWidth1 * 0.03);
            textFieldVelocity.setLayoutX(paneWidth1 * 0.185);
            labelMomentum.setLayoutX(paneWidth1 * 0.03);
            textFieldMomentum.setLayoutX(paneWidth1 * 0.185);
            labelExitTrainingLoss.setLayoutX(paneWidth1 * 0.03);
            textFieldExitTrainingLoss.setLayoutX(paneWidth1 * 0.185);
            labelClassifierMatch.setLayoutX(paneWidth1 * 0.03);
            textFieldMatch.setLayoutX(paneWidth1 * 0.185);
        });

        stackPaneCenter.heightProperty().addListener((observable, oldValue, newValue) -> {
            double paneHeight1 = stackPaneCenter.getHeight();

            imageView.setLayoutY(paneHeight1 * 0.02);
            labelWordDetection.setLayoutY(paneHeight1 * 0.01);
            labelStartRecording.setLayoutY(paneHeight1 * 0.08);
            textFieldStartRecording.setLayoutY(paneHeight1 * 0.07);
            labelWordThreshold.setLayoutY(paneHeight1 * 0.15);
            textFieldWordThreshold.setLayoutY(paneHeight1 * 0.14);
            labelPreWordSamples.setLayoutY(paneHeight1 * 0.22);
            textFieldPreWordSamples.setLayoutY(paneHeight1 * 0.21);
            labelWordInertiaThreshold.setLayoutY(paneHeight1 * 0.29);
            textFieldWordInertiaThreshold.setLayoutY(paneHeight1 * 0.28);
            labelWordInertiaSamples.setLayoutY(paneHeight1 * 0.36);
            textFieldWordInertiaSamples.setLayoutY(paneHeight1 * 0.35);

            labelOtherSettings.setLayoutY(paneHeight1 * 0.01);
            checkBoxPrintToConsole.setLayoutY(paneHeight1 * 0.08);
            checkBoxPlotNeuralCharts.setLayoutY(paneHeight1 * 0.15);
            checkBoxPlotKeepLongWords.setLayoutY(paneHeight1 * 0.22);

            labelIpMic.setLayoutY(paneHeight1 * 0.5);
            checkBoxUseIpMic.setLayoutY(paneHeight1 * 0.57);
            labelToken.setLayoutY(paneHeight1 * 0.64);
            textFieldToken.setLayoutY(paneHeight1 * 0.63);
            labelMicPort.setLayoutY(paneHeight1 * 0.71);
            labelTextPort.setLayoutY(paneHeight1 * 0.78);
            textFieldPort.setLayoutY(paneHeight1 * 0.7);

            labelNeuralNetwork.setLayoutY(paneHeight1 * 0.5);
            labelVelocity.setLayoutY(paneHeight1 * 0.57);
            textFieldVelocity.setLayoutY(paneHeight1 * 0.56);
            labelMomentum.setLayoutY(paneHeight1 * 0.66);
            textFieldMomentum.setLayoutY(paneHeight1 * 0.65);
            labelExitTrainingLoss.setLayoutY(paneHeight1 * 0.75);
            textFieldExitTrainingLoss.setLayoutY(paneHeight1 * 0.74);
            labelClassifierMatch.setLayoutY(paneHeight1 * 0.84);
            textFieldMatch.setLayoutY(paneHeight1 * 0.83);
        });

        buttonWordRoutingSettings = new Button("Word Routing");
        buttonWordRoutingSettings.setOnAction(event -> {
            new WordRoutingSettingsMenu(stageReference, wordRoutingDatabase, wordRoutingList);
        });

        buttonWordCommands = new Button("Word Commands");
        buttonWordCommands.setOnAction(event -> {
            new WordCommandSettings(stageReference, wordCommandsDatabase, wordCommandsList);
        });

        buttonWordResponses = new Button("Word Responses");
        buttonWordResponses.setOnAction(event -> {
            new WordResponseSettings(stageReference, wordResponsesDatabase, wordResponsesList);
        });

        buttonWebhooks = new Button("Webhooks");
        buttonWebhooks.setOnAction(event -> {
            new WebhookSettings(stageReference, webhooksDatabase, webhooksList);
        });

        buttonShellCommands = new Button("Shell Commands");
        buttonShellCommands.setOnAction(event -> {
            new ShellCommandsSettings(stageReference, shellCommandsDatabase, shellCommandsList);
        });

        wordResponsesDatabase = loadWordResponses();
        wordResponsesList = new ListView<>();

        wordCommandsDatabase = loadWordCommands();
        wordCommandsList = new ListView<>();

        webhooksDatabase = loadWebhooks();
        webhooksList = new ListView<>();

        shellCommandsDatabase = loadShellCommands();
        shellCommandsList = new ListView<>();
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
        stackPaneCenter.getChildren().add(lineChartAudio);
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
        stackPaneCenter.getChildren().remove(lineChartAudio);
    }

    private void displayTrainingLayout()
    {
        countWords();
        calculateTopology();
        stackPaneCenter.getChildren().add(trainingList);
        buttonTrain.setDisable(!goodWordCount || topology.size() < 3);
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
        stackPaneCenter.getChildren().add(lineChartAudio);
        hBoxBottom.getChildren().add(speechRecognitionStatus);
        hBoxBottom.getChildren().add(speechRecognitionOutput);
        displayedLayout = 2;
        System.out.println("Recognition Layout displayed.");
    }

    private void hideRecognitionLayout()
    {
        stackPaneCenter.getChildren().remove(lineChartAudio);
        hBoxBottom.getChildren().remove(speechRecognitionStatus);
        hBoxBottom.getChildren().remove(speechRecognitionOutput);
    }

    private void displaySettingsLayout()
    {
        stackPaneCenter.getChildren().add(settingsPane);
        //stackPaneCenter.getChildren().add(wordRoutingList);
        /*vBoxRight.getChildren().add(labelNewWordRouting);
        vBoxRight.getChildren().add(txtNewWord);
        vBoxRight.getChildren().add(txtNewAddress);
        vBoxRight.getChildren().add(txtNewPort);
        vBoxRight.getChildren().add(buttonAddWordRouting);
        vBoxRight.getChildren().add(labelEditWordRouting);
        vBoxRight.getChildren().add(txtEditWord);
        vBoxRight.getChildren().add(txtEditAddress);
        vBoxRight.getChildren().add(txtEditPort);
        vBoxRight.getChildren().add(buttonUpdateWordRouting);
        vBoxRight.getChildren().add(buttonRemoveWordRouting);*/
        hBoxBottom.getChildren().add(buttonWordRoutingSettings);
        hBoxBottom.getChildren().add(buttonWordCommands);
        hBoxBottom.getChildren().add(buttonWordResponses);
        hBoxBottom.getChildren().add(buttonWebhooks);
        hBoxBottom.getChildren().add(buttonShellCommands);
        displayedLayout = 3;
        System.out.println("Settings Layout displayed.");
    }

    private void hideSettingsLayout()
    {
        stackPaneCenter.getChildren().remove(settingsPane);
        //stackPaneCenter.getChildren().remove(wordRoutingList);
        /*vBoxRight.getChildren().remove(labelNewWordRouting);
        vBoxRight.getChildren().remove(txtNewWord);
        vBoxRight.getChildren().remove(txtNewAddress);
        vBoxRight.getChildren().remove(txtNewPort);
        vBoxRight.getChildren().remove(buttonAddWordRouting);
        vBoxRight.getChildren().remove(labelEditWordRouting);
        vBoxRight.getChildren().remove(txtEditWord);
        vBoxRight.getChildren().remove(txtEditAddress);
        vBoxRight.getChildren().remove(txtEditPort);
        vBoxRight.getChildren().remove(buttonUpdateWordRouting);
        vBoxRight.getChildren().remove(buttonRemoveWordRouting);*/
        hBoxBottom.getChildren().remove(buttonWordRoutingSettings);
        hBoxBottom.getChildren().remove(buttonWordCommands);
        hBoxBottom.getChildren().remove(buttonWordResponses);
        hBoxBottom.getChildren().remove(buttonWebhooks);
        hBoxBottom.getChildren().remove(buttonShellCommands);
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
        goodWordCount = true;
        for (Classifier value : classifier)
            if (value.getCount() > maximum)
                maximum = value.getCount();
        trainingItem.clear();
        for (Classifier value : classifier) {
            if (value.getCount() == maximum/* && value.getCount()%2 == 0*/)
                trainingItem.add(value.getName() + "\t\t\t\tcount: " + value.getCount() + "\t\t\tOK");
            else {
                trainingItem.add(value.getName() + "\t\t\t\tcount: " + value.getCount() + "\t\t\tMore specimens required!");
                goodWordCount = false;
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
            buttonTrain.setDisable(!goodWordCount);
        } else
        {
            labelTopology.setText("\n Topology:\n   Add more layers!");
            buttonTrain.setDisable(true);
        }
    }

    public static void customPrompt(@NotNull String title, @NotNull String message, @NotNull Alert.AlertType alertType)
    {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            Alert alert = new Alert(alertType);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
            if(alertType.equals(Alert.AlertType.ERROR))
                alert.setOnCloseRequest(event1 -> {
                    System.out.println("Leaving app from Error Prompt Handler......");
                    System.exit(-23);
                });
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void sortDatabase()
    {
        Collections.sort(databaseItem);
        ObservableList<RecordedAudio> databaseClone = FXCollections.observableArrayList();
        databaseClone.addAll(database);
        int i = 0, d = 0;
        database.clear();
        while (databaseClone.size() > 0)
        {
            if(databaseItem.get(i).equals(databaseClone.get(d).name))
            {
                database.add(databaseClone.get(d));
                databaseClone.remove(d);
                d = 0;
                i++;
            }
            else
            {
                d++;
            }
        }
    }
}
