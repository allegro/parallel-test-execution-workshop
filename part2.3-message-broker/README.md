# Refactoring integration tests in an example REST service with a message broker

[//]: # (TODO: describe components [Hermes] used in this app)

### Familiarize yourself with this service

Run service locally and check documentation.

1. Run the service `./gradlew :part2.3-message-broker:bootRun` and go
   to [documentation](http://localhost:8080/swagger-ui/index.html)

### Refactor tests

1. Run tests `./gradlew --rerun-tasks :part2.3-message-broker:test :part2.3-message-broker:createTestsExecutionReport`
2. Check [reports](build/reports/tests-execution/html/test.html)
3. Enable parallel execution (in [SpockConfig.groovy](src/test/resources/SpockConfig.groovy))
4. Run tests `./gradlew --rerun-tasks :part2.3-message-broker:test :part2.3-message-broker:createTestsExecutionReport`
5. Determine and remove shared state.

#### Shared state

- stubs regarding message broker (HermesMock state)

What to check?

- test setup/cleanup
- stubs
- assertions

---
[home](../README.md)