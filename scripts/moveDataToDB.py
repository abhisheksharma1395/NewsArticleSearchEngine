from pymongo import MongoClient
import requests
import os
import json

def create_mongoDB_connection():
    try:
        client = MongoClient("mongodb://143.198.79.124:27017/newsarticles?readPreference=primary&appname=MongoDB%20Compass&directConnection=true&ssl=false")
        return client
    except Exception as err:
        print("Error occured while creation mongo client: {0}".format(err))
        
def getDBCollection(client):
    try:
        db = client["newsarticles"]
        collection = db["articles"]
        return collection
    except Exception as err:
        print("Error occured while fetching collection: {0}".format(err))

def saveArtilcesToDB(collection, articles):
    try:
        collection.insert_many(articles)
        print ("Successfully Saved Data to DB!")
    except Exception as err:
        print("Error occured while savingg data to collection: {0}".format(err))
        
#get mongodb client
client = create_mongoDB_connection()

#get collection
collection = getDBCollection(client)

count = 0
articles_payload = []

#read all json files from data directory
path = r"C:\Data\Projects\NewsArticleSearchEngine\Crawler\data/"
files = os.listdir(path)
for file in files:
    if file.startswith("block_"):
        print ('Working with file {0}'.format(file))
        with open(path+file) as articles:
            data = json.load(articles)
            for article in data:
                id = article["articleID"]
                body = article["articleBody"]
                body = body.lstrip("AdvertisementSupported by")
                body = body.rstrip("Advertisement")
                article["articleBody"] = body
                articles_payload.append(article)
                if (len(articles_payload) == 100):
                    saveArtilcesToDB(collection, articles_payload)
                    articles_payload = []
print ('Done')

#print(collection.find({}).count())