package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import java.net.*;
import java.io.*;

public class TextClient{
    // initialize socket and input output streams
    private Socket socket            = null;
    private DataOutputStream out     = null;
    private DataInputStream in     = null;
    private String response = "";
    private boolean gotResponse = false;
    private boolean isClientBusy = false;

    // constructor to put ip address and port
    public TextClient(String address, int port, String message)
    {
        isClientBusy = true;
        // establish a connection
        try
        {
            socket = new Socket();
            socket.connect(new InetSocketAddress(address, port), 10000);
            socket.setSoTimeout(10000);
            System.out.println("Connected");

            out = new DataOutputStream(socket.getOutputStream());

            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }

        try
        {
            // write the message we want to send
            if(message != null)
            {
                out.writeUTF(Parameters.token);
                out.writeUTF(message);
                out.flush();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        try{
            System.out.println("Waiting for a response");
            response = in.readUTF();
            gotResponse = true;
        }catch (Exception e)
        {
            response = "Connection timed out.";
            gotResponse = false;
        }
        System.out.println("Response: " + response);

        try {
            if(socket!=null)
                socket.close();
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
        isClientBusy = false;
    }

    public boolean isClientBusy() {
        return isClientBusy;
    }

    public boolean gotResponse() {
        return gotResponse;
    }

    public String getResponse() {
        return response;
    }
}
