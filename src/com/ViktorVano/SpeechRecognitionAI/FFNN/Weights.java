package com.ViktorVano.SpeechRecognitionAI.FFNN;

import java.util.ArrayList;

import static com.ViktorVano.SpeechRecognitionAI.FFNN.FileManagement.*;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;

public class Weights {
    public static void push_zeros_to_Weights()
    {
        int index, NumberOfWeights = 0;
        int topologySize = topology.size();

        for (index = 0; index < topologySize - 1; index++)
        {
            NumberOfWeights += (topology.get(index) + 1)*topology.get(index + 1);
        }

        weights = new ArrayList<>();

        for (index = 0; index < NumberOfWeights; index++)
        {
            weights.add(0.0f);
        }
    }

    public static void push_zeros_to_Learning_table()
    {
        ArrayList<Float> InputRow = new ArrayList<>();
        ArrayList<Float> OutputRow = new ArrayList<>();
        int row, column;

        learningInputs.clear();
        for (row = 0; row < inputNodes; row++)
        {
            InputRow.add(0.0f);
        }
        for (column = 0; column < patternCount; column++)
        {
            learningInputs.add(InputRow);
        }

        learningOutputs.clear();
        for (row = 0; row < outputNodes; row++)
        {
            OutputRow.add(0.0f);
        }
        for (column = 0; column < patternCount; column++)
        {
            learningOutputs.add(OutputRow);
        }
    }

    public static void get_training_data_count()
    {
        ArrayList<String> fileContent = new ArrayList<>(readOrCreateFile("res\\training.txt"));

        if(fileContent.size()==0 || fileContent==null)
        {
            System.out.println("Cannot open training.txt");
            System.exit(-5);
        }

        int count = 0;

        for(int i = 0; i < fileContent.size(); i++)
        {
            for(int x = 0; x < fileContent.get(i).length(); x++)
            {
                if(fileContent.get(i).charAt(x) == '}')
                    count++;
            }
        }

        if(count % 2 == 0)
            patternCount = count / 2;
        else
        {
            System.out.println("Training data error.");
            System.exit(-6);
        }

    }
}
