package com.ViktorVano.SpeechRecognitionAI.Miscellaneous;

import com.ViktorVano.SpeechRecognitionAI.Audio.RecordedAudio;

import java.util.ArrayList;
import java.util.Random;

import static com.ViktorVano.SpeechRecognitionAI.FFNN.Variables.inputNodes;

public class General {
    public static final float  pi = (float)Math.atan2(1, 1) * 4.0f;//calculated pi

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

    public static void normalizeInputs(ArrayList<Float> inputLine, RecordedAudio recordedAudio)
    {
        float sampleValue;
        float[] absArray = new float[recordedAudio.audioRecordLength/2];
        float[] realSamples = new float[(int)((double)inputNodes * (2.0/3.0))];
        int a = 0;
        //System.out.println("\nAudio Sample");
        for(int input=0; input<recordedAudio.audioRecordLength; input+=2)//calculating absolute 16 bit values
        {
            if(input+1 < recordedAudio.audioRecordLength)
            {
                sampleValue = (float) recordedAudio.audioRecord[input]
                        + (float) recordedAudio.audioRecord[input + 1] * 256.0f;
                realSamples[a] = sampleValue;
                absArray[a++] = Math.abs(sampleValue);
                //System.out.println(sampleValue);
            }else
                realSamples[a++] = 0;
        }

        float[] filteredArray = new float[absArray.length];
        float filter = 0.0f;
        for(int i=0; i<filteredArray.length; i++)//filtering values
        {
            filter = 0.98f*filter + 0.02f*absArray[i];
            filteredArray[i] = filter;
        }

        float FilteredMaximum = -9.0f;
        for(int i=0; i<filteredArray.length; i++)//look for a maximum
        {
            if(filteredArray[i] > FilteredMaximum)
                FilteredMaximum = filteredArray[i];
        }

        //System.out.println("\nFiltered Normalized Absolute Audio Samples");
        int samples = (int)((double)inputNodes * (2.0/3.0));
        float normalizedValue;
        for(int i=0; i<samples; i++)//Fill just normalized samples of the inputLine
        {
            if(i<filteredArray.length)
            {
                normalizedValue = filteredArray[i]/FilteredMaximum;
                inputLine.add(normalizedValue);
                //System.out.println(normalizedValue);
            }
            else
                inputLine.add(0.0f);
        }

        //FFT
        float AbsoluteMaximum = -9.0f;
        for(int i=0; i<absArray.length; i++)//look for a maximum
        {
            if(absArray[i] > AbsoluteMaximum)
                AbsoluteMaximum = absArray[i];
        }

        float[] normalizedSamples = new float[realSamples.length];
        for(int i=0; i<recordedAudio.audioRecordLength; i++)
        {
            normalizedSamples[i] = realSamples[i]/AbsoluteMaximum;
        }
        float[] img = new float[realSamples.length];
        fft(realSamples.length, realSamples, img);

        samples = (int)((double)inputNodes * (1.0/3.0));
        float[] resultOfFFT = new float[samples];
        float maxFFT = -999;
        for(int i=0; i<samples; i++)//Calculate FFT magnitude and find maximum
        {
            resultOfFFT[i] = (float)Math.sqrt(realSamples[i] * realSamples[i] + img[i] * img[i]);
            if(resultOfFFT[i] > maxFFT)
                maxFFT = resultOfFFT[i];
        }

        //System.out.println("\nFFT");
        for(int i=0; i<samples; i++)//Normalize FFT magnitude and add to inputLine
        {
            resultOfFFT[i] /= maxFFT;
            inputLine.add(resultOfFFT[i]);
            //System.out.println(resultOfFFT[i]);
        }
    }

    static void makeSineTable(int n, float[] sinTable)
    {
        int i, n2, n4, n8;
        float c, s, dc, ds, t;
        n2 = n / 2;  n4 = n / 4;  n8 = n / 8;
        t = (float)Math.sin(pi / n);
        dc = 2 * t * t;  ds = (float)Math.sqrt(dc * (2 - dc));
        t = 2 * dc;  c = sinTable[n4] = 1;  s = sinTable[0] = 0;
        for (i = 1; i < n8; i++) {
            c -= dc;  dc += t * c;
            s += ds;  ds -= t * s;
            sinTable[i] = s;
            sinTable[n4 - i] = c;
        }
        if (n8 != 0) sinTable[n8] = (float)Math.sqrt(0.5);
        for (i = 0; i < n4; i++)
            sinTable[n2 - i] = sinTable[i];
        for (i = 0; i < n2 + n4; i++)
            sinTable[i + n2] = - sinTable[i];
    }

    static void makeBitReverse(int n, int[] bitrev)
    {
        int i, j, k, n2;
        n2 = n / 2;  i = j = 0;
        while(true)
        {
            bitrev[i] = j;
            if (++i >= n) break;
            k = n2;
            while (k <= j) {  j -= k;  k /= 2;  }
            j += k;
        }
    }

    static void fft(int n, float[] x, float[] y)
    {
        int[]   bitrev;
        float[] sintbl;
        int i, j, k, ik, h, d, k2, n4, inverse;
        float t, s, c, dx, dy;

        if(n < 0)
        {
            n = -n;  inverse = 1;
        } else inverse = 0;
        n4 = n / 4;
        if (n == 0) return;
        sintbl = new float[n + n4];
        bitrev = new int[n];

        makeSineTable(n, sintbl);
        makeBitReverse(n, bitrev);
        for(i = 0; i < n; i++)
        {
            j = bitrev[i];
            if (i < j)
            {
                t = x[i];  x[i] = x[j];  x[j] = t;
                t = y[i];  y[i] = y[j];  y[j] = t;
            }
        }
        for (k = 1; k < n; k = k2) {
            h = 0;  k2 = k + k;  d = n / k2;
            for (j = 0; j < k; j++) {
                c = sintbl[h + n4];
                if (inverse!=0) s = - sintbl[h];
                else         s =   sintbl[h];
                for(i = j; i < n; i += k2)
                {
                    ik = i + k;
                    dx = s * y[ik] + c * x[ik];
                    dy = c * y[ik] - s * x[ik];
                    x[ik] = x[i] - dx;  x[i] += dx;
                    y[ik] = y[i] - dy;  y[i] += dy;
                }
                h += d;
            }
        }

        if (inverse==0)
            for (i = 0; i < n; i++)
            {
                x[i] /= n;
                y[i] /= n;
            }
    }
}
