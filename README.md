# StreamerBot

Chatbot designed to mimic a streamer.

Transcribes VODs on twitch and uses those generated text files to create a learning chatbot.

### Transcribe.py

Rips broadcasts from twitch and transcribes them into text files. Takes a streamer name and downloads all recent broadcasts of a streamer and then transcribes them.
Uses [youtube-dl](https://github.com/ytdl-org/youtube-dl) for the ripping and [autosub](https://github.com/agermanidis/autosub) for the transcribing.

### chatbot.js

Parses transcripts, connects to Twitch, and generates sentences when `!imitate` is sent through twitch chat.

## Dependencies

* Node.js, Python2, and Python3
* [ffmpeg](https://www.ffmpeg.org/)
* [youtube-dl](https://github.com/ytdl-org/youtube-dl)
   * Run `pip install youtube-dl`.
* [autosub](https://github.com/agermanidis/autosub)
   * Run `pip install autosub`.

## Usage

1) Run Transcribe.py with Python3 with the streamer's name as an argument, e.g.
        
        py -3 Transcribe.py TrainwrecksTV
        
      This will take a while, especially with streamers that stream more often and for longer. For example, I ran this process for a streamer with 33 broadcasts that were up to 16 hours long each, and it took over 24 hours. There's not a lot of optimizing I can do here since it mostly relies on your download speed.

2) Set TWITCH_USERNAME and TWITCH_OAUTH_TOKEN in your .env, otherwise the chatbot will fail to connect.

3) Run chatbot.js with the streamer name as an argument, e.g.

        node chatbot TrainwrecksTV
        
4) While the driver is running, sending `!imitate` in the streamer's twitch chat will cause the chatbot to generate a new sentence.


## TO-DO LIST

* Add options for chatbot (e.g. generate sentences on a timer in addition or instead of command)
* Relevantly respond to chatter @

### TO-DO LIST items finished

* Put censored words into separate text file to reduce clutter (and improve scalability)
* Add twitch chatbot functionality
* Improve documentation
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
* v0.2.0 : add chatbot functionality
* v0.2.1 : improve algorithm- bot talks more like a real person now
