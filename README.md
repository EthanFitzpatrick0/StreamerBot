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

Transcribes videos into text files. Takes a streamer name and iteratively goes through videos found in the streamer's folder, transcribing the videos into text files.
Uses autosub (https://github.com/agermanidis/autosub) to do the transcribing.

## Dependencies

* Java, Python2, and Python3
* [autosub](https://github.com/agermanidis/autosub)
   * Install [ffmpeg](https://www.ffmpeg.org/)
   * Run `pip install autosub`.

## Usage

Honestly, I wasn't planning on having anyone else use it at this point. I'm mostly trying to keep up my programming skills while looking for a job. But I noticed that I've had git clones in the last few days, so I might as well make some sort of a guide. After all, why make something if no one else can use it?

1) Put videos that need to be transcribed under StreamerBot\Twitch_VODs\streamer where "streamer" is the name of the streamer whose videos you intend to transcribe.
2) Run Transcribe.py with Python3 with the streamer's name as an argument, e.g.
        
        py -3 Transcribe.py TrainwrecksTV
      
3) Wait for the transcription process to complete (this should take a while, especially for long broadcasts). To test, you could create a small mp4 with some audio. The output will eventually be found under StreamerBot\Autosub_Output\streamer
4) Once the transcriptions are complete, run Driver.class with the streamer name as an argument, e.g.

        java backend.Driver TrainwrecksTV
        
   It will take a bit to create the mappings, especially if multiple broadcasts are being processed. When you exit, it will serialize the Brain file, allowing it to run much faster the next time.
5) While the driver is running, pressing enter will generate a new sentence. "help" will display the short list of commands. Eventually the way the driver works will be redone. I don't intend for this to be the way the chatbot works. It needs to connect to the Twitch API through Node.js, but I'm still figuring that out.


## TO-DO LIST

* Automate ripping video from Twitch
* Add twitch chatbot functionality
    * Going to have to learn a bit of JavaScript for this. Shouldn't be too hard, but I'm not sure how to pass the information from java to javascript or if I need to rewrite some code in js.
    * Relevantly respond to chatter @
* Make sentence selection algorithm better
* Improve documentation

### TO-DO LIST items finished

* Pass folder of .txt files instead of single file to Driver
* Incorporate autosub into script
* Save token maps and allow addding additional transcriptions after initial build

## Version History

* v0.1.0 : initial upload
* v0.1.1 : added serialization
* v0.1.2 : automatic transcription, mass parsing 
* v0.1.3 : fixed frequency sorting, improved performance of parsing drastically
