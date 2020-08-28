package com.ViktorVano.SpeechRecognitionAI.FFNN;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class GeneralFunctions {
    public static void showVectorValues(String label, ArrayList<Float> v)
    {
        System.out.println(label + " ");
        for (int i = 0; i < v.size(); i++)
        {
            System.out.println(v.get(i) + " ");
        }
        System.out.println();
    }

    public static String formatFloatToString12(float number)
    {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        formatSymbols.setDecimalSeparator('.');
        return new DecimalFormat("##########.############", formatSymbols).format(number);
    }

    public static String formatFloatToString4(float number)
    {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        formatSymbols.setDecimalSeparator('.');
        return new DecimalFormat("##########.####", formatSymbols).format(number);
    }

    public static String colorStyle(float value)
    {
        if(value > 1.0f)
            value = 1.0f;
        else if(value < -1.0f)
            value = -1.0f;

        value *= 255.0f;
        boolean positive = true;
        if(value >= 0.0f)
        {
            positive=true;
        }else
        {
            positive = false;
            value = -value;
        }
        String hexString = Integer.toHexString((int)value);
        String colorString = "-fx-background-color: #";
        if (positive)
        {
            if (hexString.length()>1)
                colorString += "00" + hexString + "00";
            else
                colorString += "000" + hexString + "00";
        }else
        {
            if (hexString.length()>1)
                colorString += "0000" + hexString;
            else
                colorString += "00000" + hexString;
        }

        return colorString;
    }
}
