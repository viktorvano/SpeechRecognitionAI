package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import javafx.collections.ObservableList;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.stream.IntStream;

public class WordRouter extends Thread{
    private String message;
    private ObservableList<WordRouting> wordRoutingList;

    public WordRouter(ObservableList<WordRouting> wordRoutingDatabase, String recognizedMessage)
    {
        this.message = recognizedMessage;
        this.wordRoutingList = wordRoutingDatabase;
        this.start();
    }

    @Override
    public void run() {
        IntStream.range(0, wordRoutingList.size()).parallel().
                forEach(i -> {
                    if(message.contains(wordRoutingList.get(i).word))
                    {
                        try {
                            sendDataToServer(wordRoutingList.get(i).address, Integer.parseInt(wordRoutingList.get(i).port));
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                            System.out.println("Invalid socket!!!\nIP: " + wordRoutingList.get(i).address +
                                    "\nPort: " + wordRoutingList.get(i).port);
                        }
                    }
                });
    }

    private void sendDataToServer(String address, int port)
    {
        try
        {
            // need host and port, we want to connect to the ServerSocket at port X
            Socket socket = new Socket();
            socket.setSoTimeout(3000);
            socket.connect(new InetSocketAddress(address, port), 3000);
            System.out.println("Connected!");

            // get the output stream from the socket.
            OutputStream outputStream = socket.getOutputStream();
            // create a data output stream from the output stream, so we can send data through it
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

            System.out.println("Sending string to the ServerSocket: " + address + " : " + port);

            // write the message we want to send
            dataOutputStream.writeUTF(message);
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
