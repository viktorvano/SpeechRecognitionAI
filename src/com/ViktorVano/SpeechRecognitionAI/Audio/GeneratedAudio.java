package com.ViktorVano.SpeechRecognitionAI.Audio;

public class GeneratedAudio{
    public float loss = 1000000.0f;
    public final int wordIndex;
    public RecordedAudio recordedAudio;
    public GeneratedAudio(RecordedAudio recordedAudio, int wordIndex)
    {
        this.recordedAudio = recordedAudio;
        this.wordIndex = wordIndex;
    }
}
