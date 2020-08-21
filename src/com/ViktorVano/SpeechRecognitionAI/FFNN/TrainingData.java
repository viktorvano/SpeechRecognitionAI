package com.ViktorVano.SpeechRecognitionAI.FFNN;

import java.util.LinkedList;
import static com.ViktorVano.SpeechRecognitionAI.FFNN.Variables.*;

public class TrainingData {

    /*public TrainingData()
    {

    }*/

    public static int getNextInputs(LinkedList<Float> inputValues)
    {
        inputValues.clear();

        /*if (trainingLine >= patternCount)
            trainingLine = 0;*/
        trainingLine = (int)Math.round(Math.random()*(patternCount-1));

        for (int i = 0; i<inputNodes; i++)
            inputValues.add(learningInputs.get(trainingLine).get(i));

        return inputValues.size();
    }
    public static int getTargetOutputs(LinkedList<Float> targetOutValues)
    {
        targetOutValues.clear();

        for (int i = 0; i<outputNodes; i++)
            targetOutValues.add(learningOutputs.get(trainingLine).get(i));

        //trainingLine++;

        return targetOutValues.size();
    }
}
