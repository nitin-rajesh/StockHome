from bs4 import BeautifulSoup
from urllib.request import Request, urlopen
import requests
import sys


headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36'}#soup = BeautifulSoup(html_page, "lxml")
links = []
count = 0

URL = 'https://coinmarketcap.com/currencies/' + sys.argv[1]

page = requests.get(URL)

soup = BeautifulSoup(page.content,"html.parser")
for price in soup.findAll("div",{"class":"priceValue"}):
    print(price.string.replace('$','').replace(',',''))
