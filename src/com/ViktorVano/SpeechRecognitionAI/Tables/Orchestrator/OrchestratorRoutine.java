package com.ViktorVano.SpeechRecognitionAI.Tables.Orchestrator;

import com.ViktorVano.SpeechRecognitionAI.Audio.*;

public class OrchestratorRoutine extends Thread{
    private String receivedWorkerMessage = "No worker response received.";
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
            int removeCount = bufferWordMarker.getBufferMarker();
            int newLength = recordedAudio.audioRecord.length - removeCount;
            byte[] trimmedRecordBuffer = new byte[newLength];
            System.arraycopy(recordedAudio.audioRecord, removeCount, trimmedRecordBuffer, 0, newLength);
            trimmedRecord.audioRecord = trimmedRecordBuffer;
            trimmedRecord.audioRecordLength = trimmedRecordBuffer.length;

            AudioPlayer audioPlayer = new AudioPlayer(audioCapture, trimmedRecord);
            audioPlayer.start();

            AudioRecordClient audioRecordClient = new AudioRecordClient(
                    recognizedOrchestratorWorker.address,
                    Integer.parseInt(recognizedOrchestratorWorker.port),
                    trimmedRecord.audioRecord,
                    trimmedRecord.audioRecordLength);
            try
            {
                while(audioRecordClient.isClientBusy())
                {
                    Thread.sleep(50);
                }
                if(audioRecordClient.gotResponse())
                {
                    receivedWorkerMessage = audioRecordClient.getResponse();
                }
            }catch (Exception e)
            {
                System.out.println("OrchestratorRoutine: Audio Client Cannot wait properly.");
            }
        }
    }

    public String getWorkerMessage()
    {
        return this.receivedWorkerMessage;
    }
}
