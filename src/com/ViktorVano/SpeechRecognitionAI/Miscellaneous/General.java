package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import com.ViktorVano.SpeechRecognitionAI.Audio.RecordedAudio;

import java.util.LinkedList;
import java.util.Random;

import static com.ViktorVano.SpeechRecognitionAI.FFNN.Variables.inputNodes;

public class General {
    public static String randomString(int targetStringLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        if(targetStringLength<1)
            return null;
        else
            return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static void normalizeInputs(LinkedList<Float> inputLine, RecordedAudio recordedAudio)
    {
        float maximum = -9999999999.0f;
        float absValue;
        for(int input=0; input<inputNodes*2; input+=2)
        {
            if(input+1 < recordedAudio.audioRecordLength)
            {
                float value = (float) recordedAudio.audioRecord[input] + (float) recordedAudio.audioRecord[input + 1] * 256.0f;
                inputLine.add(value);
                absValue = Math.abs(value);
                if(absValue > maximum)
                    maximum = absValue;
            }
            else
                inputLine.add(0.0f);
        }

        for(int i=0; i<recordedAudio.audioRecordLength/2; i++)
        {
            inputLine.set(i, inputLine.get(i)/maximum);
        }
    }
}
