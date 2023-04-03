#!/usr/bin/env python3

import csv
import statistics

with open('../.build-time.csv') as csvfile:
    builtimereader = csv.DictReader(csvfile)
    mean = statistics.mean([int(row["ms"]) for row in list(builtimereader) if
                            row["task"] == ":part3-measuring:test" and row["success"] == "true" and row[
                                "did_work"] == "true" and row["skipped"] == "false"])
    print("mean:", mean)
