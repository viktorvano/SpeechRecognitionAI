package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import com.ViktorVano.SpeechRecognitionAI.Tables.Commands.WordCommand;
import com.ViktorVano.SpeechRecognitionAI.Tables.Commands.WordCommandRouter;
import com.ViktorVano.SpeechRecognitionAI.Tables.Responses.WordResponse;
import com.ViktorVano.SpeechRecognitionAI.Tables.Routing.WordRouter;
import com.ViktorVano.SpeechRecognitionAI.Tables.Routing.WordRouting;
import com.ViktorVano.SpeechRecognitionAI.Tables.ShellCommands.ShellCommand;
import com.ViktorVano.SpeechRecognitionAI.Tables.ShellCommands.ShellCommander;
import com.ViktorVano.SpeechRecognitionAI.Tables.Webhooks.Webhook;
import com.ViktorVano.SpeechRecognitionAI.Tables.Webhooks.WebhookRouter;
import com.sun.istack.internal.NotNull;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.token;

public class TextServer extends Thread{
    private int port;
    private boolean run = true;
    private String message = "";
    private ObservableList<WordResponse> wordResponsesDatabase;
    private ObservableList<WordRouting> wordRoutingDatabase;
    private ObservableList<WordCommand> wordCommandsDatabase;
    private ObservableList<Webhook> webhookDatabase;
    private ObservableList<ShellCommand> shellCommandDatabase;

    //initialize socket and input stream
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in  = null;
    private DataOutputStream out = null;

    public TextServer(@NotNull ObservableList<WordRouting> wordRoutingDatabase,
                      @NotNull ObservableList<WordCommand> wordCommandsDatabase,
                      @NotNull ObservableList<WordResponse> wordResponsesDatabase,
                      @NotNull ObservableList<Webhook> webhookDatabase,
                      @NotNull ObservableList<ShellCommand> shellCommandDatabase,
                      int port)
    {
        this.wordRoutingDatabase = wordRoutingDatabase;
        this.wordCommandsDatabase = wordCommandsDatabase;
        this.wordResponsesDatabase = wordResponsesDatabase;
        this.webhookDatabase = webhookDatabase;
        this.shellCommandDatabase = shellCommandDatabase;
        this.port = port;
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
                socket.setSoTimeout(10000);

                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                out = new DataOutputStream(socket.getOutputStream());

                String receivedToken = "";
                try
                {
                    try
                    {
                        receivedToken = in.readUTF();
                        this.message = in.readUTF();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(receivedToken.equals(token))
                    {
                        System.out.println("Got the message: " + this.message);
                        new WordRouter(this.wordRoutingDatabase, this.message);
                        new WordCommandRouter(this.wordCommandsDatabase, this.message);
                        new WebhookRouter(this.webhookDatabase, this.message);
                        new ShellCommander(this.shellCommandDatabase, this.message);
                        String response = "";
                        for (WordResponse wordResponse : wordResponsesDatabase)
                        {
                            if(message.contains(wordResponse.word))
                            {
                                if(response.length() == 0)
                                    response += wordResponse.response;
                                else
                                    response += " " + wordResponse.response;
                            }
                        }
                        String outputMessage;
                        if(response.length() == 0)
                            outputMessage = message;
                        else
                            outputMessage = response;

                        if(outputMessage.length() == 0)
                            outputMessage = "No words were recognized.";

                        System.out.println("Sending Response: " + outputMessage);
                        out.writeUTF(outputMessage);
                        out.flush();
                    }
                    else
                    {
                        System.out.println("Invalid TOKEN.");
                        out.writeUTF("Invalid TOKEN.");
                        out.flush();
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
        System.out.println("Text server stopped successfully.");
    }
}
