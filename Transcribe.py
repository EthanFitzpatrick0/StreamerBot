#!/usr/bin/env python3
import os
import sys
import subprocess
from pathlib import Path
import youtube_dl
import youtube_dl.utils
from datetime import datetime

from youtube_dl.YoutubeDL import YoutubeDL

if __name__ == '__main__':
    
    #name of streamer, passed as an argument
    streamer = sys.argv[1] 
    
    #directory where streamer's twitch VODs are located
    vodDir = 'VODs\\' + streamer

    #file where record of downloaded files is kept
    dlDir = 'Records\\' + streamer
    dlFile = dlDir + '\\' + streamer + '_dl.txt'

    if not os.path.isdir(vodDir):
        os.system('mkdir .\VODs\\' + streamer)
    
    if not os.path.isdir(dlDir):
        os.system('mkdir .\Records\\' + streamer)

    #options for youtube-dl
    ydl_opts = {
        'outtmpl': vodDir + '\%(uploader)s_%(upload_date)s.%(ext)s',
        'download_archive': dlFile,
        'format': 'bestaudio/best',
        'postprocessors': [{
            'key': 'FFmpegExtractAudio',
            'preferredcodec': 'mp3',
            'preferredquality': '192',
        }]
    }
    
    with youtube_dl.YoutubeDL(ydl_opts) as ydl:
        ydl.download(['https://www.twitch.tv/' + streamer + '/videos?filter=archives&sort=time'])

    #directory where transcribed files will be put
    subDir = 'Transcriptions\\' + streamer 
    if not os.path.exists(subDir):
        os.system('mkdir .\Transcriptions\\' + streamer)

    #list of files in the autosub output directory
    subFiles = os.listdir(subDir)

    if not os.listdir(vodDir):
        print('Input error:\n\tInput directory is empty or nonexistant. Please populate the input directory .\VODs\\' + streamer + ' with the corresponding Twitch VODs or other videos or audio files')
        exit(1)
    
    #transcribe files
    for file in os.listdir(vodDir):
        #check to make sure we haven't already transcribed this file
        if file[:-4]+'.txt' not in subFiles:
            subFile = 'Transcriptions\\' + streamer + '\\' + file[:-4] + '.txt'
            vodFile = 'VODs\\' + streamer + '\\' + file
            subprocess.run(['py', '-2', 'autosub_app.py', '-o', subFile, '-S', 'en', '-D', 'en', vodFile])
            
            with open(subFile, 'r') as reader:
                lines = reader.readlines()

            #delete everything but the text (which happens to be every 4th line after the first 3 lines)
            with open(subFile, 'w') as writer:
                lastWrite = 0
                for i, line in enumerate(lines):
                    if i - lastWrite == 4:
                        writer.write(line)
                        lastWrite = i
                        continue
                    if i == 2:
                        writer.write(line)
                        lastWrite = 2
            
                    
                    
