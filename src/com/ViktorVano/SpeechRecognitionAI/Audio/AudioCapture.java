package com.ViktorVano.SpeechRecognitionAI.Audio;

import com.sun.istack.internal.NotNull;
import javafx.scene.control.Alert;

import java.io.*;
import javax.sound.sampled.*;

import static com.ViktorVano.SpeechRecognitionAI.GUI.SpeechRecognitionAI.customPrompt;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.BooleanFile.saveBooleanToFile;
import static com.ViktorVano.SpeechRecognitionAI.Miscellaneous.Variables.*;

public class AudioCapture {
    private AudioFormat adFormat;
    private TargetDataLine targetDataLine;
    private byte[] mainBuffer = new byte[1000000];
    private int mainBufferLength = 0;
    private int silenceCount = 0;
    private boolean audioRecorded = false;
    private boolean recordAudioFlag = true;

    SourceDataLine sourceLine;

    public AudioCapture() {
        captureAudio();
    }

    public void stopAudioCapture()
    {
        this.recordAudioFlag = false;
    }

    public void setRecordedAudioBuffer(@NotNull byte[] audioBuffer, int bufferLength)
    {
        this.mainBuffer = audioBuffer;
        this.mainBufferLength = bufferLength;
        this.audioRecorded = true;
    }

    private void captureAudio() {
        try {
            adFormat = AudioParameters.getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, adFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(adFormat);
            targetDataLine.start();

            CaptureThread captureThread = new CaptureThread();
            captureThread.start();
            System.out.println("Started listening.");
        } catch (Exception e) {
            e.printStackTrace();
            useIpMicOnly = true;
            useIpMic = true;
            saveBooleanToFile("useIpMic.dat", true);
            customPrompt("No Hardware Microphone", "Only IP Microphone can be used.", Alert.AlertType.WARNING);
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
        PlayThread playThread;
        InputStream byteInputStream;
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, adFormat);
        System.out.println("Playing: " + mainBufferLength);
        try {
            byteInputStream = new ByteArrayInputStream(mainBuffer);
            AudioInputStream inputStream = new AudioInputStream(byteInputStream, adFormat, mainBufferLength / adFormat.getFrameSize());
            sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceLine.open(adFormat);
            sourceLine.start();
            playThread = new PlayThread(inputStream);
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
        PlayThread playThread;
        InputStream byteInputStream;
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, adFormat);
        System.out.println("Playing: " + recordedAudio.audioRecordLength);
        try {
            byteInputStream = new ByteArrayInputStream(recordedAudio.audioRecord);
            AudioInputStream inputStream = new AudioInputStream(byteInputStream, adFormat, recordedAudio.audioRecordLength / adFormat.getFrameSize());
            sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceLine.open(adFormat);
            sourceLine.start();
            playThread = new PlayThread(inputStream);
            playThread.start();
            while (playThread.isAlive());
            System.out.println("Recording played.");
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    class CaptureThread extends Thread {

        byte[] tempCaptureBuffer = new byte[(int)AudioParameters.sampleRate];

        public void run()
        {
            try
            {
                while (recordAudioFlag)
                {
                    if(!useIpMic)
                    {
                        int cnt = targetDataLine.read(tempCaptureBuffer, 0, tempCaptureBuffer.length);
                        if (cnt > 0 && mainBufferLength < 980000)
                        {
                            boolean addRecording = false;
                            for(int i = 0; i< tempCaptureBuffer.length; i+=2)
                            {
                                if(Math.abs(tempCaptureBuffer[i] + tempCaptureBuffer[i+1]*256) > recorderThreshold) {
                                    addRecording = true;
                                    break;
                                }
                            }

                            if(addRecording && !audioRecorded)
                            {
                                System.out.println("Adding a packet");
                                for(int i = 0; i< tempCaptureBuffer.length; i++)
                                {
                                    mainBuffer[i + mainBufferLength] = tempCaptureBuffer[i];
                                }
                                mainBufferLength += cnt;
                                System.out.println("Main Buffer Length: " + mainBufferLength);
                                silenceCount = 0;
                            }else if(mainBufferLength > 0 && silenceCount < 2 && mainBufferLength < 950000)
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

                        }else if(mainBufferLength >= 980000 && !audioRecorded)
                        {
                            audioRecorded = true;
                            System.out.println("Buffer is full. Recording stopped.");
                        }
                    }
                    else
                    {
                        try{
                            Thread.sleep(50);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
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

        private AudioInputStream inputStream;

        public PlayThread(AudioInputStream inputStream)
        {
            this.inputStream = inputStream;
        }

        public void run() {
            try {
                int cnt;
                while ((cnt = inputStream.read(tempPlayBuffer, 0, tempPlayBuffer.length)) != -1) {
                    if (cnt > 0) {
                        System.out.println("Played message length: " + cnt);
                        sourceLine.write(tempPlayBuffer, 0, cnt);
                    }
                }
                inputStream.close();
            } catch (Exception e) {
                System.out.println(e);
                customPrompt("Problem playing audio", e.toString(), Alert.AlertType.WARNING);
            }
        }
    }
}
