package com.ViktorVano.SpeechRecognitionAI.FFNN;

import com.ViktorVano.SpeechRecognitionAI.Audio.RecordedAudio;
import com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Classifier;
import javafx.collections.ObservableList;

import java.util.ArrayList;

import static com.ViktorVano.SpeechRecognitionAI.FFNN.GeneralFunctions.showVectorValues;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.input;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.General.normalizeInputs;

public class TrainingThread extends Thread {
    private TrainingData trainingData;
    private NeuralNetwork neuralNetwork;
    private ObservableList<RecordedAudio> trainingDatabase;
    private ArrayList<Classifier> trainingClassifier;
    private boolean trainingFlag;

    public TrainingThread(ObservableList< RecordedAudio > database, ArrayList<Classifier> classifier)
    {
        this.trainingData = new TrainingData();
        this.neuralNetwork = new NeuralNetwork(topology);
        this.trainingDatabase = database;
        this.trainingClassifier = classifier;
        this.trainingFlag = true;
        minimumTrainingCycles = trainingDatabase.size() * 100;
        input = new ArrayList<>();
        target = new ArrayList<>();
        result = new ArrayList<>();
        inputNodes = topology.get(0);
        outputNodes = topology.get(topology.size() - 1);
        this.trainingDatabase = database;
        patternCount = this.trainingDatabase.size();
        generateTrainingData();
    }

    private void generateTrainingData()
    {
        ArrayList<Float> inputLine = new ArrayList<>();
        ArrayList<Float> outputLine = new ArrayList<>();
        for(int i=0; i<trainingDatabase.size(); i++)
        {
            inputLine.clear();
            outputLine.clear();

            normalizeInputs(inputLine, trainingDatabase.get(i));

            for(int output=0; output<outputNodes; output++)
            {
                if(trainingDatabase.get(i).name.equals(trainingClassifier.get(output).getName()))
                    outputLine.add(1.0f);
                else
                    outputLine.add(0.0f);
            }
            learningInputs.add(new ArrayList<>(inputLine));
            learningOutputs.add(new ArrayList<>(outputLine));
        }
    }

    public void stopTraining()
    {
        trainingFlag = false;
    }

    @Override
    public void run() {
        super.run();
        System.out.println("Training started\n");
        neuralNetwork.loadNeuronWeights();
        boolean repeatTrainingCycle = false;
        averageError = 1.0f;
        float currentTrainingError;
        while (true)
        {
            trainingPass++;
            System.out.println("Pass: " + trainingPass);

            //Get new input data and feed it forward:
            if(!repeatTrainingCycle)
                trainingData.getNextInputs(input);
            //showVectorValues("Inputs:", input);
            neuralNetwork.feedForward(input);

            // Train the net what the outputs should have been:
            if(!repeatTrainingCycle)
                trainingData.getTargetOutputs(target);
            showVectorValues("Targets[" + trainingLine +"]=\"" + trainingDatabase.get(trainingLine).name + "\": ", target);
            assert(target.size() == topology.get(topology.size()-1));
            neuralNetwork.backProp(target);//This function alters neurons

            // Collect the net's actual results:
            neuralNetwork.getResults(result);
            showVectorValues("Outputs: ", result);

            trainingLineLabel = trainingLine;
            trainingPassLabel = trainingPass;
            updateTrainingLabel = true;
            currentTrainingError = neuralNetwork.getRecentAverageError();
            currentTrainingErrorLabel = currentTrainingError;

            // Report how well the training is working, averaged over recent samples:
            System.out.println("Net current sample error: " + currentTrainingError);

            if(!trainingFlag)
            {
                System.out.println("Training stopped via Stop button.");
                neuralNetwork.saveNeuronWeights();
                break;
            }else if (currentTrainingError < minimumTrainingError && trainingPass > minimumTrainingCycles)
            {
                System.out.println("Exit due to low error :D\n\n");
                neuralNetwork.saveNeuronWeights();
                break;
            }

            averageError = 0.99f*averageError + 0.01f*currentTrainingError;
            System.out.println("Net average error: " + averageError + "\n\n");
            repeatTrainingCycle = currentTrainingError > averageError;
        }
        System.out.println("Training done.\n");
        System.out.println("Closing application.");
        System.exit(0);
    }
}
