package com.ViktorVano.SpeechRecognitionAI.FFNN;

import java.util.LinkedList;

public class Variables {
    /*
        vector (c++)   <===========> linkedList(java)
        v.front()      <===========> l.peekFirst()
        v.back()       <===========> l.peekLast()
        v.push_back(x) <===========> l.add(x)
        v.pop_back()   <===========> l.pollLast()
    */
    public static int patternCount;
    public static int inputNodes;
    public static int outputNodes;
    public static float velocity = 0.1f; // overall net learning rate [0.0..1.0]
    public static float momentum = 0.5f; // momentum multiplier of last deltaWeight [0.0..n]
    public static float definedRecentAverageSmoothingFactor = 0.0f;


    public static LinkedList<Integer> topology = new LinkedList<>();
    public static final LinkedList<LinkedList<Float>> learningInputs = new LinkedList<>();
    public static final LinkedList<LinkedList<Float>> learningOutputs = new LinkedList<>();
    public static LinkedList<Float> weights = new LinkedList<>();
    public static int neuronIndex = 0;
    public static int trainingLine = 0;// Has to be initialized 0
    public static LinkedList<Float> input, target, result;
    public static int trainingPass = 0;

    public static final int minimumLayerSize = 10;
    public static final int maximumTopologySize = 9;
    public static boolean trainingIsRunning = false, updateTrainingLabel = false;
    public static float currentTrainingError, minimumTrainingError = 0.015f;
    public static int minimumTrainingCycles;
    public static int trainingPassLabel, trainingLineLabel;
    public static float currentTrainingErrorLabel;
    public static float classifierThreshold = 0.3f;
    public static boolean weightsLoaded = false;
    public static int loadingStep = 0;
}
