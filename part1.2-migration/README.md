# Migration

In this part you will learn to migrate tests from sequential to parallel execution.

First, familiarize yourself with [code](src) in this module.

- Run tests `./gradlew --rerun-tasks :part1.2-migration:test :part1.2-migration:createTestsExecutionReport --continue`
- Check [reports](build/reports/tests-execution/html/test.html)

- Enable parallel execution
  in [`SpockConfig.groovy`](src/test/resources/SpockConfig.groovy)
- Run tests
  again `./gradlew --rerun-tasks :part1.2-migration:test :part1.2-migration:createTestsExecutionReport --continue`

Some tests failed. Let's fix it step by step.

- Add `@Isolated` (`spock.lang.Isolated`)
  to [`AppendTextSpec`](src/test/groovy/migration/AppendTextSpec.groovy), [`SetTextSpec`](src/test/groovy/migration/SetTextSpec.groovy)
  and [`MetricsSpec`](src/test/groovy/migration/MetricsSpec.groovy)
- Run tests `./gradlew --rerun-tasks :part1.2-migration:test :part1.2-migration:createTestsExecutionReport --continue`
- Check [reports](build/reports/tests-execution/html/test.html)
- Repeat:
    - Remove `@Isolated` in one class (start with `AppendTextSpec`, then `SetTextSpec`)
    - Fix tests
    - Run
      tests `./gradlew --rerun-tasks :part1.2-migration:test :part1.2-migration:createTestsExecutionReport --continue`
    - Check [reports](build/reports/tests-execution/html/test.html)

---

[home](../README.md)
