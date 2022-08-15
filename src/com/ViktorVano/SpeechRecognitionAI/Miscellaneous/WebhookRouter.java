package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import javafx.collections.ObservableList;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookRouter extends Thread {
    private String message;
    private ObservableList<Webhook> webhookList;

    public WebhookRouter(ObservableList<Webhook> webhooksDatabase, String recognizedMessage)
    {
        this.message = recognizedMessage;
        this.webhookList = webhooksDatabase;
        this.start();
    }

    @Override
    public void run() {
        super.run();
        for(int i = 0; i< webhookList.size(); i++)
        {
            if(message.contains(webhookList.get(i).word))
            {
                try {
                    postToWebhook(webhookList.get(i).url);
                }catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Invalid Webhook!!!\nURL: " + webhookList.get(i).url);
                }
            }
        }
    }

    private void postToWebhook(String url)
    {
        String urlParameters = "";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        HttpURLConnection connection = null;
        try
        {

            URL myURL = new URL(url);
            connection = (HttpURLConnection) myURL.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(800);

            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream()))
            {
                wr.write(postData);
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }

            StringBuilder content;

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream())))
            {

                String line;
                content = new StringBuilder();

                while ((line = br.readLine()) != null)
                {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }

            System.out.println(content);

        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Error posting webhook to on address: " + url);
        }finally
        {
            if(connection != null)
                connection.disconnect();
        }
    }
}