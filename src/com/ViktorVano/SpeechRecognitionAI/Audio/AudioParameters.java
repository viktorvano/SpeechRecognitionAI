package com.ViktorVano.SpeechRecognitionAI.Audio;

import javax.sound.sampled.AudioFormat;

public class AudioParameters {
    public static final float sampleRate = 22000.0F;
    public static final int sampleInBits = 16;
    public static AudioFormat getAudioFormat() {
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleInBits, channels, signed, bigEndian);
    }
}
