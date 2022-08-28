import crawler

if __name__ == '__main__':
    first_article_link = "https://www.nytimes.com"
    url_per_file = 1000
    url_limit = 150000
    crawler.Crawler().crawl(first_article_link, url_limit, url_per_file)
