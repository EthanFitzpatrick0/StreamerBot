#!/usr/bin/env python3
import os
import sys
import subprocess
from pathlib import Path
import youtube_dl
import youtube_dl.utils
import argparse

from youtube_dl.YoutubeDL import YoutubeDL

if __name__ == '__main__':
    
    parser = argparse.ArgumentParser()
    parser.add_argument('streamer', help="Desired streamer to rip and transcribe videos from.")
    parser.add_argument('-max', '--max-download', help="Number of max videos to download (default = no max).", type=int, default=None)
    parser.add_argument('-sim', '--simulate', help="Simulate the downloading process.", action="store_true")
    parser.add_argument('-skip', '--skip-download', help="Skip over the download process.", action="store_true")
    parser.add_argument('-skip2', '--skip-transcription', help="Skip over the transcription process", action="store_true")
    
    args = parser.parse_args()

    streamer = args.streamer
    
    #directory to store streamer data
    streamerDir = 'Streamers\\' + streamer
    
    #directory where streamer's twitch VODs are located
    vodDir = streamerDir + '\VODs'

    #file where record of downloaded files is kept
    dlFile = streamerDir + '\\' + streamer + '_dl.txt'

    if not os.path.isdir(streamerDir):
        os.system('mkdir ' + streamerDir)

    if not args.skip_download:

        if not os.path.isdir(vodDir):
            os.system('mkdir ' + vodDir)

        #options for youtube-dl
        ydl_opts = {
            'outtmpl': vodDir + '\%(uploader)s_%(upload_date)s.%(ext)s',
            'max_downloads': args.max_download,
            'simulate': args.simulate,
            'download_archive': dlFile,
            'format': 'bestaudio/best',
            'postprocessors': [{
                'key': 'FFmpegExtractAudio',
                'preferredcodec': 'mp3',
                'preferredquality': '192',
            }]
        }
        try:
            with youtube_dl.YoutubeDL(ydl_opts) as ydl:
                ydl.download(['https://www.twitch.tv/' + streamer + '/videos?filter=archives&sort=time'])
        except youtube_dl.utils.MaxDownloadsReached:
            pass


    if not args.skip_transcription:
        #directory where transcribed files will be put
        subDir = streamerDir + '\Transcriptions\\'
        if not os.path.exists(subDir):
            os.system('mkdir ' + streamerDir + '\Transcriptions')

        #list of files in the autosub output directory
        subFiles = os.listdir(subDir)

        if not os.path.exists(vodDir) or not os.listdir(vodDir):
            print('Input error:\n\tInput directory is empty or nonexistant. Please populate the input directory .\VODs\\' + streamer + ' with the corresponding Twitch VODs, other videos, or audio files')
            exit(1)
        
        #transcribe files
        for file in os.listdir(vodDir):
            #check to make sure we haven't already transcribed this file
            if file[:-4]+'.txt' not in subFiles:
                subFile = subDir + '\\' + file[:-4] + '.txt'
                vodFile = vodDir + '\\' + file
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
                #delete VOD file when finished
                os.remove(vodDir + '\\' + file)
            
                    
                    
