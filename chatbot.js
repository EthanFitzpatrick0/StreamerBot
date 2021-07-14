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
const censoredText = path.join(__dirname, 'censored.txt')

dotenv.config()

/* all words that have occurred first in a line, containing their frequency */
var initialFrequency = new Map()


/* each word mapped to a mapping of words appearing directly after it, containing their frequency */
var wordPairs = new Map()


/* map containing profanities and other words altered in the transcription/formatting process to change back */
var censoredMap = new Map()

var cens = fs.readFileSync(censoredText, "utf-8").replaceAll('\r', '').split('\n')
for (const pair of cens) {
  let pairArr = pair.split(' ')
  censoredMap.set(pairArr[1], pairArr[0])
}

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
    if (censoredMap.has(line[i])) {
      line[i] = censoredMap.get(line[i])
      repl = true
    }
    line[i] = line[i].replaceAll(/[.,\/#!$%\^&\;:{}=\-_`~()?!"]|(?<![a-zA-Z])'|'(?![a-zA-Z])/g,"")
    if (line[i] == '' || line[i] == '*') line.splice(i, 1)
  }
  return line
}

/**
 * Pairs words to words occurring after them, keeping track of number of occurrences
 * @param {String []} A line from a transcription file 
 */
function pairWords(line) {
  if (line.length > 2) {
    const firstWord = line[0] + ' ' + line[1] + ' ' + line[2]
    if (initialFrequency.has(firstWord)) {
      initialFrequency.set(firstWord, initialFrequency.get(firstWord) + 1)
    }
    else initialFrequency.set(firstWord, 1)
  }
  
  for (let i = 0; i < line.length - 5; i++) {
    var currentWord = line[i] + ' ' + line[i+1] + ' ' + line[i+2]
    var connectWord = line[i+3] + ' ' + line[i+4] + ' ' + line[i+5]
    if (wordPairs.get(currentWord) == null) { //add new map if word does not have one yet
      var newMap = new Map()
      newMap.set(connectWord, 1)
      wordPairs.set(currentWord, newMap)
    }
    else { //add word to current map or increment if already exists
      if (wordPairs.get(currentWord).has(connectWord))
        wordPairs.get(currentWord).set(connectWord, wordPairs.get(currentWord).get(connectWord) + 1)
      else
        wordPairs.get(currentWord).set(connectWord, 1)
    }
  }
}

/**
 * Creates a random sentence out of the current word mappings
 * @param {String} Seed that is used to find the first phrase. Default = null
 * @returns {String} The randomized generated sentence
 */
 function createSentence(seed=null) {
  var sentence, currentWord
  var sentenceLength, min, max, maxSentenceLength
  sentence = ''
  min = 7
  max = 15
  maxSentenceLength = Math.floor(Math.random() * (max - min) + min)

  if (seed == null) { //select word from list of initial phrases if no seed is provided
    currentWord = selectWord(initialFrequency)
  }
  else { //create map of keys from list of initial phrases that contain the seed
    var substrMap = new Map()
    initialFrequency.forEach( (val, key) => {
      if (key.indexOf(seed) !== -1) {
        substrMap.set(key, val)
      }
    });
    if (substrMap.size == 0) {
      return null
    }
    currentWord = selectWord(substrMap)
  }
  sentence = currentWord
  sentenceLength = 1

  var prevWord = currentWord
  while(sentenceLength < maxSentenceLength && wordPairs.get(currentWord) != null) {
    currentWord = selectWord(wordPairs.get(currentWord))
    if (currentWord == prevWord) { //we want different words/phrases in a row
      if (wordPairs.get(currentWord).size > 1) {
        currentWord = selectDiffWord(wordPairs.get(currentWord), prevWord)
      }
      else {
        currentWord = ''
        break
      }
    }
    prevWord = currentWord
    sentence += " " + currentWord
    sentenceLength++
  }
  
  sentence = sentence.substr(0, 1).toUpperCase() + sentence.substring(1) + '.' //add capitalization to first word and period at end
  return sentence
}

/**
 * Selects next word given the current word's connection map, favoring words that appear more often
 * @param {Map} Map of connections to word that we are currently on
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

  let frequency = getRandomInt(bound)

  //0 = top 50%, 1: top 75%, 2: top 25%, 3: top 20%, 4: top 10%, 5: top 5%
  switch (frequency) {
    case 0: //top 50%
      weightedIndex = getRandomInt(weightedSize/2)
      break
    case 1: //top 75%
      weightedIndex = getRandomInt(weightedSize*(3.0/4.0))
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
    default: //full weightedSize
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
 * Get first phrase from given map that does not match the phrase that came before in the sentence
 * @param {Map} Map of connections to word that we are currently on
 * @param {String} The previous word that we are avoiding
 * @returns {String} The next word selected, differing from previous
 */
function selectDiffWord(map, prevWord) {
  const currentList = Array.from(map.keys())
  for (const word of currentList) {
    if (word != prevWord) return word
  }
}

/**
 * Returns a random int in the range 0 to max
 * @param {Number} Maximum value (exclusive)
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
        username: process.env.TWITCH_USERNAME,
        password: process.env.TWITCH_OAUTH_TOKEN
    },
    channels: [ channel ]
  });

  client.connect()

  client.on('message', (channel, tags, message, self) => {
      if(self) return;
      
      if(message.toLowerCase() == '!imitate') { //generate normal sentence
          client.say(channel, createSentence()); 
      }
      else {
        const strArr = message.split(' ')
        if (strArr.length > 1 && strArr[0] == '!imitate') { //generate sentence from seed
          let sentence = createSentence(strArr.slice(1).join(' ').toLowerCase()) //grab chatter's message after "!imitate" and use it as a seed
          if (sentence != null) client.say(channel, sentence)
          else client.say(channel, `@${tags.username}, what are you talking about?`) //message if no sentence could be generated
        }
      }
  });
}