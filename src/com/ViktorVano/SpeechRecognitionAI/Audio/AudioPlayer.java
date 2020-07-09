package com.ViktorVano.SpeechRecognitionAI.Audio;

public class AudioPlayer extends Thread {
    private RecordedAudio record;
    private AudioCapture audio;

    public AudioPlayer(AudioCapture audioCapture, RecordedAudio recordedAudio)
    {
        this.audio = audioCapture;
        this.record = recordedAudio;
    }

    @Override
    public void run() {
        super.run();
        audio.playRecord(record);
    }
}
