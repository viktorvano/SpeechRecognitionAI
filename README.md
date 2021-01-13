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
  
##### Words: "hello", "understand", "hi"
  
###### Raw audio sample
<pre>                 (hello)                               (understand)                                  (hi)   </pre>  
<img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20raw%20samples.png" width="330"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/understand%20raw%20samples.png" width="330"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hi%20raw%20samples.png" width="330"/>  
  
###### Input Layer: Normalized Outer Shell + Normalized FFT
<pre>(hello)      Normalized Outer Shell                                            Normalized FFT</pre>  
<img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20outher%20shell.png" width="450"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20FFT.png" width="450"/>  
  
<pre>(hi)         Normalized Outer Shell                                            Normalized FFT</pre>  
<img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hi%20outher%20shell.png" width="450"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hi%20FFT.png" width="450"/>  
  
<pre>(understand) Normalized Outer Shell                                            Normalized FFT</pre>  
<img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/understand%20outher%20shell.png" width="450"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/understand%20FFT.png" width="450"/>  
  
  
###### Hidden Layers: "hello", "understand", "hi"
<pre>                 (hello) L1                            (understand) L1                               (hi) L1</pre>  
<img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L1.png" width="330"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/understand%20L1.png" width="330"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hi%20L1.png" width="330"/>  
  
<pre>                 (hello) L2                            (understand) L2                               (hi) L2</pre>  
<img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L2.png" width="330"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/understand%20L2.png" width="330"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hi%20L2.png" width="330"/>  
  
<pre>                 (hello) L3                            (understand) L3                               (hi) L3</pre>  
<img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L3.png" width="330"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/understand%20L3.png" width="330"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hi%20L3.png" width="330"/>  
  
<pre>                 (hello) L4                            (understand) L4                               (hi) L4</pre>  
<img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20L4.png" width="330"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/understand%20L4.png" width="330"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hi%20L4.png" width="330"/>  
  
###### Output Layer: "hello", "understand", "hi"
<pre>                 (hello)                               (understand)                                  (hi)   </pre>  
<img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello%20Output%20Layer.png" width="330"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/understand%20Output%20Layer.png" width="330"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hi%20Output%20Layer.png" width="330"/>  
  
  

  
## Caption2
### Caption3
#### Caption4
##### Caption5
###### Caption6
