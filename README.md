# Parallel test execution workshop

[![CI](https://github.com/allegro/parallel-test-execution-workshop/actions/workflows/ci.yml/badge.svg)](https://github.com/allegro/parallel-test-execution-workshop/actions/workflows/ci.yml)

This repository contains code and instructions used in parallel execution workshop. Examples are based
on [parallel execution](https://spockframework.org/spock/docs/2.3/parallel_execution.html) feature
for [Spock](https://spockframework.org), but concepts shown here can be used with other testing frameworks (JVM or
non-JVM).


---

## Table of contents

### [1. Introduction to parallel execution in Spock](part1.0-introduction/README.md)

#### [1.1 Shared state](part1.1-shared-state/README.md)

#### [1.2 From sequential to parallel execution](part1.2-from-sequential-to-parallel/README.md)

### 2. Refactoring integration tests in an example service

#### [2.1 REST service with a database](part2.1-database/README.md)

#### [2.2 REST service with a REST dependency](part2.2-rest/README.md)

#### [2.3 REST service with a message broker](part2.3-message-broker/README.md)

### [3. Measuring test execution time](part3.0-measuring/README.md)

### [4. Summary](part4.0-summary/README.md)

---

### Requirements (to build/run this project)

- JDK 21
- Docker

Sample applications and tests are written using:

- Spring + Spring Boot
- MongoDB
- [Java Client for Hermes](https://hermes-pubsub.readthedocs.io/en/latest/user/java-client/)
- Spock
- Testcontainers
- WireMock
- [Hermes Mock](https://hermes-pubsub.readthedocs.io/en/latest/user/hermes-mock/)
- [tests-execution-chart Gradle plugin](https://github.com/platan/tests-execution-chart) (tests execution schedule
  visualization)
