package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import java.io.*;
import java.util.Scanner;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;

public class PrintToConsoleFile {
    public static void savePrintToConsole(boolean state)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + "printToConsole.dat");
            file.createNewFile();
            //Write Content
            FileWriter writer = new FileWriter(file);
            writer.write(Boolean.toString(state));
            writer.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"printToConsole.dat\" file.");
        }
    }

    public static boolean loadPrintToConsole()
    {
        Boolean state;
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + "printToConsole.dat");
            Scanner scanner = new Scanner(file);
            if(scanner.hasNextBoolean())
            {
                state = scanner.nextBoolean();
            }else
            {
                state = false;
                savePrintToConsole(printNetworkValues);
            }
            scanner.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"printToConsole.dat\" file.");
            state = false;
            savePrintToConsole(printNetworkValues);
        }
        return state;
    }
}
