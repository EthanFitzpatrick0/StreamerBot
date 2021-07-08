const dotenv = require('dotenv')
const tmi = require('tmi.js')
const path = require('path')
const fs = require('fs')
const readline = require('readline')
const { Console, memory } = require('console')
const { listenerCount } = require('events')
const { exit } = require('process')

const streamer = process.argv.slice(2)[0]
const transcriptionDir = path.join(__dirname, 'Streamers', streamer, 'Transcriptions')
dotenv.config()

/* all words that have occurred first in a line, containing their frequency */
var initialFrequency = new Map()


/* each word mapped to a mapping of words appearing directly after it, containing their frequency */
var wordPairs = new Map()


/* map containing profanities and other words altered in the transcription/formatting process to change back */
var censoredMap = new Map()
censoredMap.set("a**", "ass")
censoredMap.set("a****", "asses")
censoredMap.set("a******", "asshole")
censoredMap.set("a*******", "assholes")
censoredMap.set("b****", "bitch")
censoredMap.set("b*****", "bitchy")
censoredMap.set("b******", "bitches")
censoredMap.set("b*******", "bitching")
censoredMap.set("c***", "cunt")
censoredMap.set("c****", "cunts")
censoredMap.set("f***", "fuck")
censoredMap.set("f****", "fucks")
censoredMap.set("f*****", "fucker")
censoredMap.set("f******", "fucking")
censoredMap.set("i'm", "I'm")
censoredMap.set("i", "I")
censoredMap.set("m***********", "motherfucker")
censoredMap.set("m************", "motherfucking")
censoredMap.set("p****", "pussy")
censoredMap.set("p******", "pussies")
censoredMap.set("s***", "shit")
censoredMap.set("s****", "shits")
censoredMap.set("s*****", "shitty")
censoredMap.set("s******", "shitter")
censoredMap.set("s*******", "shitting")


files = fs.readdirSync(transcriptionDir)
files.forEach( (file) => {
    readTranscription(path.join(transcriptionDir, file))
});

wordPairs.forEach( (m, word) => {
  var mSort = new Map([...m.entries()].sort((a, b) => b[1] - a[1]));
  wordPairs.set(word, mSort)
});

initialFrequency = new Map([...initialFrequency.entries()].sort((a, b) => b[1] - a[1]));

connectTwitch()

/**
 * Reads a transcription file and parses it
 * @param {String} The transcription file name 
 */
function readTranscription(file) {
  const fileStream = fs.createReadStream(file);

  var lines = require('fs').readFileSync(file, 'utf-8')
    .split('\n')
    .filter(Boolean);

  for (const line of lines) {
    var lineArr
    lineArr = formatWords(line.split(' '))
    if (lineArr != undefined) {
      pairWords(lineArr)
    }
  }
}

/**
 * Uncensors and removes punctuation from every string in line array
 * @param {String []} Array containing the strings in the last read line
 * @returns {String []} Formatted line array
 */
function formatWords(line) {
  for (let i = 0; i < line.length; i++) {
    line[i] = line[i].toLowerCase()
    line[i] = line[i].replace('\r', '')
    if (censoredMap.has(line[i])) line[i] = censoredMap.get(line[i])
    line[i] = line[i].replaceAll(/[.,\/#!$%\^&\*;:{}=\-_`~()?!"]|(?<![a-zA-Z])'|'(?![a-zA-Z])/g,"")
  }
  return line
}

/**
 * Pairs words to words occurring after them, keeping track of number of occurrences
 * @param {String []} A line from a transcription file 
 */
function pairWords(line) {

  const firstWord = line[0]
  if (initialFrequency.has(firstWord)) {
    initialFrequency.set(firstWord, initialFrequency.get(firstWord) + 1)
  }
  else initialFrequency.set(firstWord, 1)
  
  for (let i = 0; i < line.length - 1; i++) {
    var currentWord = line[i]
    if (wordPairs.get(currentWord) == null) { //add new map if word does not have one yet
      var newMap = new Map()
      newMap.set(line[i+1], 1)
      wordPairs.set(currentWord, newMap)
    }
    else { //add word to current map or increment if already exists
      if (wordPairs.get(currentWord).has(line[i+1]))
        wordPairs.get(currentWord).set(line[i+1], wordPairs.get(currentWord).get(line[i+1]) + 1)
      else
        wordPairs.get(currentWord).set(line[i+1], 1)
    }
  }
}

/**
 * Creates a random sentence out of the current word mappings
 * @returns {String} The randomized generated sentence
 */
function createSentence() {
  var sentence, currentWord
  var sentenceLength, min, max, maxSentenceLength
  sentence = ''
  min = 10
  max = 20
  maxSentenceLength = Math.floor(Math.random() * (max - min) + min)

  currentWord = selectWord(initialFrequency)
  sentence = currentWord
  sentenceLength = 1

  while(sentenceLength < maxSentenceLength && wordPairs.get(currentWord) != null) {

    currentWord = selectWord(wordPairs.get(currentWord))
    sentence += " " + currentWord
    sentenceLength++
  }
  
  sentence = sentence.substr(0, 1).toUpperCase() + sentence.substring(1) + '.'
  return sentence
}

/**
 * Selects next word given the current word, favoring words that appear more often
 * @param {String} Word that we are currently on
 * @returns {String} The next word selected
 */
function selectWord(map) {
  const currentList = Array.from(map.keys())
  let weightedSize = 0
  map.forEach( (val) => {
    weightedSize += val
  });
  var bound
  var weightedIndex = 0

  if (weightedSize < 4) bound = 1
  else if (weightedSize < 5) bound = 3
  else if (weightedSize < 10) bound = 4
  else if (weightedSize < 20) bound = 5
  else bound = 6

  var frequency = getRandomInt(bound)

  //0 = full size, 1: bottom 75%, 2: top 25%, 3: top 20%, 4: top 10%, 5: top 5%
  switch (frequency) {
    case 0: //full weightedSize
      weightedIndex = getRandomInt(weightedSize)
      break
    case 1: //bottom 75%
      weightedIndex = Math.floor(Math.random() * (weightedSize - (weightedSize/4)) + (weightedSize/4))
      break
    case 2: //top 25%
      weightedIndex = getRandomInt(weightedSize/4)
      break
    case 3: //top 20%
      weightedIndex = getRandomInt(weightedSize/5)
      break
    case 4: //top 10%
      weightedIndex = getRandomInt(weightedSize/10)
      break
    case 5: //top 5%
      weightedIndex = getRandomInt(weightedSize/20)
      break
    default: //full weightedSize (should never reach)
      weightedIndex = getRandomInt(weightedSize)
  }

  var currentWeight = weightedIndex
  for (const word of currentList) {
    currentWeight -= map.get(word)
    if (currentWeight <= 0) return word
  }

  return null
}

/**
 * Returns a random int in the range 0 to max
 * @param {int} Maximum value (exclusive)
 * @returns {int} Random int in the given range
 */
function getRandomInt(max) {
  return Math.floor(Math.random() * max)
}

/**
 * Connects to streamer's twitch chat and periodically generates and posts a sentence
 */
function connectTwitch() {
  const client = new tmi.Client({
    connection: {
      secure: true,
      reconnect: true
    },
    identity: {
        username: 'imitator_bot',
        password: process.env.TWITCH_OAUTH_TOKEN
    },
    channels: [ 'OmegaJr0' ] //TODO: change to streamer when done testing
  });

  client.connect()

  client.on('message', (channel, tags, message, self) => {
      if(self) return;
      
      if(message.toLowerCase() == '!imitate') {
          client.say(channel, createSentence()); 
      }
  });
}