package com.ViktorVano.SpeechRecognitionAI.FFNN;

import com.ViktorVano.SpeechRecognitionAI.Audio.RecordedAudio;
import com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Classifier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.General.normalizeInputs;

public class NeuralNetworkThread extends Thread {
    private final NeuralNetwork neuralNetwork;
    private ObservableList<RecordedAudio> records;
    private final ArrayList<Classifier> classifierOutputs;
    private boolean runThread;
    private String recognizedMessage;

    public NeuralNetworkThread(ArrayList<Classifier> classifier)
    {
        this.neuralNetwork = new NeuralNetwork(topology);
        LoadWeights loadWeights = new LoadWeights();
        loadWeights.start();
        this.records = FXCollections.observableArrayList();
        this.classifierOutputs = classifier;
        this.runThread = false;
        this.recognizedMessage = "";
        if(input == null)
            input = new ArrayList<>();
        else
            input.clear();
        if(result == null)
            result = new ArrayList<>();
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

    private int findMaximumValueIndex(ArrayList<Float> values)
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
            while (!runThread){
                try {
                    this.sleep(50);
                }catch (Exception e)
                {
                    System.out.println("Neural Network Thread sleep went wrong.");
                }
            }
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
                    System.out.println("Word \"" + classifierOutputs.get(maximumIndex).getName() + "\" has " + (result.get(maximumIndex)*100.0f) + "% match.");
                    if (recognizedMessage.length() == 0)
                        recognizedMessage = classifierOutputs.get(maximumIndex).getName();
                    else if(classifierOutputs.get(maximumIndex).getName().length() > 0)//skips in case of an empty string ""
                        recognizedMessage += " " + classifierOutputs.get(maximumIndex).getName();
                }else {
                    System.out.println("Word \"" + classifierOutputs.get(maximumIndex).getName() + "\" has low  match " + (result.get(maximumIndex)*100.0f) + "%.");
                }
                records.remove(0);

                if(printNetworkValues)
                {
                    //From the fist hidden layer to the output layer. Input layer contains just a normalized data.
                    for (int layer = 1; layer < topology.size(); layer++)
                    {
                        System.out.println("\nLayer " + layer);
                        for (int neuron = 0; neuron < topology.get(layer); neuron++)
                            System.out.println(neuralNetwork.getNeuronOutput(layer, neuron));
                    }
                    System.out.println();//just a new line after printing the last layer
                }

                if(plotNeuralCharts)
                {
                    while (displayNeuralChart)
                    {
                        try{
                            Thread.sleep(1);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    for (int layer = 1; layer < topology.size(); layer++)
                    {
                        neuralCharts.add(new XYChart.Series<>());
                        for (int neuron = 0; neuron < topology.get(layer); neuron++)
                            neuralCharts.get(layer).getData().add(new XYChart.Data<>(neuron+1, neuralNetwork.getNeuronOutput(layer, neuron)));
                    }
                    chartClassifierName = classifierOutputs.get(maximumIndex).getName();
                    DecimalFormat df = new DecimalFormat("##.##");
                    chartClassifierMatch = df.format(result.get(maximumIndex)*100.0) + "%";
                    displayNeuralChart = true;
                }
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
