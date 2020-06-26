package com.ViktorVano.SpeechRecognitionAI.Audio;

import javax.sound.sampled.AudioFormat;

public class AudioParameters {
    public static AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        int sampleInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleInBits, channels, signed, bigEndian);
    }
}
