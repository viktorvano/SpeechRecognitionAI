package com.ViktorVano.SpeechRecognitionAI.FFNN;

public class Connection {
    /*
        struct Connection
        {
            float weight;
            float deltaWeight;
        };
     */

   public double weight;
   public double deltaWeight;

   public Connection()
   {
      weight = 0.0;
      deltaWeight = 0.0;
   }
}
