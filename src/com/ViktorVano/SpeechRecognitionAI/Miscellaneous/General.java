package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import java.util.Random;

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
}
