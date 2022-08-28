from bs4 import BeautifulSoup
from matplotlib.pyplot import get

try:
    import urllib.request as urllib2
except ImportError:
    import urllib2
import re
from collections import deque
import json
import os.path


class Crawler:
    def __init__(self, threshold_words=150):
        self.__url_queue = deque()
        self.__url_map = {}
        self.__threshold_words = threshold_words
        self.__json_data = []

    
    def __get_visited_urls(self, url_per_file):
        file_exists = os.path.exists('data/article_map.json')
        if file_exists:
            with open('data/article_map.json', 'r') as article_map:
                self.__url_map = json.load(article_map)
                self.__global_index = len(self.__url_map)
                print(self.__global_index)
                last_url = list(self.__url_map.keys())[-1]
                print('last visited url {0}'.format(last_url))
                self.__block_number = int(self.__global_index/url_per_file)+1
                return last_url
        else:
            self.__url_map = {}
            self.__global_index = 0
            self.__block_number = 0
            return None

    def __write_json(self):
        filename = "data/block_" + str(self.__block_number) + ".json"
        self.__block_number += 1
        with open(filename, "w") as outfile:
            json.dump(self.__json_data, outfile)
            outfile.close()
            self.__json_data = []

    def __write_urls(self):
        filename = "data/article_map.json"
        with open(filename, "w") as outfile:
            json.dump(self.__url_map, outfile)
            outfile.close()

    def __remove_excess(self, url_list):
        final_list = []

        for url in url_list:
            index = url.find(".html")
            parsed_url = url[:index + 5]
            if index == -1:
                continue
            else:
                final_list.append(parsed_url)

        # Empty "url_list"
        del url_list[:]
        url_list = []

        for url in final_list:
            if url.startswith("http://www.nytimes.com") or url.startswith("https://www.nytimes.com"):
                url_list.append(url)
        return url_list

    def __extract_href(self, data_received):
        href_links = []
        soup = BeautifulSoup(data_received, "html.parser")
        for itHref in soup.findAll('a', href=True):
            href_links.append(itHref.get('href'))
        return href_links

    def __parse_data(self, data_received, url, current_index):
        if current_index == 0:
            return True

        soup = BeautifulSoup(data_received, "html.parser")

        if soup.find('html').get('lang') != "en":
            return False

        body = soup.find('div', class_="StoryBodyCompanionColumn")

        article_body = ""
        number_of_words = 0

        if body is not None:
            for para in soup.findAll('p'):
                if para is None:
                    continue
                content = para.getText()
                content = re.sub(r"\n+", " ", content)
                number_of_words += len(content.split())
                article_body += content
        else:
            return False

        if number_of_words >= self.__threshold_words:
            article_date = ""
            article_title = ""
            article_summary = ""

            time = soup.find('time')
            if time is not None:
                article_date = time.get('datetime')[0:19] + "Z"

            h1 = soup.find('h1')
            if h1 is not None:
                article_title = h1.get_text()

            p = soup.find("p", id="article-summary")
            if p is not None:
                article_summary = p.get_text()

            self.__json_data.append(
                {
                    "articleID": current_index,
                    "articleUrl": url,
                    "articleDate": article_date,
                    "articleTitle": article_title,
                    "articleSummary": article_summary,
                    "articleBody": article_body
                }
            )
            return True
        else:
            return False

    def __extract_href_for_last_visited_url(self, last_visited_url):
        href_links = []
        try:
            request = urllib2.urlopen(last_visited_url)
            data_received = request.read()
            href_links = self.__extract_href(data_received)
            href_links = self.__remove_excess(href_links)
        except Exception:
            print("Exception extractig href for last visited urls")
            pass
        return href_links


    def __crawl_website(self, url, current_index):
        href_links = []
        flag = False

        try:
            request = urllib2.urlopen(url)
            data_received = request.read()
            flag = self.__parse_data(data_received, url, current_index)
            href_links = self.__extract_href(data_received)
            href_links = self.__remove_excess(href_links)
        except Exception:
            print("Exception")
            pass
        return href_links, flag

    def __bfs(self, start_url, url_limit, url_per_file):
        try:
            global_crawl_index = self.__global_index+1
            self.__url_queue.append(start_url)
            url_limit = url_limit - len(self.__url_map)
            while global_crawl_index < url_limit:
                if len(self.__url_queue) == 0:
                    print("URL size=0")
                    break
                url = self.__url_queue.popleft()
                if url not in self.__url_map:
                    self.__url_map[url] = True

                    current_index = global_crawl_index
                    href_list, should_increment_crawler_index = self.__crawl_website(url, current_index)
                    self.__url_queue.extend(href_list)

                    if should_increment_crawler_index:
                        global_crawl_index += 1
                        if global_crawl_index % url_per_file == 0:
                            self.__write_json()
            self.__write_json()
            self.__write_urls()
        except:
            print ('Error occurred')
            self.__write_json()
            self.__write_urls()

    def crawl(self, start, url_limit, url_per_file):
        self.__url_queue = deque()
        start_url = self.__get_visited_urls(url_per_file)
        if start_url is not None:
            start = start_url
        if (start_url):
            href_list = self.__extract_href_for_last_visited_url(start_url)
            self.__url_queue.extend(href_list)
        self.__bfs(start, url_limit, url_per_file)
