# Speeding up test execution time in real projects

## Project A (REST API with REST dependencies)

| Task            | Serial execution | Parallel execution | Difference | % Difference |
|-----------------|------------------|--------------------|------------|--------------|
| unitTest        | 4.3s             | 3.4s               | -0.9s      | -21%         |
| integrationTest | 19.6s            | 13.7s              | -5.9s      | -30%         |

## Project B (REST API with database and message broker)

| Task            | Serial execution | Parallel execution | Difference | % Difference |
|-----------------|------------------|--------------------|------------|--------------|
| unitTest        | 5.4s             | 3.9s               | -1.5s      | -28%         |
| integrationTest | 45.6s            | 34.6s              | -11.0s     | -24%         |

## Project C (REST API with REST dependencies)

| Task     | Serial execution | Parallel execution | Difference | % Difference |
|----------|------------------|--------------------|------------|--------------|
| unitTest | 1.6s             | 1.3s               | -0.3s      | -19%         |

## Project D (Spark SQL with BigQuery)

| Task  | Serial execution | Parallel execution | Difference      | % Difference |
|-------|------------------|--------------------|-----------------|--------------|
| tests | 26 min 44 sec    | 4 min 23 sec       | -22 min 21 secs | -84%         |

---

[home](../README.md)
