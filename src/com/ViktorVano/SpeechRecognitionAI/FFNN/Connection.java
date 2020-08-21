package com.ViktorVano.SpeechRecognitionAI.FFNN;

public class Connection {
    /*
        struct Connection
        {
            float weight;
            float deltaWeight;
        };
     */

   public float weight;
   public float deltaWeight;

   public Connection()
   {
      weight = 0.0f;
      deltaWeight = 0.0f;
   }
}
