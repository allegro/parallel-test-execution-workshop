# Migration

In this part you will learn to migrate tests from sequential to parallel execution.

First, familiarize yourself with [code](src) in this module.

## Possible problems after switching from sequential to parallel execution (part 2/3)

### Cleanup in one test affects other tests

```text
             time
 ─────────────────────────────►
 ┌───────────┐
 │  test 1   │
 └─┬───┬─┬───┘
   │   │ │
   │   │ │
 ┌─┼───┼─┼───┐
 │ │test 2   │
 └─┼─┬─┼─┼─┬─┘
   │ │ │ │ │
   │+│+│?│-│?
   │a│b│a│*│b
   │ │ │ │ │
   ▼ ▼ ▼ ▼ ▼
   ✓ ✓ ✓ ✓ !
```

- Run
  tests `./gradlew --rerun-tasks :part1.2-from-sequential-to-parallel:test :part1.2-from-sequential-to-parallel:createTestsExecutionReport --continue`
- Check [reports](build/reports/tests-execution/html/test.html)

- Enable parallel execution
  in [`SpockConfig.groovy`](src/test/resources/SpockConfig.groovy)
- Run tests
  again `./gradlew --rerun-tasks :part1.2-from-sequential-to-parallel:test :part1.2-from-sequential-to-parallel:createTestsExecutionReport --continue`

Some tests failed. Let's fix it step by step.

- Add `@Isolated` (`spock.lang.Isolated`)
  to [`AppendTextSpec`](src/test/groovy/migration/AppendTextSpec.groovy), [`SetTextSpec`](src/test/groovy/migration/SetTextSpec.groovy)
  and [`MetricsSpec`](src/test/groovy/migration/MetricsSpec.groovy)
- Run
  tests `./gradlew --rerun-tasks :part1.2-from-sequential-to-parallel:test :part1.2-from-sequential-to-parallel:createTestsExecutionReport --continue`
- Check [reports](build/reports/tests-execution/html/test.html)
- Repeat:
    - Remove `@Isolated` in one class (start with [`AppendTextSpec`](src/test/groovy/migration/AppendTextSpec.groovy), then [`SetTextSpec`](src/test/groovy/migration/SetTextSpec.groovy))
    - Fix tests
    - Run
      tests `./gradlew --rerun-tasks :part1.2-from-sequential-to-parallel:test :part1.2-from-sequential-to-parallel:createTestsExecutionReport --continue`
    - Check [reports](build/reports/tests-execution/html/test.html)

## Possible problems after switching from sequential to parallel execution (part 3/3)

### Assertion is not precise enough

```text
             time
 ─────────────────────────────►
 ┌───────────┐
 │  test 1   │
 └─┬─┬───────┘
   │ │
 ┌─┼─┼───────┐
 │ │test 2   │
 └─┼─┼─┬─┬───┘
   │ │ │ │
   │+│?│+│?(count)
   │a│a│b│*
   │ │ │ │
   ▼ ▼ ▼ ▼
   ✓ ✓ ✓ !
```

---

[home](../README.md)
