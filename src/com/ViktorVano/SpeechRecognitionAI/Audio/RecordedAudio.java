package com.ViktorVano.SpeechRecognitionAI.Audio;

import java.io.Serializable;

public class RecordedAudio implements Serializable {
    public byte[] audioRecord;
    public int audioRecordLength;
    public String name = "";
}
