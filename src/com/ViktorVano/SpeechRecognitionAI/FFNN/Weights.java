package com.ViktorVano.SpeechRecognitionAI.FFNN;

import java.util.ArrayList;
import java.util.Arrays;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;

public class Weights {
    public static void setRandomWeights()
    {
        int index, NumberOfWeights = 0;
        int topologySize = topology.size();

        for (index = 0; index < topologySize - 1; index++)
        {
            NumberOfWeights += (topology.get(index) + 1)*topology.get(index + 1);
        }

        weights = new ArrayList<>(Arrays.asList(new Float[NumberOfWeights]));
    }
}
