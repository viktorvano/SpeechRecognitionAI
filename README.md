# SpeechRecognitionAI
 Speech Recognition AI based on FFNN in Java.  
 This Speech Recognition AI converts speech to text and it can communicate with other applications, servers and hardware.  
 Tested on Windows and on Linux.  
   
 Android App on Google Play Store: [IP Mic](https://play.google.com/store/apps/details?id=eu.cyberpunktech.IpMic)  
   
<img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Android_App_1.jpg" width="150"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Android_App_2.jpg" width="150"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Android_App_3.jpg" width="150"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Android_App_4.jpg" width="150"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Android_App_Icon.jpg" width="150"/>  
   
 Videos:  
 [Speech Recognition AI v20220327 - IP Mic Android App Update](https://www.youtube.com/watch?v=94wzIKT2vC8)  
 [Speech Recognition AI v20220126 - Review](https://www.youtube.com/watch?v=cy86pgSeL5c)  
 [Short Demo - version 20210927 - IoT Moon Lamp](https://www.youtube.com/watch?v=7-QHbnDm6Ds)  
 [Update Review - version 20210927](https://www.youtube.com/watch?v=0sCcgp6zsAU)  
 [Calling an Elevator demo - version 20210403](https://www.youtube.com/watch?v=aTKumJoA4KU)  
 [The First Demo - Controlling RGB LED Strip](https://www.youtube.com/watch?v=USob8uHvUNw)  
 
### TODO List  
- Weights Ironing: Set weight to zero of the first hidden layer of those connections which training has not altered.  
- Extenf FFT to full FFT with Real and Imaginary values (now there is only an FFT magnitude Real x Imag)  
  
  
## Application Screenshots
  
###### Training Data
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Screenshot%20of%20Training%20Data%20Layout.png?raw=true)  
  
###### Train AI
Vocabulary
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Screenshot%20of%20Train%20AI%20Layout%20Vocabulary.png?raw=true)  
  
New Training (learning from scratch)
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Screenshot%20of%20Train%20AI%20Layout%20New%20Training.png?raw=true)  

Transfer Learning (learning continuation)
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Screenshot%20of%20Train%20AI%20Layout%20Transfer%20Learning.png?raw=true)  
  
###### Speech Recognition
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Screenshot%20of%20Speech%20Recognition%20Layout.png?raw=true)  
  
###### Settings
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Screenshot%20of%20Settings%20Layout.png?raw=true)  
  
###### Advanced Settings
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Screenshot%20of%20Advanced%20Settings.png?raw=true)  
  
###### Weights Quicksave Feature
![alt text](https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/Weights%20Quicksave%20Feature.png?raw=true) 
  
  
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
6.) If you want to speak more fluently, you can, but the neural network has a 2.97 second word (phrase) buffer.  
7.) For a good training data it is recommended to have about several training samples of each word you want to teach the neural network.  
8.) It is also recommended to record an audio artifacts (random unwanted noises like chair sounds, typing, clicking...). 
    They should be named as an empty string "". This way the neural network will learn those sounds and will not be mistaken of a spoken word.  
9.) "SpeechRecognitionAI.jar" automatically creates "res" folder in the same location, if it does not exist. In the "res" multiple files will be generated, if they do not exist like: "database.dat", "printToConsole.dat", "topology.dat" and "wordRouting.dat".
  
  
### Neural Network Visualization Screenshot Examples
  
##### Word Examples: "understand", [artifact], "hello", "hi"
  
###### Visualization screenshots
<pre>      (understand)                 (artifact)                  (hello)                      (hi)   </pre>  
<img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/understand.png" width="200"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/artifact.png" width="200"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hello.png" width="200"/> <img src="https://github.com/viktorvano/SpeechRecognitionAI/blob/master/Document%20Files/hi.png" width="200"/>  
  
