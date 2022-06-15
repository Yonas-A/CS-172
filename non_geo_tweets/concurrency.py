from glob import glob
import json
import os
import threading
import time
from urllib import robotparser
from urllib.parse import urlparse

import requests
import tweepy
from bs4 import BeautifulSoup
from dotenv import load_dotenv

load_dotenv()
bearer_token = os.getenv("BEARER_TOKEN")

count = 200

def visitUrl(fileName):
  visited = {}
  rp = robotparser.RobotFileParser()

  # create new file for visited url info
  newName = fileName[:-5] + "_visited.json"
  saveFile = open(newName, "a")
  saveFile.write("[\n")
  
  with open(fileName) as oldFile:
    source = json.load(oldFile)

    for json_data in source:
      try:
        # json_data = json.load(line)

        j = {}
        j["id"] = json_data["data"]["id"]
        j["users"] = json_data["includes"]["users"][0]["name"]
        j["created_at"] = json_data["data"]["created_at"]
        j["text"] = json_data["data"]["text"]
        j["lang"] = json_data["data"]["lang"]
        # j["location"] = json_data["place"]["bounding_box"]["coordinates"]
        
        if(json_data["data"]["entities"]):
          print(" ")
        else: 
          print("No entities")
        
        try:
          if(json_data["data"]["entities"]["urls"]):
            j["urls"] = json_data["data"]["entities"]["urls"]
          else:
            j["urls"] = None
        except BaseException as e:
            j["urls"] = None
            j["title"] = None

        # if links exist
        if j["urls"]:
          urls = j["urls"]
          # for each link
          for url in urls:
            # get url value
            url = url["expanded_url"]

            # check robots
            parsed = urlparse(url)
            robotsUrl = (
              parsed.scheme + "://" + parsed.netloc + "/robots.txt"
            )
            rp.set_url(robotsUrl)
            rp.read()

            if rp.can_fetch("*", url):
              # get info from url
              r = requests.get(url)
              soup = BeautifulSoup(r.content, "html.parser")
              if soup.title.string:
                j["title"] = soup.title.string
              else:
                j["title"] = None

        else:  # link doesn't exist
          j["title"] = None
        # write line to file
        newLine = json.dumps(j)
        saveFile.write(newLine)
        
        if(json_data != source[-1]):
          saveFile.write(",\n")
      except BaseException as e:
        print("failed on visit", str(e))
        pass

  saveFile.write("\n]")
  saveFile.close()
  oldFile.close()
  return


class TweetSaver(tweepy.StreamingClient):
  outputDir = './archive'
  fileSize = 10485760 # in bytes 10485760 = 10MB 1048576 = 1MB

  def on_data(self, raw_data):
    try: 
      global count
      to_json = json.loads(raw_data)
      data = json.dumps(to_json)
      
      fileName = os.path.join( self.outputDir, "tweets_" + str(count) + ".json" )
      if( not os.path.isfile(fileName)):
        saveFile = open(fileName, "a")    
        saveFile.write("[\n")
        saveFile.close()
      
      saveFile = open(fileName, "a")
      saveFile.write(data)
      
      if os.path.getsize(fileName) > self.fileSize:
        saveFile.write("\n]")
        saveFile.close()
        count += 1
        # New thread to visit urls of tweets
        t = threading.Thread(target=visitUrl, args=[fileName])
        t.start()
      else :
        saveFile.write(",\n")
        saveFile.close()
      return True
     
    except BaseException as e:
      print("failed on data,", str(e))
      time.sleep(5)
      pass
  def on_connection_error(self):
    print("Disconnected")
    self.disconnect()

writer = TweetSaver(bearer_token, return_type=dict)


writer.sample(
  tweet_fields=["lang", "created_at", "id", "entities"],
  user_fields=["username"],
  expansions=["geo.place_id", "author_id"],
  place_fields=['full_name', 'name', 'country', 'geo', 'place_type'],
)
writer.sample()
