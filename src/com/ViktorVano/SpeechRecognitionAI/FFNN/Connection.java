package com.ViktorVano.SpeechRecognitionAI.FFNN;

public class Connection {
   public float weight;
   public float deltaWeight;

   public Connection()
   {
      weight = 2f*(float)Math.random()-1f;//+-1.0f
      deltaWeight = 0.0f;
   }
}
