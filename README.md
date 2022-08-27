# NewsArticleSearchEngine

## Description

The project "News Article Search Engine" is designed and developed to understand the basics of Information Retrieval (IR) principles including indexing and searching document collections and performing real-life Web searches. The project consists of an Angular Web App to send post requests to the backend service to retrieve and display the query-based search results. 
There are three main aspects of the projects:
1. Gather the Data: Created a web crawler to fetch news articles from "The New York Times". 
2. Lucene Based Search Engine: Employed Apache Lucene library at the backend to index collected data and facilitate search-based query results. 
3. Hadoop Based search Engine: The search engine is built using the Hadoop MapReduce framework to process the data and build indexes based on it. We have employed the TF-IDF ranking mechanism to rank the pages and provide relevant results.

## Crawler
### Overview of the crawling system
The crawler crawls the website of [The New York Times](https://www.nytimes.com/) for news articles and saves the data in 
a JSON file structure. The crawler receives parameters such as root source, number of articles 
to be crawled, and number of articles to be stored per file. The crawler is designed to handle 
interruption (exceptions), failures, and resume crawler from the previous stage. 

### Architecture
As shown in the above image, the user/actor is responsible to initiate the script/bat file with 
required params as given below.

- The root node or source article: The homepage of The New York Times (or any other 
NYTimes article).
- Number of articles to be stored per file: Which stores the URLs collected from the page
- Number of articles to be crawled: The stopping condition for our crawler.

The crawler component is responsible to perform various operations such as fetching articles, 
parsing, and extracting content and hyperlinks to other articles, checking duplicates and other 
validations such as language, minimum word thresholds, and storing the retrieved/crawled data 
in a json file structure.

### Crawler operations
Fetching Article: Once the crawler is initiated, the crawler will fetch the first article 
provided as source/root from New York Times.

- On fetching the article, the html content must be parsed. For this purpose, we have used 
the beautiful Soup library.
- Then from the parsed data, the article content, title, summary, and other metadata are 
extracted.
- Along with the required content, the hyperlink to other articles is extracted and pushed 
to the queue of unvisited links. Before, pushing the new articles, the links are checked
for the duplicity. We have validated the duplicates by comparing article with article 
title and article URL.
- We have placed another constraint, if somehow, the parsed article content less than a 
certain word threshold, say 150, the article will be dropped and will not be considered.
- As per provided input, once the articles read reaches the limit of articles per file, the 
data will be written on the disk as a JSON data file.
- The crawler is equipped to handle sudden interruption, if the crawler is interrupted in 
between then it can be resumed by starting the crawler again. The crawler saves the 
crawled articles in a file and when crawler is resumed it will get the last crawled article 
and resume from it.

### The Crawling Strategy
The implementation begins with calling the crawl() method which initializes a queue. This queue is for the program to keep track of the URLs we visit and begin back from the last URL to handle failure scenarios where the program crashes or runs into errors resulting in the termination of the process. The program then checks for the start_url, which when “none” triggers to begin the program with the homepage. Or if start_url is not “none” we get the list of the URLs for that 
within the starting URL.

After completing checks for where the crawler is crawling, the program calls the crawl_website() function which uses BeautifulSoup library to parse and extract information like the title, article summary and article body. These attributes along with the article date, article ID and article URL is combined into a JSON object and appended to our collection.

The collected data/articles are stored in files every time we finish visiting 1000 URLs (is configurable) and the upper bound for visited URLs is 150000. This approach helped us gather more than 1Gb of data. We have accounted for the duplicates by comparing article with article title and article URL. The crawler is equipped to handle sudden interruption, if the crawler is interrupted in between then it can be resumed by starting the crawler again. The crawler saves the crawled articles in a file 
and when crawler is resumed it will get the last crawled article and resume from it.


