package com.ViktorVano.SpeechRecognitionAI.FFNN;

import com.ViktorVano.SpeechRecognitionAI.Audio.RecordedAudio;
import com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Classifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.ViktorVano.SpeechRecognitionAI.FFNN.Variables.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.General.normalizeInputs;

public class NeuralNetworkThread extends Thread {
    private NeuralNetwork neuralNetwork;
    private ObservableList<RecordedAudio> records;
    private ArrayList<Classifier> classifierOutputs;
    private LoadWeights loadWeights;
    private boolean runThread;
    private String recognizedMessage;

    public NeuralNetworkThread(ArrayList<Classifier> classifier)
    {
        this.neuralNetwork = new NeuralNetwork(topology);
        this.loadWeights = new LoadWeights();
        this.loadWeights.start();
        this.records = FXCollections.observableArrayList();
        this.classifierOutputs = classifier;
        this.runThread = false;
        this.recognizedMessage = "";
        if(input == null)
            input = new LinkedList<>();
        else
            input.clear();
        if(result == null)
            result = new LinkedList<>();
        else
            result.clear();
        inputNodes = topology.get(0);
        outputNodes = topology.get(topology.size() - 1);
    }

    public boolean isFinished()
    {
        return !runThread;
    }

    public void startAnalysis()
    {
        runThread = true;
    }

    public String getRecognizedMessage()
    {
        return recognizedMessage;
    }

    public void setRecords(ObservableList<RecordedAudio> recordedWords)
    {
        this.records = recordedWords;
    }

    private int findMaximumValueIndex(LinkedList<Float> values)
    {
        int maximumIndex = 0;
        for(int i=0; i<values.size(); i++)
        {
            if(values.get(i) > values.get(maximumIndex))
                maximumIndex = i;
        }
        return maximumIndex;
    }

    @Override
    public void run() {
        super.run();
        while(true)
        {
            System.out.println("Neural Network Thread is waiting.");
            while (!runThread);
            System.out.println("Speech being processed.");
            recognizedMessage = "";
            while (records.size() > 0)
            {
                input.clear();
                normalizeInputs(input, records.get(0));
                neuralNetwork.feedForward(input);
                neuralNetwork.getResults(result);
                int maximumIndex = findMaximumValueIndex(result);
                if (result.get(maximumIndex) > classifierThreshold)
                {
                    System.out.println("Word \"" + classifierOutputs.get(maximumIndex).getName() + "\" had " + (result.get(maximumIndex)*100.0f) + "% match.");
                    if (recognizedMessage.length() == 0)
                        recognizedMessage = classifierOutputs.get(maximumIndex).getName();
                    else
                        recognizedMessage += " " + classifierOutputs.get(maximumIndex).getName();
                }else {
                    System.out.println("Word \"" + classifierOutputs.get(maximumIndex).getName() + "\" had low  match " + (result.get(maximumIndex)*100.0f) + "%.");
                }

                records.remove(0);
            }
            System.out.println("Speech analysed.");
            runThread = false;
        }
    }

    class LoadWeights extends Thread {
        public LoadWeights(){

        }

        @Override
        public void run() {
            super.run();
            neuralNetwork.loadNeuronWeights();
            weightsLoaded = true;
        }
    }
}
