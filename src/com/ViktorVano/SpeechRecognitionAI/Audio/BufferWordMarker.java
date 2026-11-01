package com.ViktorVano.SpeechRecognitionAI.Audio;

public class BufferWordMarker {
    private int bufferMarker = 0;
    private int wordIndex = 0;

    public BufferWordMarker(int end, int wordIndex)
    {
        this.bufferMarker = end + 1;
        this.wordIndex = wordIndex;
    }

    public int getBufferMarker() {
        return bufferMarker;
    }

    public int getWordIndex() {
        return wordIndex;
    }
}
