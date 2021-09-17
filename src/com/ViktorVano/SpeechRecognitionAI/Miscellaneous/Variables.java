package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import java.util.ArrayList;

public class Variables {

    public static int versionNumber = 20210915;

    public static int recorderThreshold = 500;
    public static int wordThreshold = 500;
    public static int preWordSamples = 1200;
    public static int wordInertiaSamples = 250;
    public static int wordInertiaThreshold = 300;

    public static int patternCount;
    public static int inputNodes;
    public static int outputNodes;
    public static float velocity = 0.1f; // overall net learning rate [0.0..1.0]
    public static float momentum = 0.5f; // momentum multiplier of last deltaWeight [0.0..n]


    public static ArrayList<Integer> topology = new ArrayList<>();
    public static final ArrayList<ArrayList<Float>> learningInputs = new ArrayList<>();
    public static final ArrayList<ArrayList<Float>> learningOutputs = new ArrayList<>();
    public static ArrayList<Float> weights = new ArrayList<>();
    public static int neuronIndex = 0;
    public static int trainingLine = 0;// Has to be initialized 0
    public static ArrayList<Float> input, target, result;
    public static int trainingPass = 0;

    public static final int minWordLength = 6000, maxWordLength = 131072;//max length is binary (2^16)*2 (because of 16 bit)
    public static final int minimumLayerSize = 10;
    public static final int maximumTopologySize = 9;
    public static boolean trainingIsRunning = false, updateTrainingLabel = false;
    public static float averageError, minimumTrainingError = 0.008f;
    public static int minimumTrainingCycles;
    public static int trainingPassLabel, trainingLineLabel;
    public static float currentTrainingErrorLabel;
    public static float classifierThreshold = 0.5f;
    public static boolean weightsLoaded = false;
    public static int loadingStep = 0;
    public static boolean printNetworkValues = false;
    public static boolean plotNeuralCharts = false;
}