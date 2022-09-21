package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class ShellCommandsFile {
    public static void saveShellCommands(ObservableList<Webhook> shellCommands)
    {
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            File file = new File("res" + fileSeparator + "shellCommands.dat");
            file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);
            for(int i=0; i<shellCommands.size(); i++)
                o.writeObject(shellCommands.get(i));
            o.close();
            f.close();
        }catch (Exception e)
        {
            System.out.println("Failed to create the \"shellCommands.dat\" file.");
        }
    }

    public static ObservableList<ShellCommand> loadShellCommands()
    {
        ObservableList<ShellCommand> shellCommands = FXCollections.observableArrayList();
        try
        {
            String fileSeparator = System.getProperty("file.separator");
            FileInputStream fi = new FileInputStream("res" + fileSeparator + "shellCommands.dat");
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
                    shellCommands.add((ShellCommand) object);
            }

            oi.close();
            fi.close();
        }catch (Exception e)
        {
            System.out.println("Failed to read the \"shellCommands.dat\" file.");
        }
        return shellCommands;
    }
}
