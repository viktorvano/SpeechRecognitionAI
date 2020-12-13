package com.ViktorVano.SpeechRecognitionAI.FFNN;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import static com.ViktorVano.SpeechRecognitionAI.FFNN.Variables.*;

public class Neuron {
    public Neuron(int numOutputs, int myIndex)
    {
        m_outputWeights = new ArrayList<>();

        for (int c = 0; c < numOutputs; c++)
        {
            m_outputWeights.add(new Connection());
            m_outputWeights.get(m_outputWeights.size()-1).weight = randomWeight();
        }

        m_myIndex = myIndex;
    }

    public void setOutputValue(float value) { m_outputValue = value; }
    public float getOutputValue() { return m_outputValue; }
    public void feedForward(Layer prevLayer)
    {
        float sum = 0.0f;

        // Sum the previous layer's outputs (which are inputs)
        // Include the bias node from the previous layer.

        for (Neuron neuron : prevLayer)
            sum += neuron.getOutputValue() * neuron.m_outputWeights.get(m_myIndex).weight;

        m_outputValue = Neuron.transferFunction(sum);
    }

    public void calcOutputGradients(float targetValue)
    {
        float delta = targetValue - m_outputValue;
        m_gradient = delta * transferFunctionDerivative(m_outputValue);
    }

    public void calcHiddenGradients(Layer nextLayer)
    {
        float dow = sumDOW(nextLayer);
        m_gradient = dow * transferFunctionDerivative(m_outputValue);
    }

    public void updateInputWeights(Layer prevLayer)
    {
        // The weights to updated are in the Connection container
        // in the neurons in the preceding layer
        for (Neuron neuron : prevLayer) {
            float oldDeltaWeight = neuron.m_outputWeights.get(m_myIndex).deltaWeight;

            float newDeltaWeight =
                    // Individual input, magnified by the gradient and train rate:
                    eta // 0.0==slowlearner; 0.2==medium learner; 1.0==reckless learner
                            * neuron.getOutputValue()
                            * m_gradient
                            // Also add momentum = a fraction of the previous delta weight
                            + alpha // 0.0==no momentum; 0.5==moderate momentum
                            * oldDeltaWeight;
            neuron.m_outputWeights.get(m_myIndex).deltaWeight = newDeltaWeight;
            neuron.m_outputWeights.get(m_myIndex).weight += newDeltaWeight;
        }
    }

    public void saveInputWeights(Layer prevLayer)
    {
        for (Neuron neuron : prevLayer) {
            weights.set(neuronIndex, neuron.m_outputWeights.get(m_myIndex).weight);
            neuronIndex++;
        }

        if (neuronIndex == weights.size())
        {
            //save weights from Weights[] to a file
            System.out.println("Saving weights to weights.dat...");
            try
            {
                File file = new File("res\\weights.dat");
                file.createNewFile();
                FileOutputStream f = new FileOutputStream(file);
                ObjectOutputStream o = new ObjectOutputStream(f);
                for (Float weight : weights) o.writeObject(weight);
                o.close();
                f.close();
            }catch (Exception e)
            {
                System.out.println("Failed to create the \"weights.dat\" file.");
            }
        }
    }

    public void loadInputWeights(Layer prevLayer)
    {
        for (Neuron neuron : prevLayer) {
            neuron.m_outputWeights.get(m_myIndex).weight = weights.get(neuronIndex);
            neuronIndex++;
        }
    }

    private static final float eta = velocity; // [0.0..1.0] overall network training rate
    private static final float alpha = momentum; // [0.0..n] multiplier of last weight change (momentum)

    private float sumDOW(Layer nextLayer)
    {
        float sum = 0.0f;

        // Sum our contributions of the errors at the nodes we feed
        for (int n = 0; n < nextLayer.size() - 1; n++)
        {
            sum += m_outputWeights.get(n).weight * nextLayer.get(n).m_gradient;
        }

        return sum;
    }

    private static float transferFunction(float x)
    {
        // tanh - output range [-1.0..1.0]
        return (float)Math.tanh(x);
    }

    private float transferFunctionDerivative(float x)
    {
        // tanh derivative
        return 1.0f - (float)Math.pow(Math.tanh(x), 2.0);// approximation return 1.0 - x*x;
    }

    private float randomWeight()
    {
        return (float)Math.random()-0.5f;
    }

    private float m_outputValue;
    private final ArrayList<Connection> m_outputWeights;
    private final int m_myIndex;
    private float m_gradient;
}
