package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import java.io.*;
import java.util.Scanner;

public class PrintToConsoleFile {
    public static void savePrintToConsole(boolean state)
    {
        try
        {
            File file = new File("res\\printToConsole.dat");
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
            File file = new File("res\\printToConsole.dat");
            Scanner scanner = new Scanner(file);
            if(scanner.hasNextBoolean())
            {
                state = scanner.nextBoolean();
            }else
            {
                state = false;
                savePrintToConsole(false);
            }
            scanner.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"printToConsole.dat\" file.");
            state = false;
            savePrintToConsole(false);
        }
        return state;
    }
}
