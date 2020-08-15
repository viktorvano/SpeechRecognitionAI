package com.ViktorVano.SpeechRecognitionAI.FFNN;

import java.io.*;
import java.util.LinkedList;

public class TopologyFile {
    public static void saveTopology(LinkedList<Integer> topology)
    {
        try
        {
            File file = new File("res\\topology.dat");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);
            for(int i=1; i<topology.size()-1; i++)
                o.writeObject(topology.get(i));
            o.close();
            f.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"topology.dat\" file.");
        }
    }

    public static LinkedList<Integer> loadTopology()
    {
        LinkedList<Integer> topology = new LinkedList<>();
        try
        {
            FileInputStream fi = new FileInputStream(new File("res\\topology.dat"));
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
                    topology.add((Integer) object);
            }

            oi.close();
            fi.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"topology.dat\" file.");
        }
        return topology;
    }
}
