package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class StringFile {
    public static void saveStringToFile(String filename, String value)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + filename);
            file.createNewFile();
            //Write Content
            FileWriter writer = new FileWriter(file);
            writer.write(value);
            writer.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"" + filename + "\" file.");
        }
    }

    public static String loadStringFromFile(String filename, String defaultValue)
    {
        String value;
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + filename);
            Scanner scanner = new Scanner(file);
            if(scanner.hasNextLine())
            {
                value = scanner.nextLine();
            }else
            {
                value = defaultValue;
                saveStringToFile(filename, value);
            }
            scanner.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"" + filename + "\" file.");
            value = defaultValue;
            saveStringToFile(filename, value);
        }
        return value;
    }
}
