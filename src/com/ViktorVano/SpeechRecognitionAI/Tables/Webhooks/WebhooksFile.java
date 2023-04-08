package com.ViktorVano.SpeechRecognitionAI.Tables.Webhooks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class WebhooksFile {
    public static void saveWebhooks(ObservableList<Webhook> webhooks)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + "webhooks.dat");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);
            for(int i=0; i<webhooks.size(); i++)
                o.writeObject(webhooks.get(i));
            o.close();
            f.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"webhooks.dat\" file.");
        }
    }

    public static ObservableList<Webhook> loadWebhooks()
    {
        ObservableList<Webhook> webhooks = FXCollections.observableArrayList();
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            FileInputStream fi = new FileInputStream("res" + fileSeparator + "webhooks.dat");
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
                    webhooks.add((Webhook) object);
            }

            oi.close();
            fi.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"webhooks.dat\" file.");
        }
        return webhooks;
    }
}
