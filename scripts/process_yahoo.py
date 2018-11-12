#!/usr/bin/env python

import sys
import pandas as pd
from scipy.stats import norm

SECONDS_PER_DAY = 24 * 3600

if len(sys.argv) < 2:
    print('Usage: process_yahoo.py <path to search_logs-v1.txt>')
    exit(1)

search_logs = pd.read_csv(
    sys.argv[1],
    sep='\t',
    header=0,
    usecols=[0, 1, 2],
    names=['query', 'user', 'timestamp'],
)

# Replace the timestamp by a day, relative to the beginning of the logs.
min_time = search_logs['timestamp'].agg('min')
search_logs['day'] = (search_logs['timestamp'] - min_time) // SECONDS_PER_DAY
search_logs.drop('timestamp', axis=1, inplace=True)

# Compute the distribution of the number of searches per user per day.
counts = search_logs.groupby(['day', 'user'])['query'].agg({'count': 'count'})['count']
counts.to_csv('volume.csv')

# ... and fit a distribution to represent it.
mu, var = norm.fit(counts)
print('mu=' + str(mu) + ' var=' + str(var))

# Compute the frequency of each query.
search_logs['query'].value_counts(normalize=True, sort=False).to_csv('queries.csv')