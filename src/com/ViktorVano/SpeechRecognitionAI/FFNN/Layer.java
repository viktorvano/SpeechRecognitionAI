package com.ViktorVano.SpeechRecognitionAI.FFNN;

import java.util.ArrayList;

public class Layer extends ArrayList<Neuron> {

    public Layer()
    {
        super();
    }

    @Override
    public boolean add(Neuron aNeuron) {
        return super.add(aNeuron);
    }

    public Neuron pollLast() {
        Neuron neuron = peekFirst();
        this.remove(super.get(this.size()-1));
        return neuron;
    }

    public Neuron peekFirst() {
        return super.get(0);
    }

    public Neuron peekLast() {
        return super.get(this.size()-1);
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
