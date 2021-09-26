package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import java.io.*;
import java.util.Scanner;

public class FloatFile {
    public static void saveFloatToFile(String filename, float value)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + filename);
            file.createNewFile();
            //Write Content
            FileWriter writer = new FileWriter(file);
            writer.write(Float.toString(value));
            writer.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"" + filename + "\" file.");
        }
    }

    public static float loadFloatFromFile(String filename, float defaultValue)
    {
        float value;
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + filename);
            Scanner scanner = new Scanner(file);
            if(scanner.hasNextLine())
            {
                value = Float.parseFloat(scanner.nextLine());
            }else
            {
                value = defaultValue;
                saveFloatToFile(filename, value);
            }
            scanner.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"" + filename + "\" file.");
            value = defaultValue;
            saveFloatToFile(filename, value);
        }
        return value;
    }
}
