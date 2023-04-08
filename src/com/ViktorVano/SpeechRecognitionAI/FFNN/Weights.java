package com.ViktorVano.SpeechRecognitionAI.FFNN;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;

public class Weights {
    public static void allocateNewWeights()
    {
        int index, NumberOfWeights = 0;
        int topologySize = topology.size();

        for (index = 0; index < topologySize - 1; index++)
        {
            NumberOfWeights += (topology.get(index) + 1)*topology.get(index + 1);
        }

        weights = new float[NumberOfWeights];
        System.gc();
    }
}
