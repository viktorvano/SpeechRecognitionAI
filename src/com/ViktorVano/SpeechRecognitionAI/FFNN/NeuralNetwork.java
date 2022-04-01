package com.ViktorVano.SpeechRecognitionAI.FFNN;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;
import static com.ViktorVano.SpeechRecognitionAI.FFNN.Weights.setRandomWeights;

public class NeuralNetwork {

    public NeuralNetwork(ArrayList<Integer> topology)
    {
        m_loss = 0;
        m_recentAverageLoss = 0;
        int numLayers = topology.size();
        System.out.println("Number of layers: " + numLayers);
        m_layers = new ArrayList<>();
        for (int layerNum = 0; layerNum < numLayers; layerNum++)
        {
            m_layers.add(new Layer());
            int numOutputs = layerNum == topology.size() - 1 ? 0 : topology.get(layerNum + 1);

            // We have made a new Layer, now fill it with neurons, and add a bias neuron to the layer.
            for (int neuronNum = 0; neuronNum <= topology.get(layerNum); neuronNum++)
            {
                m_layers.get(m_layers.size()-1).add(new Neuron(numOutputs, neuronNum));
                //System.out.println("Made a neuron: " + neuronNum);
            }

            // Force the bias node's output value to 1.0. It's last neuron created above
            m_layers.get(m_layers.size()-1).peekLast().setOutputValue(1.0f);
        }
    }

    public void feedForward(ArrayList<Float> inputValues)
    {
        assert(inputValues.size() == m_layers.get(0).size() - 1);

        // Assign (latch) the input values into the input neurons
        IntStream.range(0, inputValues.size()).parallel().
                forEach(i -> m_layers.get(0).get(i).setOutputValue(inputValues.get(i)));

        // Forward propagate
        for (int layerNum = 1; layerNum < m_layers.size(); layerNum++)
        {
            Layer prevLayer = m_layers.get(layerNum - 1);

            final int finalLayerNum = layerNum;
            IntStream.range(0, m_layers.get(layerNum).size() - 1).parallel().
                    forEach(n -> m_layers.get(finalLayerNum).get(n).feedForward(prevLayer));
        }
    }

    public void backProp(ArrayList<Float> targetValues)
    {
        // Calculate overall net loss (RMS of output neuron losses)
        Layer outputLayer = m_layers.get(m_layers.size()-1);
        m_loss = 0.0f;

        for (int n = 0; n < outputLayer.size() - 1; n++)
        {
            float delta = targetValues.get(n) - outputLayer.get(n).getOutputValue();
            m_loss += delta * delta;
        }
        m_loss /= outputLayer.size() - 1; //get average loss squared
        m_loss = (float)Math.sqrt(m_loss); // RMS

        // Implement a recent average measurement;

        m_recentAverageLoss = m_loss;

        // Calculate output layer gradients
        for (int n = 0; n < outputLayer.size() - 1; n++)
        {
            outputLayer.get(n).calcOutputGradients(targetValues.get(n));
        }

        // Calculate gradients on hidden layers
        for (int layerNum = m_layers.size() - 2; layerNum > 0; layerNum--)
        {
            Layer hiddenLayer = m_layers.get(layerNum);
            Layer nextLayer = m_layers.get(layerNum + 1);

            for (Neuron neuron : hiddenLayer)
                neuron.calcHiddenGradients(nextLayer);
        }

        // For all layers from outputs to first hidden layer.
        // update connection weights

        for (int layerNum = m_layers.size() - 1; layerNum > 0; layerNum--)
        {
            Layer layer = m_layers.get(layerNum);
            Layer prevLayer = m_layers.get(layerNum - 1);

            for (int n = 0; n < layer.size() - 1; n++)
            {
                layer.get(n).updateInputWeights(prevLayer);
            }
        }
    }

    public void getResults(ArrayList<Float> resultValues)
    {
        resultValues.clear();

        for (int n = 0; n < m_layers.get(m_layers.size()-1).size() - 1; n++)
        {
            resultValues.add(m_layers.get(m_layers.size()-1).get(n).getOutputValue());
        }
    }

    public float getNeuronOutput(int layer, int neuron)
    {
        return m_layers.get(layer).get(neuron).getOutputValue();
    }

    public float getRecentAverageLoss() { return m_recentAverageLoss; }

    public void saveNeuronWeights()
    {
        Toolkit.getDefaultToolkit().beep();
        System.out.println("Saving Neuron Weights...");
        savingWeightsPopUp = true;
        updateTrainingLabel = true;
        neuronIndex = 0;
        setRandomWeights();
        // Forward propagate
        for (int layerNum = 1; layerNum < m_layers.size(); layerNum++)
        {
            System.out.println("Reading weights from Layer: " + layerNum);
            Layer prevLayer = m_layers.get(layerNum - 1);
            for (int n = 0; n < m_layers.get(layerNum).size() - 1; n++)
            {
                m_layers.get(layerNum).get(n).saveInputWeights(prevLayer);
            }
        }
        System.out.println("All Weights are Saved.");
        Toolkit.getDefaultToolkit().beep();
    }

    public void loadNeuronWeights()
    {
        System.out.println("Loading Weights...");
        loadingStep = 1;
        neuronIndex = 0;
        setRandomWeights();

        System.out.println("Reading file weights.dat...");
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            FileInputStream fi = new FileInputStream("res" + fileSeparator + "weights.dat");
            ObjectInputStream oi = new ObjectInputStream(fi);
            Object object;
            while(true)
            {
                try{
                    object = oi.readObject();
                }
                catch(IOException e){
                    break;
                }
                if(object != null)
                    weights.set(neuronIndex++,(Float) object);
            }

            oi.close();
            fi.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"weights.dat\" file.");
        }

        loadingStep = 2;
        if(neuronIndex == weights.size())
        {
            System.out.println("Weights loaded.");
        }else
        {
            System.out.println("Weights size did not match with the topology.");
            System.out.println("Loaded only what was there.");
            System.out.println("The rest are random weights.");
        }

        //Transfer Learning: Neural network will load as many neurons as it has in weights, rest are random weights.
        neuronIndex = 0;
        // Forward propagate
        for (int layerNum = 1; layerNum < m_layers.size(); layerNum++)
        {
            System.out.println("Loading Layer: " + layerNum);
            Layer prevLayer = m_layers.get(layerNum - 1);
            for (int n = 0; n < m_layers.get(layerNum).size() - 1; n++)
            {
                m_layers.get(layerNum).get(n).loadInputWeights(prevLayer);
            }
        }

        Toolkit.getDefaultToolkit().beep();
    }

    private final ArrayList<Layer> m_layers; // m_layers[layerNum][neuronNum]
    private float m_loss;
    private float m_recentAverageLoss;
}
