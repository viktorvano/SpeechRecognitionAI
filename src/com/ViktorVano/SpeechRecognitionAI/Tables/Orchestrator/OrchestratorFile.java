package com.ViktorVano.SpeechRecognitionAI.Tables.Orchestrator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class OrchestratorFile {
    public static void saveOrchestratorWorkers(ObservableList<OrchestratorWorker> orchestratorWorkers)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + "orchestratorWorkers.dat");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);
            for(int i=0; i<orchestratorWorkers.size(); i++)
                o.writeObject(orchestratorWorkers.get(i));
            o.close();
            f.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"orchestratorWorkers.dat\" file.");
        }
    }

    public static ObservableList<OrchestratorWorker> loadOrchestratorWorkers()
    {
        ObservableList<OrchestratorWorker> orchestratorWorkers = FXCollections.observableArrayList();
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            FileInputStream fi = new FileInputStream("res" + fileSeparator + "orchestratorWorkers.dat");
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
                    orchestratorWorkers.add((OrchestratorWorker) object);
            }

            oi.close();
            fi.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"orchestratorWorkers.dat\" file.");
        }
        return orchestratorWorkers;
    }
}
