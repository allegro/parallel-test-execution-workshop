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
            print()


class CsvFormatter:
    def __init__(self):
        pass

    def format(self, results):
        with io.StringIO() as csv_output:
            field_names = ['file', 'min', 'max', 'mean', 'count']
            writer = csv.DictWriter(csv_output, fieldnames=field_names)
            writer.writeheader()
            for result in results:
                writer.writerow({'file': result['file'],
                                 'min': result['min'],
                                 'max': result['max'],
                                 'mean': result['mean'],
                                 'count': result['count']})
            print(csv_output.getvalue())


class JsonFormatter:
    def __init__(self):
        pass

    def format(self, results):
        for result in results:
            print(json.dumps(result))


def main():
    args = parse_args()
    results = []
    for file in args.stats_file:
        test_times = filter_build_times(file, args.task_name)
        results.append(gather_stats(test_times, file))

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
    parser.add_argument('stats_file', type=argparse.FileType('r'), nargs='+',
                        help="A CSV file created by buildtimetracker")
    parser.add_argument('task_name', type=str)
    parser.add_argument('--format', choices=['csv', 'json', 'table'], default='table')
    args = parser.parse_args()
    return args


def filter_build_times(file, task_name):
    with file as csvfile:
        builtimereader = csv.DictReader(csvfile)
        test_times = [int(row["ms"]) for row in list(builtimereader) if
                      row["task"] == task_name and row["success"] == "true" and row["did_work"] == "true" and
                      row["skipped"] == "false"]
    return test_times


def gather_stats(test_times, file):
    if test_times:
        min_value = min(test_times)
        max_value = max(test_times)
        mean_value = round(statistics.mean(test_times))
        count_value = len(test_times)
        return {'file': file.name,
                'min': min_value,
                'max': max_value,
                'mean': mean_value,
                'count': count_value}
    return {}


if __name__ == '__main__':
    sys.exit(main())
