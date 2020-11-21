package com.ViktorVano.SpeechRecognitionAI.Audio;

import java.io.*;
import javax.sound.sampled.*;

public class AudioCapture {
    private AudioFormat adFormat;
    private TargetDataLine targetDataLine;
    private byte[] mainBuffer = new byte[1000000];
    private int mainBufferLength = 0;
    private int silenceCount = 0;
    private boolean audioRecorded = false;
    private boolean recordAudioFlag = true;

    SourceDataLine sourceLine;
    AudioInputStream inputStream;

    public AudioCapture() {
        captureAudio();
    }

    private void captureAudio() {
        try {
            adFormat = AudioParameters.getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, adFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(adFormat);
            targetDataLine.start();

            Thread captureThread = new Thread(new CaptureThread());
            captureThread.start();
            System.out.println("Started listening.");
        } catch (Exception e) {
            StackTraceElement stackEle[] = e.getStackTrace();
            for (StackTraceElement val : stackEle) {
                System.out.println(val);
            }
            System.exit(0);
        }
    }


    public boolean isAudioRecorded()
    {
        return audioRecorded;
    }

    public byte[] getRecord() {
        if(audioRecorded)
            return mainBuffer;
        else
            return null;
    }

    public int getRecordLength()
    {
        return mainBufferLength;
    }

    public void clearRecord()
    {
        mainBuffer = new byte[1000000];
        mainBufferLength = 0;
        silenceCount = 0;
        audioRecorded = false;
        System.out.println("Listening again");
    }

    public void playRecord()
    {
        Thread playThread;
        InputStream byteInputStream;
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, adFormat);
        System.out.println("Playing: " + mainBufferLength);
        try {
            byteInputStream = new ByteArrayInputStream(mainBuffer);
            inputStream = new AudioInputStream(byteInputStream, adFormat, mainBufferLength / adFormat.getFrameSize());
            sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceLine.open(adFormat);
            sourceLine.start();
            playThread = new Thread(new PlayThread());
            playThread.start();
            while (playThread.isAlive());
            System.out.println("Recording played.");
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public void playRecord(RecordedAudio recordedAudio)
    {
        Thread playThread;
        InputStream byteInputStream;
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, adFormat);
        System.out.println("Playing: " + recordedAudio.audioRecordLength);
        try {
            byteInputStream = new ByteArrayInputStream(recordedAudio.audioRecord);
            inputStream = new AudioInputStream(byteInputStream, adFormat, recordedAudio.audioRecordLength / adFormat.getFrameSize());
            sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceLine.open(adFormat);
            sourceLine.start();
            playThread = new Thread(new PlayThread());
            playThread.start();
            while (playThread.isAlive());
            System.out.println("Recording played.");
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    class CaptureThread extends Thread {

        byte[] tempCaptureBuffer = new byte[10000];

        public void run()
        {
            try
            {
                while (recordAudioFlag)
                {
                    int cnt = targetDataLine.read(tempCaptureBuffer, 0, tempCaptureBuffer.length);
                    if (cnt > 0 && mainBufferLength < 980000)
                    {
                        boolean add = false;
                        for(int i = 0; i< tempCaptureBuffer.length; i++)
                        {
                            if(Math.abs(tempCaptureBuffer[i]) > 100) {
                                add = true;
                                break;
                            }
                        }

                        if(add && !audioRecorded)
                        {
                            System.out.println("Adding a packet");
                            for(int i = 0; i< tempCaptureBuffer.length; i++)
                            {
                                mainBuffer[i + mainBufferLength] = tempCaptureBuffer[i];
                            }
                            mainBufferLength += cnt;
                            System.out.println("Main Buffer Length: " + mainBufferLength);
                            silenceCount = 0;
                        }else if(mainBufferLength > 0 && silenceCount < 3 && mainBufferLength < 950000)
                        {
                            for(int i = 0; i< tempCaptureBuffer.length; i++)
                            {
                                mainBuffer[i + mainBufferLength] = tempCaptureBuffer[i];
                            }
                            mainBufferLength += cnt;
                            System.out.println("Main Buffer Length: " + mainBufferLength);
                            silenceCount ++;
                        }else if (mainBufferLength > 0 && !audioRecorded)
                        {
                            audioRecorded = true;
                            System.out.println("Recording stopped.");
                        }

                    }
                }
            } catch (Exception e)
            {
                System.out.println("CaptureThread::run()" + e);
                System.exit(0);
            }
            targetDataLine.close();
            System.out.println("Audio recording stopped.");
        }
    }

    class PlayThread extends Thread {

        byte[] tempPlayBuffer = new byte[1000000];

        public void run() {
            try {
                int cnt;
                while ((cnt = inputStream.read(tempPlayBuffer, 0, tempPlayBuffer.length)) != -1) {
                    if (cnt > 0) {
                        System.out.println("Played message length: " + cnt);
                        sourceLine.write(tempPlayBuffer, 0, cnt);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}
