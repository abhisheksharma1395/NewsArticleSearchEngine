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

### Instruction on how to run crawler
To run crawler,

Option 1: Execute “run.bat” file present in crawler folder. This file will execute the crawler 
script with default parameters as follows:
- Start_url: https://nytimes.com 
- Articles_per_file: 50
- Articles_to_crawl: 1000

Option 2: Execute python script and pass the params with below command
```bash
python main.py https://www.nytimes.com/ 50 1000
```
Where, start_url = https://www.nytimes.com/
Articles_per_file = 50
Articles_to_crawl = 1000

## Apache Lucene Based Search Engine

### Overview Apache Lucene
Apache Lucene™ is a high-performance, full-featured search engine library written entirely in Java. It is a technology suitable for nearly any application that requires structured search, full-text search, faceting, nearest-neighbor search across high-dimensionality vectors, spell correction or query suggestions.

### Lucene Implementation and Operations
#### Acquire Raw Content
Our Crawler crawls data and stores it in a JSON file. Each JSON file contains 1000 articles. 
We read these files and converted them into a JSON array. We have implemented this using 
the method – parseJSONFile().

#### Build the document
In the next step we build the document from the data which the search application can 
understand and interpret easily. This has been done in method addDocument() where we create 
a Document object to store the article data.

#### Analyse the document
Before starting the indexing process, the document needs to be analysed, to identify the words 
that need to be indexed. In this part we have performed operations like making text lowercase, 
removing stop words, punctuations, tokenization, and filtering. For implementing this we have 
used Lucene Standard analyser since it performs all these operations.

#### Indexing the document
Once documents are built and analysed, the next step is to index them so that these documents 
can be retrieved based on certain keys instead of the entire content of the document. For 
implementing this we have used createIndex() method which takes JSON files (data), 
indexwriter and Document object as input and creates the index. For index creation, we have 
used two fields – articleTitle and articleBody (article content). We used article title for indexing 
because if our query keyword is present in the title then that article is more relevant to the user 
than the one that does not contain it. ArticleBody field is used because that is the field that 
contains actual content of news article.

#### Build Query
To build the query we are using the createQueryParser() and getIndexSearcher() method which 
is used to get an index and search for a query in an index.

#### Process Query
In this part we have used the query object and checked the indexed database to find the news 
articles that match with the input query. We have implemented this in a search() method.

### Instruction on how to build the Lucene index
To run Lucene run below command in LuceneSearch.jar folder

```bash
java -jar LuceneSearch.jar <data path> <path to create index> <path to store result> <query> 
<number of articles to fetch> <create index(true) or use existing index (false)>
```
Example as shown below

>java -jar LuceneSearch.jar /opt/home/cs242-w22/projectdata/data/ /opt/home/cs242-
w22/projectdata/index/ /opt/home/cs242-w22/projectdata/result/ "covid vaccine" 5 true

## Hadoop Based Search Engine
### Overview
We have developed our own search engine that is capable of querying and retrieving New York 
Times news articles from the crawled data. The search engine primarily comprises of efficient 
storing, indexing, and retrieving of relevant documents based on queries. The search engine is 
built using Hadoop MapReduce framework to process the data and build indexes based on it. 
We have employed TF-IDF ranking mechanism to rank the pages and provide relevant results.

### Architecture

The above diagram represents different components and gears that all works together to provide 
a working prototype for the Hadoop Based search engine. The existing search engine works 
only for the news article sourced (crawled) from The New York Times.

#### Building Indices
Building indexes is the crux of any search engine. One cannot scan each document every time 
a user provides a query. The index consists of “word” as key and document along with meta 
data as its values. This approach is also known as inverted indexes. As a part of the meta data,
we have used article id, frequency of the query term in the document and the total number of 
terms in the document. This meta data was used by the score generation engine to generate 
scores based on query and providing relevant results to the user.

To build the engine, we employed MapReduce framework which includes reading the article 
files (JSON), processing them, and generated desirable index as output. The detailed 
explanation of the MapReduce approach will be discussed in the following sections. Once the 
MapReduce function generates the indexes in the text files, the output is read and saved in the
MongoDB database for fast accessing and processing of the data. We have built an index on 
the “word” attributes of the collection so that the data retrieval is improved.

