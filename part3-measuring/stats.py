#!/usr/bin/env python3

import csv
import statistics

with open('../.build-time.csv') as csvfile:
    builtimereader = csv.DictReader(csvfile)
    test_times = [int(row["ms"]) for row in list(builtimereader) if
                  row["task"] == ":part3-measuring:test" and row["success"] == "true" and row["did_work"] == "true" and
                  row["skipped"] == "false"]
    print("min:", min(test_times))
    print("max:", max(test_times))
    print("mean:", statistics.mean(test_times))
