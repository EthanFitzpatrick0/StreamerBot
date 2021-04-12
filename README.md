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

Does driver things.

## TO-DO LIST

* Pass folder of .txt files instead of single file to Driver
    * Ultimate goal would be to pass streamer name and automatically rip + transcribe VODs
* Incorporate pyTranscriber into script
    * Right now I am transcribing with the pyTranscriber app, but I should be able to create a python script using pyTranscriber's functions
* Add twitch chatbot functionality
    * Relevantly respond to chatter @
* ~~Save token maps and allow addding additional transcriptions after initial build~~
* Make sentence selection algorithm better
* Improve documentation

## Version History

* v0.1.0 : initial upload
* v0.1.1 : added serialization
