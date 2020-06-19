package com.viktorvano.SpeechRecognitionAI.FFNN;

import java.util.LinkedList;

import static com.viktorvano.SpeechRecognitionAI.FFNN.FileManagement.*;
import static com.viktorvano.SpeechRecognitionAI.FFNN.Variables.*;

public class Weights {
    public static void push_zeros_to_Weights()
    {
        int index, NumberOfWeights = 0;
        int topologySize = topology.size();

        for (index = 0; index < topologySize - 1; index++)
        {
            NumberOfWeights += (topology.get(index) + 1)*topology.get(index + 1);
        }

        weights = new LinkedList<>();

        for (index = 0; index < NumberOfWeights; index++)
        {
            weights.add(0.0);
        }
    }

    public static void push_zeros_to_Learning_table()
    {
        LinkedList<Double> InputRow = new LinkedList<>();
        LinkedList<Double> OutputRow = new LinkedList<>();
        int row, column;

        learningInputs.clear();
        for (row = 0; row < inputNodes; row++)
        {
            InputRow.add(0.0);
        }
        for (column = 0; column < patternCount; column++)
        {
            learningInputs.add(InputRow);
        }

        learningOutputs.clear();
        for (row = 0; row < outputNodes; row++)
        {
            OutputRow.add(0.0);
        }
        for (column = 0; column < patternCount; column++)
        {
            learningOutputs.add(OutputRow);
        }
    }

    public static void get_training_data_count()
    {
        LinkedList<String> fileContent = new LinkedList<>(readOrCreateFile("res\\training.txt"));

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

    public static void loadTopology()
    {
        topology = new LinkedList<>();
        LinkedList<String> fileContent = new LinkedList<>(readOrCreateFile("res\\topology.txt"));

        if(fileContent.size()==0 || fileContent==null)
        {
            System.out.println("Cannot open topology.txt");
            System.exit(-7);
        }

        for(int i = 0; i < fileContent.size(); i++)
        {
            String numberString = new String();
            for(int x = 0; x < fileContent.get(i).length(); x++)
            {
                char c = fileContent.get(i).charAt(x);
                if(c >= '0' && c <= '9')
                {
                    numberString += c;
                }
            }

            if(numberString!=null && numberString.length()!=0)
            {
                topology.add(Integer.parseInt(numberString));
                inputNodes = topology.get(0);
                outputNodes = topology.get(topology.size() - 1);
                get_training_data_count();
                push_zeros_to_Learning_table();
                push_zeros_to_Weights();
            }
        }
    }


    public static void load_training_data_from_file()
    {
        LinkedList<String> fileContent = new LinkedList<>(readOrCreateFile("res\\training.txt"));

        if(fileContent.size()==0 || fileContent==null)
        {
            System.out.println("Cannot open topology.txt");
            System.exit(-8);
        }

        int trainingDataLine = 0;
        for(int fileLine = 0; fileLine < fileContent.size(); fileLine++)
        {
            if(fileContent.get(fileLine).contains("{"))
            {
                String[] bracketContent = fileContent.get(fileLine).split(",");
                LinkedList<Double> inputLine = new LinkedList<>();
                LinkedList<Double> outputLine = new LinkedList<>();
                int flag=0;
                for(int segment = 0; segment < bracketContent.length; segment++)
                {
                    String number = new String();
                    for(int i = 0; i < bracketContent[segment].length(); i++)
                    {
                        char c = bracketContent[segment].charAt(i);
                        if(flag==0 && c=='{')
                            flag=1;
                        else if(flag==1 && c=='{')
                            flag=2;
                        if(c == '+' || c == '-' || c == '.' || (c  >= '0' && c <= '9'))
                            number+=c;
                    }

                    if(number.length() != 0)
                    {
                        try
                        {
                            if(flag==1)
                                inputLine.add(Double.parseDouble(number));
                            else if(flag==2)
                                outputLine.add(Double.parseDouble(number));
                        }catch (Exception e)
                        {
                            System.out.println("Failed to parse number from this string:" + number);
                        }
                    }
                }
                System.out.println("Training =>> inputs:" + inputLine + " outputs: " + outputLine);
                learningInputs.set(trainingDataLine, inputLine);
                learningOutputs.set(trainingDataLine, outputLine);
                trainingDataLine++;
            }
        }

        System.out.println("learningInputs: " + learningInputs);
        System.out.println("learningOutputs: " + learningOutputs);
    }

    public static int get_number_of_weights_from_file()
    {
        int number_of_weights = 0;

        LinkedList<String> fileContent = new LinkedList<>(readOrCreateFile("res\\weights.txt"));

        for (int i = 0; i < fileContent.size(); i++)
        {
            if(fileContent.get(i).length()!=0)
                number_of_weights++;
        }

        return number_of_weights;
    };
}
