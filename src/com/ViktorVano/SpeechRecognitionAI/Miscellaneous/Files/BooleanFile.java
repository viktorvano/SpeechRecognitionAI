package com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Files;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class BooleanFile {
    public static void saveBooleanToFile(String filename, boolean state)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + filename);
            file.createNewFile();
            //Write Content
            FileWriter writer = new FileWriter(file);
            writer.write(Boolean.toString(state));
            writer.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"" + filename + "\" file.");
        }
    }

    public static boolean loadBooleanFromFile(String filename, boolean defaultValue)
    {
        Boolean state;
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + filename);
            Scanner scanner = new Scanner(file);
            if(scanner.hasNextBoolean())
            {
                state = scanner.nextBoolean();
            }else
            {
                state = defaultValue;
                saveBooleanToFile(filename, state);
            }
            scanner.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"" + filename + "\" file.");
            state = defaultValue;
            saveBooleanToFile(filename, state);
        }
        return state;
    }
}
