package com.ViktorVano.SpeechRecognitionAI.Tables.Orchestrator;

import com.ViktorVano.SpeechRecognitionAI.Audio.AudioCapture;
import com.ViktorVano.SpeechRecognitionAI.Audio.AudioPlayer;
import com.ViktorVano.SpeechRecognitionAI.Audio.RecordedAudio;
import javafx.collections.ObservableList;

public class OrchestratorRoutine extends Thread{
    private String receivedWorkerMessage = "";
    private RecordedAudio recordedAudio;
    private OrchestratorWorker recognizedOrchestratorWorker;
    private AudioCapture audioCapture;

    public OrchestratorRoutine(RecordedAudio recordedAudio, OrchestratorWorker recognizedOrchestratorWorker, AudioCapture audioCapture)
    {
        this.recordedAudio = recordedAudio;
        this.recognizedOrchestratorWorker = recognizedOrchestratorWorker;
        this.audioCapture = audioCapture;
    }

    @Override
    public void run() {
        if(recordedAudio != null && recordedAudio.audioRecord != null)
        {
            AudioPlayer audioPlayer = new AudioPlayer(audioCapture, recordedAudio);
            audioPlayer.start();
        }

        receivedWorkerMessage = "Received message from a worker.";
    }

    public String getWorkerMessage()
    {
        return this.receivedWorkerMessage;
    }
}
