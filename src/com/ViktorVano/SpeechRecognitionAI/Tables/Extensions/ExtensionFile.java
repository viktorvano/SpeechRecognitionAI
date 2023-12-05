package com.ViktorVano.SpeechRecognitionAI.Tables.Extensions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class ExtensionFile {
    public static void saveExtensions(ObservableList<Extension> extensions)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + "extensions.dat");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);
            for(int i=0; i<extensions.size(); i++)
                o.writeObject(extensions.get(i));
            o.close();
            f.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"extensions.dat\" file.");
        }
    }

    public static ObservableList<Extension> loadExtensions()
    {
        ObservableList<Extension> extensions = FXCollections.observableArrayList();
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            FileInputStream fi = new FileInputStream("res" + fileSeparator + "extensions.dat");
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
                    extensions.add((Extension) object);
            }

            oi.close();
            fi.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"extensions.dat\" file.");
        }
        return extensions;
    }
}
