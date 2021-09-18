package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import java.io.*;
import java.util.Scanner;

public class IntegerFile {
    public static void saveIntegerToFile(String filename, int value)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + filename);
            file.createNewFile();
            //Write Content
            FileWriter writer = new FileWriter(file);
            writer.write(Integer.toString(value));
            writer.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"" + filename + "\" file.");
        }
    }

    public static int loadIntegerFromFile(String filename, int defaultValue)
    {
        int value;
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + filename);
            Scanner scanner = new Scanner(file);
            if(scanner.hasNextInt())
            {
                value = scanner.nextInt();
            }else
            {
                value = defaultValue;
                saveIntegerToFile(filename, value);
            }
            scanner.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"" + filename + "\" file.");
            value = defaultValue;
            saveIntegerToFile(filename, value);
        }
        return value;
    }
}
