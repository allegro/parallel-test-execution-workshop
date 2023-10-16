#!/usr/bin/env python3

import argparse
import csv
import io
import json
import statistics
import sys


class TableFormatter:
    def __init__(self):
        pass

    def format(self, results):
        for result in results:
            for entry_key, entry_value in result.items():
                print(f"{entry_key}: {entry_value}")


class CsvFormatter:
    def __init__(self):
        pass

    def format(self, results):
        with io.StringIO() as csv_output:
            field_names = ['min', 'max', 'mean', 'count']
            writer = csv.DictWriter(csv_output, fieldnames=field_names)
            writer.writeheader()
            for result in results:
                writer.writerow({'min': result['min'],
                                 'max': result['max'],
                                 'mean': result['mean'],
                                 'count': result['count']})
            print(csv_output.getvalue())


class JsonFormatter:
    def __init__(self):
        pass

    def format(self, results):
        if results:
            print(json.dumps(results[0]))


def main():
    args = parse_args()
    test_times = filter_build_times(args)
    results = gather_stats(test_times)

    if results:
        if args.format == 'csv':
            CsvFormatter().format(results)
        if args.format == 'table':
            TableFormatter().format(results)
        if args.format == 'json':
            JsonFormatter().format(results)

    return 0


def parse_args():
    parser = argparse.ArgumentParser(description="Show task stats based on data reported by buildtimetracker")
    parser.add_argument('stats_file', type=argparse.FileType('r'), help="A CSV file created by buildtimetracker")
    parser.add_argument('task_name', type=str)
    parser.add_argument('--format', choices=['csv', 'json', 'table'], default='table')
    args = parser.parse_args()
    return args


def filter_build_times(args):
    with args.stats_file as csvfile:
        builtimereader = csv.DictReader(csvfile)
        test_times = [int(row["ms"]) for row in list(builtimereader) if
                      row["task"] == args.task_name and row["success"] == "true" and row["did_work"] == "true" and
                      row["skipped"] == "false"]
    return test_times


def gather_stats(test_times):
    results = []
    if test_times:
        min_value = min(test_times)
        max_value = max(test_times)
        mean_value = round(statistics.mean(test_times))
        count_value = len(test_times)
        results.append({'min': min_value,
                        'max': max_value,
                        'mean': mean_value,
                        'count': count_value})
    return results


if __name__ == '__main__':
    sys.exit(main())
