#!/usr/bin/env python3

import argparse
import csv
import statistics

parser = argparse.ArgumentParser(description="Show task stats based on data reported by buildtimetracker")

parser.add_argument('stats_file', type=argparse.FileType('r'), help="A CSV file created by buildtimetracker")
parser.add_argument('task_name', type=str)
args = parser.parse_args()

with args.stats_file as csvfile:
    builtimereader = csv.DictReader(csvfile)
    test_times = [int(row["ms"]) for row in list(builtimereader) if
                  row["task"] == args.task_name and row["success"] == "true" and row["did_work"] == "true" and
                  row["skipped"] == "false"]
    if test_times:
        print("min:", min(test_times))
        print("max:", max(test_times))
        print("mean:", round(statistics.mean(test_times)))
