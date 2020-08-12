package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

public class Classifier {
    private String name;
    private int count;

    public Classifier(String name)
    {
        this.name = name;
        this.count = 1;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount()
    {
        this.count++;
    }
}
