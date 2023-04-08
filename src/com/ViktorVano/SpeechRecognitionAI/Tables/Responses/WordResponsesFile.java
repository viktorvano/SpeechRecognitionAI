package com.ViktorVano.SpeechRecognitionAI.Tables.Responses;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class WordResponsesFile {
    public static void saveWordResponses(ObservableList<WordResponse> wordResponses)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + "wordResponses.dat");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);
            for(int i=0; i<wordResponses.size(); i++)
                o.writeObject(wordResponses.get(i));
            o.close();
            f.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"wordResponses.dat\" file.");
        }
    }

    public static ObservableList<WordResponse> loadWordResponses()
    {
        ObservableList<WordResponse> wordResponses = FXCollections.observableArrayList();
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            FileInputStream fi = new FileInputStream("res" + fileSeparator + "wordResponses.dat");
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
                    wordResponses.add((WordResponse) object);
            }

            oi.close();
            fi.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"wordResponses.dat\" file.");
        }
        return wordResponses;
    }
}
