package com.viktorvano.SpeechRecognitionAI.FFNN;

import java.util.LinkedList;
import static com.viktorvano.SpeechRecognitionAI.FFNN.Variables.*;

public class NeuralNetwork {

    public NeuralNetwork(LinkedList<Integer> topology)
    {
        m_error = 0;
        m_recentAverageError = 0;
        m_recentAverageSmoothingFactor = definedRecentAverageSmoothingFactor;
        int numLayers = topology.size();
        System.out.println("Number of layers: " + numLayers);
        m_layers = new LinkedList<Layer>();
        m_layers.clear();
        for (int layerNum = 0; layerNum < numLayers; layerNum++)
        {
            m_layers.add(new Layer());
            int numOutputs = layerNum == topology.size() - 1 ? 0 : topology.get(layerNum + 1);

            // We have made a new Layer, now fill it with neurons, and add a bias neuron to the layer.
            for (int neuronNum = 0; neuronNum <= topology.get(layerNum); neuronNum++)
            {
                m_layers.peekLast().add(new Neuron(numOutputs, neuronNum));
                System.out.println("Made a neuron: " + neuronNum);
            }

            // Force the bias node's output value to 1.0. It's last neuron created above
            m_layers.peekLast().peekLast().setOutputValue(1.0);
        }
    }
    public void feedForward(LinkedList<Double> inputValues)
    {
        assert(inputValues.size() == m_layers.get(0).size() - 1);

        // Assign (latch) the input values into the input neurons
        for (int i = 0; i < inputValues.size(); i++)
        {
            m_layers.get(0).get(i).setOutputValue(inputValues.get(i));
        }

        // Forward propagate
        for (int layerNum = 1; layerNum < m_layers.size(); layerNum++)
        {
            Layer prevLayer = m_layers.get(layerNum - 1);
            for (int n = 0; n < m_layers.get(layerNum).size() - 1; n++)
            {
                m_layers.get(layerNum).get(n).feedForward(prevLayer);
            }
        }
    }
    public void backProp(LinkedList<Double> targetValues)
    {
        // Calculate overall net error (RMS of output neuron errors)
        Layer outputLayer = m_layers.peekLast();
        m_error = 0.0;

        for (int n = 0; n < outputLayer.size() - 1; n++)
        {
            double delta = targetValues.get(n) - outputLayer.get(n).getOutputValue();
            m_error += delta * delta;
        }
        m_error /= outputLayer.size() - 1; //get average errorsquared
        m_error = Math.sqrt(m_error); // RMS

        // Implement a recent average measurement;

        m_recentAverageError =
                (m_recentAverageError * m_recentAverageSmoothingFactor + m_error)
                        / (m_recentAverageSmoothingFactor + 1.0);

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

            for (int n = 0; n < hiddenLayer.size(); n++)
            {
                hiddenLayer.get(n).calcHiddenGradients(nextLayer);
            }
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
    public void getResults(LinkedList<Double> resultValues)
    {
        resultValues.clear();

        for (int n = 0; n < m_layers.peekLast().size() - 1; n++)
        {
            resultValues.add(m_layers.peekLast().get(n).getOutputValue());
        }
    }
    public double getNeuronOutput(int x, int y)
    {
        return m_layers.get(x).get(y).getOutputValue();
    }
    public double getRecentAverageError() { return m_recentAverageError; }

    public void saveNeuronWeights()
    {
        neuronIndex = 0;
        // Forward propagate
        for (int layerNum = 1; layerNum < m_layers.size(); layerNum++)
        {
            Layer prevLayer = m_layers.get(layerNum - 1);
            for (int n = 0; n < m_layers.get(layerNum).size() - 1; n++)
            {
                m_layers.get(layerNum).get(n).saveInputWeights(prevLayer);
            }
        }
    }

    public void loadNeuronWeights()
    {
        neuronIndex = 0;
        // Forward propagate
        for (int layerNum = 1; layerNum < m_layers.size(); layerNum++)
        {
            Layer prevLayer = m_layers.get(layerNum - 1);
            for (int n = 0; n < m_layers.get(layerNum).size() - 1; n++)
            {
                m_layers.get(layerNum).get(n).loadInputWeights(prevLayer);
            }
        }
    }

    private LinkedList<Layer> m_layers; // m_layers[layerNum][neuronNum]
    private double m_error;
    private double m_recentAverageError;
    private double m_recentAverageSmoothingFactor;

}
