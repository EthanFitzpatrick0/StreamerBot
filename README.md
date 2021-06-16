# StreamerBot

Chatbot designed to mimic a streamer.

**Currently very much so a work in progress.**

The goal is to transcribe VODs on twitch and use those generated text files to create a learning chatbot.

### Parser.java

Parser.java is what separates the .txt file into tokens of Strings and pairs those Strings together in a meaningful way.

### Brain.java

Brain.java is the "Brain" in the sense that it generates the sentences from the Map that Parser.java creates.
This process is done randomly, but a skew is added to make more frequent words selected more often.

### Driver.java

Controls the brain & parser. Takes a streamer name and uses the output of Translate.py to create the Brain object.

### Transcribe.py

Transcribes mp4 videos into text files. Takes a streamer name and iteratively goes through videos found in the streamer's folder, transcribing the videos into text files.
Uses autosub (https://github.com/agermanidis/autosub) to do the transcribing.

## TO-DO LIST

* URGENT: currently parses through text files in respective streamer's folder, but does not delete them
    * This means every time program is run, it deserializes what has already been parsed + tries to parse through and add what is already there to the data set
    * I don't want to just delete the files when I'm done with them, I need to also keep track of the files/dates of the streams that I've already parsed
    * Either that, or I handle this problem when ripping the videos from Twitch (which I haven't gotten to yet). This seems like a better option, but I need a short-term solution for now
    * I wanted to just push something to Git, even if it's not in great condition, because I haven't in a while
* ~~~Pass folder of .txt files instead of single file to Driver~~~
    * Ultimate goal would be to pass streamer name and automatically rip + transcribe VODs
        * Can now pass streamer name, but it only transcribes. Ripping video shouldn't be too hard, will get to that next.
* ~~~Incorporate pyTranscriber into script~~~
    * ~~~Right now I am transcribing with the pyTranscriber app, but I should be able to create a python script using pyTranscriber's functions~~~
    * Was able to create a python script using what pyTranscriber was based off- autosub. I can now automatically transcribe videos.
* Add twitch chatbot functionality
    * Going to have to learn a bit of JavaScript for this. Shouldn't be too hard, but I'm not sure how to pass the information from java to javascript or if I need to rewrite some code in js.
    * Relevantly respond to chatter @
* ~~Save token maps and allow addding additional transcriptions after initial build~~
* Make sentence selection algorithm better
* Improve documentation

## Version History

* v0.1.0 : initial upload
* v0.1.1 : added serialization
* v0.1.2 : automatic transcription, mass parsing 
