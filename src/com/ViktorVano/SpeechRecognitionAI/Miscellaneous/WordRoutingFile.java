package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class WordRoutingFile {
    public static void saveWordRouting(ObservableList<WordRouting> wordRouting)
    {
        try
        {
            File file = new File("res\\wordRouting.dat");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);
            for(int i=0; i<wordRouting.size(); i++)
                o.writeObject(wordRouting.get(i));
            o.close();
            f.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"wordRouting.dat\" file.");
        }
    }

    public static ObservableList<WordRouting> loadWordRouting()
    {
        ObservableList<WordRouting> wordRouting = FXCollections.observableArrayList();
        try
        {
            FileInputStream fi = new FileInputStream(new File("res\\wordRouting.dat"));
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
                    wordRouting.add((WordRouting) object);
            }

            oi.close();
            fi.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"wordRouting.dat\" file.");
        }
        return wordRouting;
    }
}
