# SpeechRecognitionAI
 Speech Recognition AI based on FFNN in Java.  
 This Speech Recognition AI converts speech to text and it can communicate with other applications, servers and hardware.  
 Tested on Windows and on Linux.  
  
  
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
  
When a user starts to speak, the application starts to record the audio into a buffer.  
When a user stops to speak, the application stops recording the audio and splits the recording into individual words which are then analysed by a neural network.  
A word is processed afterwards and outputs two normalized characteristics: an outer shell amplitude and a frequency spectrum (FFT).  
These are then relayed over the Feed Forward Neural Network.  
If a word has significant match, it is added into the output buffer as a text.  
The neural network analyses the whole speech word by word.  
When all words are analysed then the "word routing" feature steps in and sends the analysed spoken message to individual applications.  
After this the application listens again and the whole process repeats.  
  
###### Notes:
1.) The application can record 22 seconds of speech.  
2.) Words are detected by their amplitude. Alternating amplitude is considered as a word and silence is not a word.  
3.) A single word of a phrase can be 2.97 seconds long.  
4.) Speech is recognized word by word.
5.) If you want to analyse speech word by word, then you need to separate words with a short break.  
    It means that you need to speak like a sloth.  
5.) If you wont to speak more fluently you can, but the neural network has a 2.97 second word (phrase) buffer.  
6.) For a good training data it is recommended to have about 50 training samples of each word you want to teach the neural network.  
7.) It is also recommended to record an audio artifacts (random unwanted noises like chair sounds, typing, clicking...). 
    They should be named as an empty string "". This way the neural network will learn those sounds and will not be mistaken of a spoken word.  
8.) "SpeechRecognitionAI.jar" needs to have "res" folder in the same location. "res" folder must contain "database.dat", "printToConsole.dat", "topology.dat" and "wordRouting.dat".
  
  
### Word Examples Feedforwarded via Neural Network
  
##### Words: "hello", "understand", "hi"
  
###### Raw audio sample
<pre>         (understand)                (artifact)                      (hello)                          (hi)   </pre>  
<img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/understand.png" width="240"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/artifact.png" width="240"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello.png" width="240"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hi.png" width="240"/>  
  
