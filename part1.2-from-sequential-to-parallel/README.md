# Migration

In this part you will learn to migrate tests from sequential to parallel execution.

First, familiarize yourself with [code](src) in this module.

## Possible problems after switching from sequential to parallel execution

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

- Run
  tests `./gradlew --rerun-tasks :part1.2-from-sequential-to-parallel:test :part1.2-from-sequential-to-parallel:createTestsExecutionReport --continue`
- Check [reports](build/reports/tests-execution/html/test.html)

- Enable parallel execution
  in [`SpockConfig.groovy`](src/test/resources/SpockConfig.groovy)
- Run tests
  again `./gradlew --rerun-tasks :part1.2-from-sequential-to-parallel:test :part1.2-from-sequential-to-parallel:createTestsExecutionReport --continue`

Some tests failed. Let's fix it step by step.

- Add `@Isolated` (`spock.lang.Isolated`)
  to [`AppendTextSpec class`](src/test/groovy/migration/AppendTextSpec.groovy), [`SetTextSpec class`](src/test/groovy/migration/SetTextSpec.groovy), [`MetricsSpec class`](src/test/groovy/migration/MetricsSpec.groovy)
  and [`RemoveFileSpec class`](src/test/groovy/migration/RemoveFileSpec.groovy)
- Run
  tests `./gradlew --rerun-tasks :part1.2-from-sequential-to-parallel:test :part1.2-from-sequential-to-parallel:createTestsExecutionReport --continue`
- Check [reports](build/reports/tests-execution/html/test.html)
- Repeat:
    - Remove `@Isolated` in one class (start
      with [`AppendTextSpec class`](src/test/groovy/migration/AppendTextSpec.groovy),
      then [`SetTextSpec class`](src/test/groovy/migration/SetTextSpec.groovy)
      and [`RemoveFileSpec class`](src/test/groovy/migration/RemoveFileSpec.groovy))
    - Fix tests
    - Run
      tests `./gradlew --rerun-tasks :part1.2-from-sequential-to-parallel:test :part1.2-from-sequential-to-parallel:createTestsExecutionReport --continue`
    - Check [reports](build/reports/tests-execution/html/test.html)

---

[home](../README.md)
