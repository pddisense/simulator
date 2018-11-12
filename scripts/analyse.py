#!/usr/bin/env python

import os
import sys
import json

results_dir = sys.argv[1]
sep = ','
capture_params = ['groups', 'queries', 'delay', 'gs']
capture_metrics = ['count_mae', 'count_mre', 'freq_mae', 'freq_mre', 'confidence']

def get_value(payload):
    if 'floatValue' in payload:
        return payload['floatValue']
    if 'stringValue' in payload:
        return payload['stringValue']
    if 'boolValue' in payload:
        return payload['boolValue']
    if 'intValue' in payload:
        return payload['intValue']
    return None


def write_header():
    fields = []
    fields.append('run_id')
    fields.extend(capture_params)
    fields.extend(capture_metrics)
    return sep.join(fields)


def write_row(run_id, params, metrics):
    fields = []
    fields.append(run_id)
    for name in capture_params:
        fields.append(str(params[name]) if name in params else '')
    for name in capture_metrics:
        fields.append(str(metrics[name]) if name in metrics else '')
    return sep.join(fields)


print(write_header())
for run_id in os.listdir(results_dir):
    stderr_file = results_dir + '/' + run_id + '/stderr'
    params = {}
    metrics = {}
    with open(stderr_file) as f:
        line = f.readline()
        while len(line) > 0:
            if line.startswith('LUMOS '):
                event = json.loads(line[6:])
                if 'logParam' in event:
                    name = event['logParam']['name']
                    if name in capture_params:
                        params[name] = get_value(event['logParam'])
                if 'logChannel' in event:
                    name = event['logChannel']['name']
                    if name in capture_metrics:
                        metrics[name] = get_value(event['logChannel'])
            line = f.readline()
    print(write_row(run_id, params, metrics))