#### MongoDB database to store articles and Indices
The news articles were stored in a JSON block file consisting of around 1000 articles, and 
hence its was getting complicated and inefficient to load and retrieve the required articles based 
on query. Therefore, we have employed MongoDB to store and fast accessing of required 
articles. Moreover, the Article collection table has separate index for fast retrieving of records 
to enhance performance.

Like the above, the index generated by Hadoop MapReduce function was too big to load and 
process. And hence, once the index is generated, we have uploaded the data in the MongoDB 
collection with separate index of the “word” attributes. This enable fast accessing of the query 
and provided better performance.

#### Spring Boot Service for REST API
Another major component of our architecture is the maven spring boot application. This is a 
micro-service that host the query controller that exposes the restful APIs endpoints to cater to
the search query provided by the user. Every time user makes a query, the web interface makes 
an API call to the backend which retrieve results based on scores of the documents. The API 
provided only 10 articles at a time and when user clicks next, then the next 10 results are fetched 
for the same query.

There are two phases to serve the query request, 
- Score Generation: As shown as the backend is hit by the query, the query is tokenized 
to retrieve index values based on the words. As mentioned, the data is retrieved form 
the database. The value contains all the meta-data to generate score of the document. 
After the score for every document is generated, the list (consisting of articles ids) is 
sorted in descending order and top articles ids are send for further processing.
- Retrieving of articles: Once the score is generated and we know which articles to 
retrieve, the second phase consists of making a call to MongoDB server and fetching 
the required articles and forward it to the web interface to show it to the user.

#### Web Interface (Angular application)
The web interface is built upon an angular application. It is a single page application that 
receives the user’s query and the type of search (Lucene/Hadoop) as input and serves the result 
provided by the backend. We only display 10 documents at a time and have included pagination 
support for fast and efficient processing of the search query resulted.

### Hadoop Inverted Index
#### Data Processing
Hadoop was used for cleaning and processing of data. In processing, the following task were 
performed:
1. Removing whitespaces
2. Removing punctuations
3. Removing Stop words
4. Lowercase document
5. Tokenizing words

#### Index Building
Inverted index was built by storing word along with article id (in which word appears), 
frequency of word in that article and length of article. These three parameters were stored 
because we have used TF.IDF for scoring which requires number of times the word appears in 
document and the length of document. The following functions were implemented in Hadoop:
- Mapper
Input to mapper was article<Value> and output was Word<key> along with article id : article 
length <value>.
  
- Combiner
Combiner was used to find frequency of words in a single article. Input to the Combiner was 
word<Key> and articleId : article length <Value>. Output of combiner was word<Key> along 
with articleId, frequency and article length <Value>.
  
- Reducer
Reducer was used to combine all the articles in which word has appeared. Input to reducer was 
word<Key> along with articleId, frequency and article length <Value> and output was 
word<Key> along with a string with a list of all articles <Value> in which word has appeared.

```bash
 <Word> <Article Id>: <Word Count>: <Article Length>
```
  
E.g., of Inverted Index
> pigments 116148:1:1374 116228:1:2641 664:1:1791<br>
revolt 117407:1:1401 

#### Querying Hadoop Index
To generate the index using Hadoop, the query given by the user, is taken as an input. The 
inverted index, that is generated from the MapReduce function and stored in MongoDB is also 
used here. First, the query is converted to lowercase and then split into words. The inverted 
index data is then fetched from MongoDB. We get the article ID, number of occurrences of the 
word and article length as an output from MongoDB for a query. We then use these values to 
calculate the TF.IDF and get a list of articles. We sort these articles in the descending order 
and return the list of articleIDs based on their TF.IDF values.

Example,<br>

>Input => pigments 116148:1:1374 116228:1:2641 664:1:1791 <br>
Total_Documents = 138200 <br>
word = pigments<br>
articleID = 116148<br>
wordcount = 1<br>
articleLength = 1374<br>
docsCount = 3<br>
TF = wordcount / articleLength = 0.00072780203<br>
IDF = Log(Total_Documents / docsCount) = 4.66338678832<br>
TDIDF (for document 1) = TF * IDF = 0.00339402237<br>
Similarly, we can calculate TFIDF for this word in other documents:<br>
TDIDF (for document 2) = TF * IDF = 0.00176576552<br>
TDIDF (for document 3) = TF * IDF = 0.00260378937<br>
After sorting, we get a list of articleIDs in the descending order of their TFIDF values:<br>
[ 116148, 664, 116228 ]
>>>
