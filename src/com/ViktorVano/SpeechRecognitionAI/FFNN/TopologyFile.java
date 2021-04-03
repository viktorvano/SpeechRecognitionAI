package com.ViktorVano.SpeechRecognitionAI.FFNN;

import java.io.*;
import java.util.ArrayList;

public class TopologyFile {
    public static void saveTopology(ArrayList<Integer> topology)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res"+ fileSeparator + "topology.dat");
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

    public static ArrayList<Integer> loadTopology()
    {
        ArrayList<Integer> topology = new ArrayList<>();
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            FileInputStream fi = new FileInputStream("res" + fileSeparator + "topology.dat");
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
