package com.ViktorVano.SpeechRecognitionAI.FFNN;

import java.util.LinkedList;

public class Layer extends LinkedList<Neuron> {

    public Layer()
    {
        super();
    }

    @Override
    public boolean add(Neuron aNeuron) {
        return super.add(aNeuron);
    }

    @Override
    public Neuron pollLast() {
        return super.pollLast();
    }

    @Override
    public Neuron peekFirst() {
        return super.peekFirst();
    }

    @Override
    public Neuron peekLast() {
        return super.peekLast();
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public int size() {
        return super.size();
    }
}
