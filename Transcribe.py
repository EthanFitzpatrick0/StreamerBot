#!/usr/bin/env python3
import os
import sys
import subprocess
from pathlib import Path

if __name__ == '__main__':
    
    #name of streamer, passed as an argument
    streamer = sys.argv[1] 
    
    #directory where streamer's twitch VODs are located
    inputDir = 'Twitch_VODs\\' + streamer 
    
    #directory where transcribed files will be put
    outputDir = 'Autosub_Output\\' + streamer 
    if not os.path.exists(outputDir):
        os.system('mkdir .\Autosub_Output\\' + streamer)

    #list of files in the autosub output directory
    outputFiles = os.listdir(outputDir)

    if not os.path.isdir(inputDir) or not os.listdir(inputDir):
        print('Input error:\n\tInput directory is empty or nonexistant. Please populate the input directory .\Twitch_VODs\\' + streamer + ' with the corresponding Twitch VODs or other .mp4s')
        exit(1)
    for inputFile in os.listdir(inputDir):
        #check to make sure we haven't already transcribed this file
        if inputFile[:-4]+'.txt' not in outputFiles:
            outputPath = 'Autosub_Output\\' + streamer + '\\' + inputFile[:-4] + '.txt'
            inputPath = 'Twitch_VODs\\' + streamer + '\\' + inputFile
            subprocess.run(['py', '-2', 'autosub_app.py', '-o', outputPath, '-S', 'en', '-D', 'en', inputPath])
            
            with open(outputPath, 'r') as reader:
                lines = reader.readlines()

            #delete everything but the text (which happens to be every 4th line after the first 3 lines)
            with open(outputPath, 'w') as writer:
                lastWrite = 0
                for i, line in enumerate(lines):
                    if i - lastWrite == 4:
                        writer.write(line)
                        lastWrite = i
                        continue
                    if i == 2:
                        writer.write(line)
                        lastWrite = 2
                    
                    
