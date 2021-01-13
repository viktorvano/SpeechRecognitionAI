# SpeechRecognitionAI
 Speech Recognition AI based on FFNN in Java.  
 This Speech Recognition AI converts speech to text and it can communicate with other applications, servers and hardware.  
  
  
## Application Screenshots
  
###### Training Data
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Screenshot%20of%20Training%20Data%20Layout.png?raw=true)  
  
###### Train AI
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Screenshot%20of%20Train%20AI%20Layout.png?raw=true)  
  
###### Speech Recognition
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Screenshot%20of%20Speech%20Recognition%20Layout.png?raw=true)  
  
###### Settings
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Screenshot%20of%20Settings%20Layout.png?raw=true)  
  
  
## How does it work?
  
### Word Examples Feedforwarded via Neural Network
  
##### Words: "hello", "hi", "understand"
  
###### Raw audio sample
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20raw%20samples.png?raw=true)  
<p float="left">
  <img src="/https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20raw%20samples.png" width="100" />
  <img src="/https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hi%20raw%20samples.png" width="100" /> 
  <img src="/https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/understand%20raw%20samples.png" width="100" />
</p>
  
###### Input Layer: Normalized Outer Shell + Normalized FFT
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20outher%20shell.png?raw=true) ![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20FFT.png?raw=true)  
  
###### Hidden Layers
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L1.png?raw=true)  
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L2.png?raw=true)  
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L3.png?raw=true)  
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L4.png?raw=true)  
  
###### Output Layer
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20Output%20Layer.png?raw=true)  
  
  
##### Word: "hi"
  
###### Raw audio sample
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20raw%20samples.png?raw=true)  
  
###### Input Layer: Normalized Outer Shell + Normalized FFT
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20outher%20shell.png?raw=true) ![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20FFT.png?raw=true)  
  
###### Hidden Layers
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L1.png?raw=true)  
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L2.png?raw=true)  
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L3.png?raw=true)  
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L4.png?raw=true)  
  
###### Output Layer
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20Output%20Layer.png?raw=true)  
  
  
##### Word: "understand"
  
###### Raw audio sample
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20raw%20samples.png?raw=true)  
  
###### Input Layer: Normalized Outer Shell + Normalized FFT
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20outher%20shell.png?raw=true) ![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20FFT.png?raw=true)  
  
###### Hidden Layers
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L1.png?raw=true)  
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L2.png?raw=true)  
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L3.png?raw=true)  
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L4.png?raw=true)  
  
###### Output Layer
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20Output%20Layer.png?raw=true)  
  
  

  
## Caption2
### Caption3
#### Caption4
##### Caption5
###### Caption6
