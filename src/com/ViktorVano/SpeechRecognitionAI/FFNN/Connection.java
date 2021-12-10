package com.ViktorVano.SpeechRecognitionAI.FFNN;

public class Connection {
   public float weight;
   public float deltaWeight;

   public Connection()
   {
      weight = (float)Math.random()-0.5f;//+-0.5f
      deltaWeight = 0.0f;
   }
}
