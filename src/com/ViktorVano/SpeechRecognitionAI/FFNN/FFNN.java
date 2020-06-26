package com.ViktorVano.SpeechRecognitionAI.FFNN;

import java.util.LinkedList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;


import static com.ViktorVano.SpeechRecognitionAI.FFNN.Variables.*;
import static com.ViktorVano.SpeechRecognitionAI.FFNN.Weights.*;
import static com.ViktorVano.SpeechRecognitionAI.FFNN.GeneralFunctions.*;

public class FFNN extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    private Pane pane;
    private Button[] btnInputs;
    private Button[] btnOutputs;
    private Label[] lblOutputs;
    private double[] inputColor;
    private double[] outputColor;
    private final int width = 1600;
    private final int height = 900;
    private Timeline timelineNeuralNetTrain;
    private Timeline timelineNeuralNetRun;
    private NeuralNetwork myNet;
    private boolean training = true;
    private Button btnRun;
    private LinkedList<LinkedList<Button>> btnHidden = new LinkedList<>();

    @Override
    public void start(Stage stage)
    {
        pane = new Pane();
        Scene scene = new Scene(pane, width, height);

        stage.setTitle("FFNN Visualization");
        stage.setScene(scene);
        stage.show();
        stage.setMaxWidth(stage.getWidth());
        stage.setMinWidth(stage.getWidth());
        stage.setMaxHeight(stage.getHeight());
        stage.setMinHeight(stage.getHeight());
        stage.setResizable(false);

        try
        {
            Image icon = new Image(getClass().getResourceAsStream("../images/neural-network-icon.jpg"));
            stage.getIcons().add(icon);
            System.out.println("Icon loaded from IDE...");
        }catch(Exception e)
        {
            try
            {
                Image icon = new Image("images/neural-network-icon.jpg");
                stage.getIcons().add(icon);
                System.out.println("Icon loaded from exported JAR...");
            }catch(Exception e1)
            {
                System.out.println("Icon failed to load...");
            }

        }

        btnInputs =  new Button[9];
        inputColor = new double[9];
        for(int i=0; i<btnInputs.length; i++)
        {
            btnInputs[i] = new Button();
            btnInputs[i].setPrefSize(50, 50);
            inputColor[i] = 0.0;
            btnInputs[i].setStyle(colorStyle(inputColor[i]));
            btnInputs[i].setText(formatDoubleToString4(inputColor[i]));
            if(i<3)
            {
                btnInputs[i].setLayoutY(340);
                btnInputs[i].setLayoutX(100+60*i);
            }else if(i>=3 && i<6)
            {
                btnInputs[i].setLayoutY(400);
                btnInputs[i].setLayoutX(100+60*(i-3));
            }else if(i>=6 && i<9)
            {
                btnInputs[i].setLayoutY(460);
                btnInputs[i].setLayoutX(100+60*(i-6));
            }
        }

        btnOutputs =  new Button[4];
        outputColor = new double[4];
        lblOutputs = new Label[4];
        for(int i=0; i<btnOutputs.length; i++)
        {
            btnOutputs[i] = new Button();
            btnOutputs[i].setPrefSize(70, 70);
            outputColor[i] = 0.0;
            btnOutputs[i].setStyle(colorStyle(outputColor[i]));
            btnOutputs[i].setText(formatDoubleToString4(outputColor[i]));

            btnOutputs[i].setLayoutX(1420);
            btnOutputs[i].setLayoutY(180+150*i);

            lblOutputs[i] = new Label();
            lblOutputs[i].setLayoutX(1500);
            lblOutputs[i].setLayoutY(180+150*i);
        }

        lblOutputs[0].setText("0  1  0\n1  0  1\n0  1  0");
        lblOutputs[1].setText("0  0  0\n1  1  1\n0  0  0");
        lblOutputs[2].setText("1  0  1\n0  1  0\n1  0  1");
        lblOutputs[3].setText("0  1  0\n1  1  1\n0  1  0");

        btnRun = new Button("Run");
        btnRun.setPrefSize(80, 40);
        btnRun.setLayoutX(50);
        btnRun.setLayoutY(50);
        btnRun.setOnAction(event-> {
            if(training==false)
            {
                timelineNeuralNetRun.play();
            }
        });

        pane.getChildren().addAll(btnInputs);
        pane.getChildren().addAll(btnOutputs);
        pane.getChildren().addAll(lblOutputs);
        pane.getChildren().add(btnRun);

        timelineNeuralNetRun = new Timeline(new KeyFrame[]{new KeyFrame(Duration.millis(20), new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                runCycle();
            }
        })});

        timelineNeuralNetTrain = new Timeline(new KeyFrame[]{new KeyFrame(Duration.millis(250), new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                trainNeuralNet();
                training = false;
                timelineNeuralNetRun.setCycleCount(Timeline.INDEFINITE);
                timelineNeuralNetRun.play();
            }
        })});

        timelineNeuralNetTrain.setCycleCount(1);
        timelineNeuralNetTrain.play();
    }

    private void trainNeuralNet()
    {
        TrainingData trainData = new TrainingData();
        loadTopology();
        if (topology.size() < 3)
        {
            System.out.println("Topology ERROR:\nTopology is too short, may miss some layer.");
            return;
        }

        if (topology.get(0) != 9)
        {
            System.out.println("Topology ERROR:\nNeural network must have 9 inputs.");
            return;
        }

        if (topology.getLast() != 4)
        {
            System.out.println("Topology ERROR:\nNeural network must have 4 outputs.");
            return;
        }

        int x_range, y_range;
        x_range = 900/(topology.size() - 3);
        for(int x=1; x< topology.size()-1; x++)//X = 900 pix range
        {
            btnHidden.add(new LinkedList<>());
            for(int y=0; y<topology.get(x); y++)//Y = 750 pix range
            {
                y_range = 750/topology.get(x);
                btnHidden.get(x-1).add(new Button("0"));
                btnHidden.get(x-1).get(y).setLayoutX(350+(x-1)*x_range);
                btnHidden.get(x-1).get(y).setLayoutY(100+y*y_range);
                btnHidden.get(x-1).get(y).setPrefSize(70, 40);
                btnHidden.get(x-1).get(y).setStyle("-fx-background-color: #000000;");
                pane.getChildren().add(btnHidden.get(x-1).get(y));
            }
        }

        myNet = new NeuralNetwork(topology);

        input = new LinkedList<>();
        target = new LinkedList<>();
        result = new LinkedList<>();
        input.clear();
        target.clear();
        result.clear();

        if(weights.size() != get_number_of_weights_from_file())
        {
            load_training_data_from_file();

            System.out.println("Training started\n");
            while (true)
            {
                trainingPass++;
                System.out.println("Pass: " + trainingPass);

                //Get new input data and feed it forward:
                trainData.getNextInputs(input);
                showVectorValues("Inputs:", input);
                myNet.feedForward(input);

                // Train the net what the outputs should have been:
                trainData.getTargetOutputs(target);
                showVectorValues("Targets: ", target);
                assert(target.size() == topology.peekLast());
                myNet.backProp(target);//This function alters neurons

                // Collect the net's actual results:
                myNet.getResults(result);
                showVectorValues("Outputs: ", result);


                // Report how well the training is working, averaged over recent samples:
                System.out.println("Net recent average error: " + myNet.getRecentAverageError() + "\n\n");

                if (myNet.getRecentAverageError() < 0.00003 && trainingPass>50000)
                {
                    System.out.println("Exit due to low error :D\n\n");
                    myNet.saveNeuronWeights();
                    break;
                }
            }
            System.out.println("Training done.\n");
        }else
        {
            myNet.loadNeuronWeights();
            System.out.println("Weights were loaded from file.\n");
        }

        System.out.println("Run mode begin\n");
        trainingPass = 0;
    }

    private void runCycle()
    {
        //trainingPass++;
        //System.out.println("Run: " + trainingPass);

        //Get new input data and feed it forward:
        //Make sure that your input data are the same size as InputNodes
        input.clear();
        for(int i = 0; i < inputNodes; i++)
        {
            input.add((double)(Math.round(Math.random())));
            inputColor[i] = input.getLast().doubleValue();
            btnInputs[i].setStyle(colorStyle(inputColor[i]));
            btnInputs[i].setText(formatDoubleToString4(inputColor[i]));
        }
        showVectorValues("Inputs:", input);
        myNet.feedForward(input);

        for(int x=1; x< topology.size()-1; x++)//X = 900 pix range
        {
            btnHidden.add(new LinkedList<>());
            for(int y=0; y<topology.get(x); y++)//Y = 750 pix range
            {
                double color = myNet.getNeuronOutput(x, y);
                btnHidden.get(x-1).get(y).setText(formatDoubleToString4(color));
                btnHidden.get(x-1).get(y).setStyle(colorStyle(color));
            }
        }

        // Collect the net's actual results:
        myNet.getResults(result);
        showVectorValues("Outputs: ", result);

        for(int i = 0; i < outputNodes; i++)
        {
            outputColor[i] = result.get(i).doubleValue();
            btnOutputs[i].setStyle(colorStyle(outputColor[i]));
            btnOutputs[i].setText(formatDoubleToString4(outputColor[i]));

            if(outputColor[i]>0.5)
                timelineNeuralNetRun.stop();
        }
    }
}
