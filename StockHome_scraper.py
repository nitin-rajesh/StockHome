import yfinance as yf
import sys
from datetime import date, datetime, timedelta

yesterday = datetime.now() - timedelta(4)

aapl_df = yf.download(sys.argv[1],start = datetime.strftime(yesterday,'%Y-%m-%d'), end = date.today().strftime('%Y-%m-%d'))

print(aapl_df['Close'][1])