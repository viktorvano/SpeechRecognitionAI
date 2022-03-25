package com.ViktorVano.SpeechRecognitionAI.Audio;

import com.ViktorVano.SpeechRecognitionAI.FFNN.NeuralNetworkThread;
import com.ViktorVano.SpeechRecognitionAI.GUI.SpeechRecognitionAI;
import com.sun.istack.internal.NotNull;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;

public class AudioServer extends Thread{
    private int port;
    private boolean run = true;
    private byte buffer[] = new byte[1000000];
    private AudioCapture audioCapture;
    private SpeechRecognitionAI speechRecognitionAI;
    private NeuralNetworkThread neuralNetworkThread;

    //initialize socket and input stream
    private Socket		 socket = null;
    private ServerSocket server = null;
    private DataInputStream in  = null;
    private DataOutputStream out = null;

    public AudioServer(@NotNull SpeechRecognitionAI speechRecognitionAI,
                       @NotNull AudioCapture audioCapture,
                       @NotNull NeuralNetworkThread neuralNetworkThread,
                       int port){
        this.port = port;
        this.audioCapture = audioCapture;
        this.speechRecognitionAI = speechRecognitionAI;
        this.neuralNetworkThread = neuralNetworkThread;
    }

    public void stopServer()
    {
        this.run = false;
        try {
            if(socket!=null)
                socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(server!=null)
                server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(in!=null)
                in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(out!=null)
                out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        while (run)
        {
            socket = null;
            server = null;
            in	 = null;
            out = null;

            // starts server and waits for a connection
            try
            {
                server = new ServerSocket(port);
                System.out.println("Server started");

                System.out.println("Waiting for a client ...");

                socket = server.accept();
                System.out.println("Client accepted");

                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                out = new DataOutputStream(socket.getOutputStream());

                int length = 0;
                String receivedToken = "";
                try
                {
                    try
                    {
                        receivedToken = in.readUTF();
                        length = in.readInt();
                        System.out.println("Got the Size: " + length);
                        int bytesRead ;
                        int len = 0;
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        while (len<length)
                        {
                            bytesRead = in.read(buffer, 0, (int)Math.min(buffer.length, length-len));
                            len = len + bytesRead;
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                        }
                        buffer = byteArrayOutputStream.toByteArray();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(buffer.length == length && !useHardwareMicrophone)
                    {
                        if(receivedToken.equals(token))
                        {
                            System.out.println("Recording received: " + buffer.length);
                            speechRecognitionAI.captureAudio();
                            audioCapture.setRecordedAudioBuffer(buffer, length);
                            while (audioCapture.isAudioRecorded())
                            {
                                try
                                {
                                    Thread.sleep(100);
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            String message = neuralNetworkThread.getRecognizedMessage();
                            System.out.println("Sending message: " + message);
                            out.writeUTF(neuralNetworkThread.getRecognizedMessage());
                            out.flush();
                        }
                        else
                        {
                            System.out.println("Invalid TOKEN.");
                            out.writeUTF("Invalid TOKEN.");
                            out.flush();
                        }
                    }else
                    {
                        System.out.println("Error receiving a recording...");
                    }
                }
                catch(Exception e)
                {
                    System.out.println(e);
                    e.printStackTrace();
                }

            }
            catch(Exception e)
            {
                System.out.println(e);
            }
            System.out.println("Closing connection");


            try {
                if(socket!=null)
                    socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if(server!=null)
                    server.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if(in!=null)
                    in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if(out!=null)
                    out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server stopped successfully.");
    }
}
