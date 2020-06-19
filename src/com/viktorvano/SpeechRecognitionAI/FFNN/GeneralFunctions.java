package com.viktorvano.SpeechRecognitionAI.FFNN;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.Locale;

public class GeneralFunctions {
    public static void showVectorValues(String label, LinkedList<Double> v)
    {
        System.out.println(label + " ");
        for (int i = 0; i < v.size(); i++)
        {
            System.out.println(v.get(i) + " ");
        }
        System.out.println();
    }

    public static String formatDoubleToString12(double number)
    {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        formatSymbols.setDecimalSeparator('.');
        return new DecimalFormat("##########.############", formatSymbols).format(number);
    }

    public static String formatDoubleToString4(double number)
    {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        formatSymbols.setDecimalSeparator('.');
        return new DecimalFormat("##########.####", formatSymbols).format(number);
    }

    public static String colorStyle(double value)
    {
        if(value > 1.0)
            value = 1.0;
        else if(value < -1.0)
            value = -1.0;

        value *= 255.0;
        boolean positive = true;
        if(value >= 0.0)
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
