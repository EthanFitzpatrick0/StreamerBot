\****Work in progress***\*

# StreamerBot

Chatbot designed to mimic a streamer.

Transcribes VODs on twitch and uses those generated text files to create a learning chatbot.

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

It's not in a perfectly functional state as of right now, but it *can* currently download + transcribe Twitch VODs and create sentences from those transcriptions.

1) Run Transcribe.py with Python3 with the streamer's name as an argument, e.g.
        
        py -3 Transcribe.py TrainwrecksTV
        
      This will take a while, especially with streamers that stream more often and for longer. For example, I ran this process for a streamer with 33 broadcasts that were up to 16 hours long each, and it took over 24 hours. There's not a lot of optimizing I can do here since it mostly relies on your download speed.
      
2) Once the transcriptions are complete, run the Driver with the streamer name as an argument, e.g.

        java memory.Driver TrainwrecksTV
        
3) While the driver is running, pressing enter will generate a new sentence. "help" will display the short list of commands. Eventually the way the driver works will be redone. I don't intend for this to be the way the chatbot works. It needs to connect to the Twitch API through Node.js, but I'm still figuring that out.


## TO-DO LIST

* Add twitch chatbot functionality
    * Going to have to learn a bit of JavaScript for this. Shouldn't be too hard, but I'm not sure how to pass the information from java to javascript or if I need to rewrite some code in js.
    * Relevantly respond to chatter @
* Improve documentation

### TO-DO LIST items finished

* Option to limit amount of broadcasts you want to download
* Incorporate weight into word selection algorithm
    * This makes the word/sentence selection algorithm *much* better
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
* v0.1.5 : improve word selection algorithm, remove serialization 
