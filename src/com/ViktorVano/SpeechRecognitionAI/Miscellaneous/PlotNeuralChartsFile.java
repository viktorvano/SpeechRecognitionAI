package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import java.io.*;
import java.util.Scanner;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;

public class PlotNeuralChartsFile {
    public static void savePlotNeuralCharts(boolean state)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + "plotNeuralCharts.dat");
            file.createNewFile();
            //Write Content
            FileWriter writer = new FileWriter(file);
            writer.write(Boolean.toString(state));
            writer.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"plotNeuralCharts.dat\" file.");
        }
    }

    public static boolean loadPlotNeuralCharts()
    {
        Boolean state;
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + "plotNeuralCharts.dat");
            Scanner scanner = new Scanner(file);
            if(scanner.hasNextBoolean())
            {
                state = scanner.nextBoolean();
            }else
            {
                state = false;
                savePlotNeuralCharts(plotNeuralCharts);
            }
            scanner.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"plotNeuralCharts.dat\" file.");
            state = false;
            savePlotNeuralCharts(plotNeuralCharts);
        }
        return state;
    }
}
