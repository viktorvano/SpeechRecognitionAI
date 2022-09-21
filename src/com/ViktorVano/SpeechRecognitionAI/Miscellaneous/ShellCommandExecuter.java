package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import javafx.collections.ObservableList;

import java.io.*;

public class ShellCommandExecuter  extends Thread {
    private String message;
    private ObservableList<ShellCommand> shellCommandList;

    public ShellCommandExecuter(ObservableList<ShellCommand> shellCommandsDatabase, String recognizedMessage)
    {
        this.message = recognizedMessage;
        this.shellCommandList = shellCommandsDatabase;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        for(int i = 0; i< shellCommandList.size(); i++)
        {
            if(message.contains(shellCommandList.get(i).word))
            {
                try {
                    executeShellCommand(shellCommandList.get(i).command);
                }catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Invalid Shell Command!!!\nCommand: " + shellCommandList.get(i).command);
                }
            }
        }
    }

    private void executeShellCommand(String cmd) throws Exception
    {
        String[] args = cmd.split(" ");
        InputStream stdin = Runtime.getRuntime().exec(args).getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);
        String s;
        while ((s = br.readLine()) != null) {
            System.out.println("[Shell Command] " + cmd + " output: " + s);
        }
    }
}