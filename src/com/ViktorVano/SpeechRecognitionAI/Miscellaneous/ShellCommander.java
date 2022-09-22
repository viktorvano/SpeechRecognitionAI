package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import javafx.collections.ObservableList;

import java.io.*;

public class ShellCommander extends Thread {
    private String message;
    private ObservableList<ShellCommand> shellCommandList;

    public ShellCommander(ObservableList<ShellCommand> shellCommandsDatabase, String recognizedMessage)
    {
        this.message = recognizedMessage;
        this.shellCommandList = shellCommandsDatabase;
        this.start();
    }

    @Override
    public void run() {
        shellCommandList.parallelStream().forEach(shellCommand ->
        {
            if(message.contains(shellCommand.word))
            {
                try {
                    executeShellCommand(shellCommand.command);
                }catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Invalid Shell Command!!!\nCommand: " + shellCommand.command);
                }
            }
        });
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