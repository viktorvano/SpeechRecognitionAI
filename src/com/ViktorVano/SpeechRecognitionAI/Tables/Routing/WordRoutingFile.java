package com.ViktorVano.SpeechRecognitionAI.Tables.Routing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class WordRoutingFile {
    public static void saveWordRouting(ObservableList<WordRouting> wordRouting)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + "wordRouting.dat");
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
            String fileSeparator = System.getProperty("file.separator");
            FileInputStream fi = new FileInputStream("res" + fileSeparator + "wordRouting.dat");
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
