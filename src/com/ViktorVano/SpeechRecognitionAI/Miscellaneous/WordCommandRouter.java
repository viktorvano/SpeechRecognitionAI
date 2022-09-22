package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import javafx.collections.ObservableList;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class WordCommandRouter extends Thread {
    private String message;
    private ObservableList<WordCommand> wordCommandsList;

    public WordCommandRouter(ObservableList<WordCommand> wordCommandsDatabase, String recognizedMessage)
    {
        this.message = recognizedMessage;
        this.wordCommandsList = wordCommandsDatabase;
        this.start();
    }

    @Override
    public void run() {
        wordCommandsList.parallelStream().forEach(wordCommand ->
        {
            if(message.contains(wordCommand.word))
            {
                try {
                    sendDataToServer(wordCommand.command, wordCommand.address, Integer.parseInt(wordCommand.port));
                }catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Invalid socket!!!\nIP: " + wordCommand.address +
                            "\nPort: " + wordCommand.port);
                }
            }
        });
    }

    private void sendDataToServer(String command, String address, int port)
    {
        try
        {
            // need host and port, we want to connect to the ServerSocket at port X
            Socket socket = new Socket();
            socket.setSoTimeout(800);
            socket.connect(new InetSocketAddress(address, port), 800);
            System.out.println("Connected!");

            // get the output stream from the socket.
            OutputStream outputStream = socket.getOutputStream();
            // create a data output stream from the output stream, so we can send data through it
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            System.out.println("Sending string to the ServerSocket: " + address + " : " + port);

            // write the message we want to send
            dataOutputStream.writeUTF(command);
            dataOutputStream.flush(); // send the message
            dataOutputStream.close(); // close the output stream when we're done.

            System.out.println("Closing socket.");
            socket.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error sending string to the ServerSocket: " + address + " : " + port);
        }
    }
}
