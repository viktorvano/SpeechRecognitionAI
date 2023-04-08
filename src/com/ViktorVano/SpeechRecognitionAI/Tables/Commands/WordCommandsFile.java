package com.ViktorVano.SpeechRecognitionAI.Tables.Commands;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class WordCommandsFile {
    public static void saveWordCommands(ObservableList<WordCommand> wordCommands)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + "wordCommands.dat");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);
            for(int i=0; i<wordCommands.size(); i++)
                o.writeObject(wordCommands.get(i));
            o.close();
            f.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"wordCommands.dat\" file.");
        }
    }

    public static ObservableList<WordCommand> loadWordCommands()
    {
        ObservableList<WordCommand> wordCommands = FXCollections.observableArrayList();
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            FileInputStream fi = new FileInputStream("res" + fileSeparator + "wordCommands.dat");
            ObjectInputStream oi = new ObjectInputStream(fi);
            Object object;
            while(true)
            {
                try{
                    object = oi.readObject();
                }
                catch(IOException e){
                    break;
                }
                if(object != null)
                    wordCommands.add((WordCommand) object);
            }

            oi.close();
            fi.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"wordCommands.dat\" file.");
        }
        return wordCommands;
    }
}
