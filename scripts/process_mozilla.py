#!/usr/bin/env python

from __future__ import division
import pandas as pd
import numpy as np
from scipy.stats import beta, kstest
import sys
import os


MILLIS_PER_HOUR = 3600 * 1000
DAYS_PER_WEEK = 7
HOURS_PER_DAY = 24
HOURS_PER_WEEK = DAYS_PER_WEEK * HOURS_PER_DAY


def fit_beta(i, df):
    loc = 0
    scale = 7
    records = []
    user = 0

    for row in df.itertuples():
        daily = dict(zip(range(DAYS_PER_WEEK), [0] * DAYS_PER_WEEK))
        avg_session_length = row.total_session_length / row.num_sessions

        def propagate(day):
            if daily[day] > HOURS_PER_DAY:
                if day < 6:
                    daily[day + 1] += daily[day] - HOURS_PER_DAY
                    propagate(day + 1)
                daily[day] = HOURS_PER_DAY

        for j in range(row.num_sessions):
            valid_days = [k for k, v in daily.items() if v < HOURS_PER_DAY]
            if len(valid_days) == 0:
                break
            day = np.random.choice(valid_days)
            daily[day] += avg_session_length
            propagate(day)

        for k, v in daily.items():
            if v > 0:
                records.append({'user': user, 'day': k, 'duration': min(HOURS_PER_DAY, v)})
        user += 1

    activity = pd.DataFrame.from_records(records)
    dist = activity.groupby('user')['day'].count()
    dist.to_csv('activity-' + str(i) + '.csv')

    a, b, _, _ = beta.fit(dist, loc=loc, scale=scale)
    distance = kstest(dist, 'beta', (a, b, loc, scale)).statistic
    print('iter[' + str(i) + '] a=' + str(a) + ' b=' + str(b) + ' distance=' + str(distance))

    return a, b, distance


if len(sys.argv) < 2:
    print('Usage: process_mozilla.py <path to week-life-stats-70.csv>')
    exit(1)

df = pd.read_csv(sys.argv[1], usecols=[13, 15])
df['total_session_length'] = df['total_session_length'] // MILLIS_PER_HOUR
df = df.loc[(df['total_session_length'] < HOURS_PER_WEEK) & (df['total_session_length'] > 0)]

tries = 100

best_i = -1
best_a = 0
best_b = 0
best_distance = float('inf')
for i in range(tries):
    a, b, distance = fit_beta(i, df)
    if distance < best_distance:
        best_a = a
        best_b = b
        best_distance = distance
        best_i = i

print('a=' + str(best_a) + ' b=' + str(best_b) + ' distance=' + str(best_distance))

os.rename('activity-' + str(best_i) + '.csv', 'activity.csv')
for i in range(tries):
    if i != best_i:
        os.remove('activity-' + str(i) + '.csv')
