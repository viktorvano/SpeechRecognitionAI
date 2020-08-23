package com.ViktorVano.SpeechRecognitionAI.FFNN;

import com.ViktorVano.SpeechRecognitionAI.Audio.RecordedAudio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

import static com.ViktorVano.SpeechRecognitionAI.FFNN.Variables.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.General.normalizeInputs;

public class NeuralNetworkThread extends Thread {
    private NeuralNetwork neuralNetwork;
    private ObservableList<RecordedAudio> records;

    public NeuralNetworkThread()
    {
        this.neuralNetwork = new NeuralNetwork(topology);
        this.neuralNetwork.loadNeuronWeights();
        this.records = FXCollections.observableArrayList();
    }

    @Override
    public void run() {
        super.run();
        while (records.size() > 0)
        {
            normalizeInputs(input, records.get(0));
            neuralNetwork.feedForward(input);
            neuralNetwork.getResults(result);
        }
    }
}
