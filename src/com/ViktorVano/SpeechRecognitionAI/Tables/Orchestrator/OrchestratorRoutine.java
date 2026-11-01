package com.ViktorVano.SpeechRecognitionAI.Tables.Orchestrator;

import com.ViktorVano.SpeechRecognitionAI.Audio.AudioCapture;
import com.ViktorVano.SpeechRecognitionAI.Audio.AudioPlayer;
import com.ViktorVano.SpeechRecognitionAI.Audio.BufferWordMarker;
import com.ViktorVano.SpeechRecognitionAI.Audio.RecordedAudio;
import javafx.collections.ObservableList;

public class OrchestratorRoutine extends Thread{
    private String receivedWorkerMessage = "";
    private RecordedAudio recordedAudio;
    private OrchestratorWorker recognizedOrchestratorWorker;
    private AudioCapture audioCapture;
    private BufferWordMarker bufferWordMarker;

    public OrchestratorRoutine(RecordedAudio recordedAudio, OrchestratorWorker recognizedOrchestratorWorker, BufferWordMarker bufferWordMarker, AudioCapture audioCapture)
    {
        this.recordedAudio = recordedAudio;
        this.recognizedOrchestratorWorker = recognizedOrchestratorWorker;
        this.audioCapture = audioCapture;
        this.bufferWordMarker = bufferWordMarker;
    }

    @Override
    public void run() {
        if(recordedAudio != null && recordedAudio.audioRecord != null)
        {
            // trim the audio
            RecordedAudio trimmedRecord = new RecordedAudio();
            int removeCount = bufferWordMarker.getEnd() + 1;
            int newLength = recordedAudio.audioRecord.length - removeCount;
            byte[] trimmedRecordBuffer = new byte[newLength];
            System.arraycopy(recordedAudio.audioRecord, removeCount, trimmedRecordBuffer, 0, newLength);
            trimmedRecord.audioRecord = trimmedRecordBuffer;
            trimmedRecord.audioRecordLength = trimmedRecordBuffer.length;

            AudioPlayer audioPlayer = new AudioPlayer(audioCapture, trimmedRecord);
            audioPlayer.start();
        }

        receivedWorkerMessage = "Received message from a worker " + bufferWordMarker.getWordIndex() + ".";
    }

    public String getWorkerMessage()
    {
        return this.receivedWorkerMessage;
    }
}
