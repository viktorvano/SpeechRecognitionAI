package com.ViktorVano.SpeechRecognitionAI.Audio;

import com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Parameters;

import java.net.*;
import java.io.*;

public class AudioRecordClient {
    // initialize socket and input output streams
    private Socket socket            = null;
    private DataOutputStream out     = null;
    private DataInputStream in     = null;
    private String response = "";
    private boolean gotResponse = false;
    private boolean isClientBusy = false;

    // constructor to put ip address and port
    public AudioRecordClient(String address, int port, byte[] buffer, int length)
    {
        isClientBusy = true;
        // establish a connection
        try
        {
            socket = new Socket();
            socket.connect(new InetSocketAddress(address, port), 20000);
            socket.setSoTimeout(20000);
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
            if(buffer != null)
            {
                InputStream inn = new ByteArrayInputStream(buffer);
                out.writeUTF(Parameters.token);
                out.writeInt(length);
                int len;
                byte[] b = new byte[1024];
                while ((len = inn.read(b)) != -1) {
                    out.write(b, 0, len);
                }
                out.flush();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        response = "";
        try{
            System.out.println("Waiting for a response");
            response = in.readUTF();
            gotResponse = true;
        }catch (Exception e)
        {
            gotResponse = false;
            System.out.println("AudioRecordClient: Connection timed out.");
        }
        System.out.println("Worker response: " + response);

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
