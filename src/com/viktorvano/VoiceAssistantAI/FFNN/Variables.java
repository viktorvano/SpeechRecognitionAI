package com.viktorvano.VoiceAssistantAI.FFNN;

import java.util.LinkedList;

public class Variables {
    /*
        vector (c++)   <===========> linkedlist(java)
        v.front()      <===========> l.peekFirst()
        v.back()       <===========> l.peekLast()
        v.push_back(x) <===========> l.add(x)
        v.pop_back()   <===========> l.pollLast()
    */
    public static int patternCount;
    public static int inputNodes;
    public static int outputNodes;
    public static double velocity = 0.1; // overall net learning rate [0.0..1.0]
    public static double momentum = 0.5; // momentum multiplier of last deltaWeight [0.0..n]
    public static double definedRecentAverageSmoothingFactor = 0.0;


    public static LinkedList<Integer> topology;
    public static final LinkedList<LinkedList<Double>> learningInputs = new LinkedList<>();
    public static final LinkedList<LinkedList<Double>> learningOutputs = new LinkedList<>();
    public static LinkedList<Double> weights = new LinkedList<>();
    public static int neuronIndex = 0;
    public static int trainingLine = 0;// Has to be initialized 0
    public static LinkedList<Double> input, target, result;
    public static int trainingPass = 0;
}
