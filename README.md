# StreamerBot

Chatbot designed to mimic a streamer.

**Currently very much so a work in progress.**

The goal is to transcribe VODs on twitch and use those generated text files to create a learning chatbot.

### Parser

Parser.java is what separates the .txt file into tokens of Strings and pairs those Strings together in a meaningful way.

### Brain

Brain.java is the "Brain" in the sense that it generates the sentences from the Map that Parser.java creates.
This process is done randomly, but a skew is added to make more frequent words selected more often.

### Driver

Does driver things.
