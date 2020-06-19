package com.viktorvano.VoiceAssistantAI.FFNN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;

public class FileManagement {
    public static String[] readFile(String filename)
    {
        File file = new File(filename);

        try
        {
            //Create the file
            if (file.canRead() && file.exists())
            {
                System.out.println("File can be read!");
            } else {
                System.out.println("File does not exist.");
            }

            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String[] data= new String[2];
            data[0] = reader.readLine();
            data[1] = reader.readLine();
            reader.close();
            System.out.println("Reading successful.");
            return data;
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.\n", filename);
            //e.printStackTrace();
            return null;
        }
    }

    public static LinkedList<String> readOrCreateFile(String filename)
    {
        File file = new File(filename);

        try
        {
            //Create the file
            if (file.createNewFile())
            {
                System.out.println("File is created!");
            } else {
                System.out.println("File already exists.");
            }

            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            LinkedList<String> fileContent = new LinkedList<>();
            do {
                line = reader.readLine();
                if(line!=null)
                {
                    fileContent.add(line);
                }
            }while (line!=null);
            reader.close();
            System.out.println("Reading successful.");


            return fileContent;
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeToFile(String filename, String data)
    {
        File file = new File(filename);

        try
        {
            //Create the file
            if (file.createNewFile())
            {
                System.out.println("File is created!");
            } else {
                System.out.println("File already exists.");
            }

            //Write Content
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
            System.out.println("File write successful.");
            return true;
        }
        catch (Exception e)
        {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return false;
        }
    }
}

