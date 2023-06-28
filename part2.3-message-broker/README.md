# Refactoring integration tests in an example REST service with a message broker

### Familiarize yourself with this service

```mermaid
sequenceDiagram
    participant User as User
    participant REST API as REST API
    participant external service as message broker
    Note over User, external service: send e-mail
    User ->>+ REST API: POST /emails {"subject": "New ...", "sender": "...", "recipient": "..."}
    REST API ->>+ external service: POST /topics/pl.allegro.[...].email {"subject": "New ...", "sender": "...", "recipient": "..."}
    external service -->>- REST API: response
    REST API -->>- User: response
```

Run service locally and check documentation.

1. Run the service `./gradlew :part2.3-message-broker:bootRun` and go
   to [documentation](http://localhost:8080/swagger-ui/index.html)

### Refactor tests

1. Run
   tests `./gradlew --rerun-tasks :part2.3-message-broker:test :part2.3-message-broker:createTestsExecutionReport --continue`
2. Check [reports](build/reports/tests-execution/html/test.html)
3. Enable parallel execution (in [SpockConfig.groovy](src/test/resources/SpockConfig.groovy))
4. Run
   tests `./gradlew --rerun-tasks :part2.3-message-broker:test :part2.3-message-broker:createTestsExecutionReport --continue`
5. Determine and remove shared state.

#### Shared state

- stubs regarding message broker (HermesMock state)

What to check?

- test setup/cleanup
- stubs (
  check [Hermes Mock documentation](https://hermes-pubsub.readthedocs.io/en/latest/user/hermes-mock/#hermesmockdefine))
- assertions (
  check [Hermes Mock documentation](https://hermes-pubsub.readthedocs.io/en/latest/user/hermes-mock/#hermesmockexpect))

---
[home](../README.md)