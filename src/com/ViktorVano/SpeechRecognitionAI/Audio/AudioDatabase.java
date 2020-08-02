package com.ViktorVano.SpeechRecognitionAI.Audio;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class AudioDatabase {
    public static void saveDatabase(ObservableList<RecordedAudio> Database)
    {
        try
        {
            File file = new File("res\\database.dat");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);
            for(RecordedAudio r : Database)
                o.writeObject(r);
            o.close();
            f.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"database.dat\" file.");
        }
    }

    public static ObservableList<RecordedAudio> loadDatabase()
    {
        ObservableList<RecordedAudio> database = FXCollections.observableArrayList();
        try
        {
            FileInputStream fi = new FileInputStream(new File("res\\database.dat"));
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
                    database.add((RecordedAudio) object);
            }

            oi.close();
            fi.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"database.dat\" file.");
        }
        return database;
    }
}
