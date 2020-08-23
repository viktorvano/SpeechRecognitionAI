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

    public NeuralNetworkThread(ArrayList<Classifier> classifier)
    {
        this.neuralNetwork = new NeuralNetwork(topology);
        this.loadWeights = new LoadWeights();
        this.loadWeights.start();
        this.records = FXCollections.observableArrayList();
        this.classifierOutputs = classifier;
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
        recognizedMessage = "";
        while (records.size() > 0)
        {
            normalizeInputs(input, records.get(0));
            neuralNetwork.feedForward(input);
            neuralNetwork.getResults(result);
            int maximumIndex = findMaximumValueIndex(result);
            if (result.get(maximumIndex) > classifierThreshold)
            {
                if (recognizedMessage.length() == 0)
                    recognizedMessage = classifierOutputs.get(maximumIndex).getName();
                else
                    recognizedMessage += " " + classifierOutputs.get(maximumIndex).getName();
            }

            records.remove(0);
        }
        wordsRecognizedFlag = true;
    }

    class LoadWeights extends Thread {
        public LoadWeights(){

        }

        @Override
        public void run() {
            super.run();
            neuralNetwork.loadNeuronWeights();
        }
    }
}
