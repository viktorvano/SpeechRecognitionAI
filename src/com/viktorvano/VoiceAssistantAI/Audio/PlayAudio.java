package com.viktorvano.VoiceAssistantAI.Audio;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class PlayAudio {

    AudioInputStream inputStream;
    SourceDataLine sourceLine;

    public void runVOIP() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(7521);
            byte[] receiveData = new byte[10000];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            Thread playThread;
            AudioFormat adFormat = AudioParameters.getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, adFormat);
            byte audioData[];
            InputStream byteInputStream;
            System.out.println("Started server.");
            while (true) {
                serverSocket.receive(receivePacket);
                System.out.println("RECEIVED: " + receivePacket.getAddress().getHostAddress() + " " + receivePacket.getPort());
                try {
                    audioData = receivePacket.getData();
                    byteInputStream = new ByteArrayInputStream(audioData);
                    inputStream = new AudioInputStream(byteInputStream, adFormat, audioData.length / adFormat.getFrameSize());
                    sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                    sourceLine.open(adFormat);
                    sourceLine.start();
                    playThread = new Thread(new PlayThread());
                    playThread.start();
                } catch (Exception e) {
                    serverSocket.close();
                    System.out.println(e);
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[10000];

        public void run() {
            try {
                int cnt;
                while ((cnt = inputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                    if (cnt > 0) {
                        sourceLine.write(tempBuffer, 0, cnt);
                    }
                }
                //sourceLine.drain();
                //sourceLine.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}
