package com.ViktorVano.SpeechRecognitionAI.Tables.Extensions;

import javafx.collections.ObservableList;

public class ExtensionHandler extends Thread{
    private String message;
    private String response;
    private ObservableList<Extension> extensionsDatabase;

    public ExtensionHandler(ObservableList<Extension> extensionsDatabase, String recognizedMessage, String response)
    {
        this.message = recognizedMessage;
        this.extensionsDatabase = extensionsDatabase;
        this.response = response;
        this.start();
    }

    @Override
    public void run() {
        extensionsDatabase.parallelStream().forEach(wordRouting ->
        {
            if(message.contains(wordRouting.word))
            {
                try {

                }catch (Exception e)
                {
                    e.printStackTrace();
                    /*System.out.println("Invalid socket!!!\nIP: " + wordRouting.address +
                            "\nPort: " + wordRouting.port);*/
                }
            }
        });
    }
}
