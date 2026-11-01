package com.ViktorVano.SpeechRecognitionAI.Audio;

public class BufferWordMarker {
    private int start = 0;
    private int end = 0;
    private int wordIndex = 0;

    public BufferWordMarker(int start, int end, int wordIndex)
    {
        this.start = start;
        this.end = end;
        this.wordIndex = wordIndex;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getWordIndex() {
        return wordIndex;
    }
}
