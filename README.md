# StreamerBot

Chatbot designed to mimic a streamer.

**Currently a work in progress.**

The goal is to transcribe VODs on twitch and use those generated text files to create a learning chatbot.

### Parser.java

Parser.java is what separates the .txt file into tokens of Strings and pairs those Strings together in a meaningful way.

### Brain.java

Brain.java is the "Brain" in the sense that it generates the sentences from the Map that Parser.java creates.
This process is done randomly, but a skew is added to make more frequent words selected more often.

### Driver.java

Controls the brain & parser. Takes a streamer name and uses the output of Translate.py to create the Brain object.

### Transcribe.py

Rips broadcasts from twitch and transcribes them into text files. Takes a streamer name and downloads all recent broadcasts of a streamer and then transcribes them.
Uses [youtube-dl](https://github.com/ytdl-org/youtube-dl) for the ripping and [autosub](https://github.com/agermanidis/autosub) for the transcribing.

## Dependencies

* Java, Python2, and Python3
* [ffmpeg](https://www.ffmpeg.org/)
* [youtube-dl](https://github.com/ytdl-org/youtube-dl)
   * Run `pip install youtube-dl`.
* [autosub](https://github.com/agermanidis/autosub)
   * Run `pip install autosub`.

## Usage

Honestly, I wasn't planning on having anyone else use it at this point. I'm mostly trying to keep up my programming skills while looking for a job. But I noticed that I've had git clones in the last few days, so I might as well make some sort of a guide. After all, why make something if no one else can use it?

1) Put videos that need to be transcribed under StreamerBot\VODs\streamer where "streamer" is the name of the streamer whose videos you intend to transcribe.
2) Run Transcribe.py with Python3 with the streamer's name as an argument, e.g.
        
        py -3 Transcribe.py TrainwrecksTV
      
3) Wait for the transcription process to complete (this should take a while, especially for long broadcasts). To test, you could create a small mp4 with some audio. The output will eventually be found under StreamerBot\Transcriptions\streamer
4) Once the transcriptions are complete, run Driver.class with the streamer name as an argument, e.g.

        java memory.Driver TrainwrecksTV
        
   It will take a bit to create the mappings, especially if multiple broadcasts are being processed. When you exit, it will serialize the Brain file, allowing it to run much faster the next time.
5) While the driver is running, pressing enter will generate a new sentence. "help" will display the short list of commands. Eventually the way the driver works will be redone. I don't intend for this to be the way the chatbot works. It needs to connect to the Twitch API through Node.js, but I'm still figuring that out.


## TO-DO LIST

* Option to limit amount of broadcasts you want to download
* Delete broadcasts when done with transcription (pretty easy but it's late and I've been working for a while)
* Right now, I'm picking words based on their order of frequency, but this doesn't completely account for frequency; i.e- a map could be `["I":2445, "we":4]` . Even though "I
" has appeared MUCH more frequently than "we", right now the algorithm will put it to a 50% chance of picking either. I'm going to look into assigning words individual weight.  
* Add twitch chatbot functionality
    * Going to have to learn a bit of JavaScript for this. Shouldn't be too hard, but I'm not sure how to pass the information from java to javascript or if I need to rewrite some code in js.
    * Relevantly respond to chatter @
* Make sentence selection algorithm better
* Improve documentation

### TO-DO LIST items finished

* Automate ripping video from Twitch
* Pass folder of .txt files instead of single file to Driver
* Incorporate autosub into script
* Save token maps and allow addding additional transcriptions after initial build

## Version History

* v0.1.0 : initial upload
* v0.1.1 : added serialization
* v0.1.2 : automatic transcription, mass parsing 
* v0.1.3 : fixed frequency sorting, improved performance of parsing drastically
* v0.1.4 : automatic ripping, change directory/package names to be more intuitive 
