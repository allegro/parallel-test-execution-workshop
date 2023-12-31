# Shared state

In this part you will see examples of shared state modifications in tests.

## Sequential execution of tests

```text
             time
 ─────────────────────────────►
 ┌───────────┐   ┌───────────┐
 │  test 1   │   │  test 2   │
 └─┬───┬───┬─┘   └─┬───┬───┬─┘
   │   │   │       │   │   │
   │+  │?  │-      │+  │?  │-
   │a  │a  │a      │a  │a  │a
   │   │   │       │   │   │
   ▼   ▼   ▼       ▼   ▼   ▼
   ✓   ✓   ✓       ✓   ✓   ✓
```

```text
  symbol | description
  +      | add entity (`given/when` block)
  ?      | verify (`then` block)
  -      | remove entity/entities (`cleanup` block)
  a      | entity id
  *      | all entities
  ✓      | operation succeeded
  !      | operation failed
```

## Possible problems after switching from sequential to parallel execution

### Cannot create two entities with same id/name in test setup

```text
             time
 ─────────────────────────────►
 ┌───────────┐
 │  test 1   │
 └─┬─────────┘
   │
 ┌─┼─────────┐
 │ │test 2   │
 └─┼──┬──────┘
   │  │
   │+ │+
   │a │a
   │  │
   ▼  ▼
   ✓  !
```

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

## Exercise

Familiarize yourself with [tests](src/test/groovy) in this module.

- Run tests `./gradlew --rerun-tasks :part1.1-shared-state:test :part1.1-shared-state:createTestsExecutionReport`
- Check [reports](build/reports/tests-execution/html/test.html)
- Enable parallel execution
  in [`SpockConfig.groovy`](src/test/resources/SpockConfig.groovy)
- Run tests
  again `./gradlew --rerun-tasks :part1.1-shared-state:test :part1.1-shared-state:createTestsExecutionReport --continue`

Some tests failed. Eliminate shared state by using different `name` in test cases.

### Approach no. 1

- Set a unique `name` value in each test.
- Run tests
  again `./gradlew --rerun-tasks :part1.1-shared-state:test :part1.1-shared-state:createTestsExecutionReport --continue`
- Check output

Sample output:

```text
name = testName 3
name = testName 1
name = testName 2
```

### Approach no. 2

- Add `private UniqueValueGenerator generator = RandomUniqueValueGenerator.instance` field.
- Set `name` value in `setup` using `name = generator.next()`
- Remove `name` assignments in tests.
- Run tests
  again `./gradlew --rerun-tasks :part1.1-shared-state:test :part1.1-shared-state:createTestsExecutionReport --continue`
- Check output

Sample output:

```text
name = BM8LX
name = Kbc28
name = boR1b
```

### Approach no. 3

- Add `private UniqueValueGenerator generator = NextIntValueGenerator.instance` field.
- Set `name` value in `setup` using `name = generator.next()`
- Run tests
  again `./gradlew --rerun-tasks :part1.1-shared-state:test :part1.1-shared-state:createTestsExecutionReport --continue`
- Check output

Sample output:

```text
name = 3
name = 1
name = 2
```

### Approach no. 4

- Add `private UniqueValueGenerator generator = new TestNameUniqueValueGenerator(specificationContext)` field.
- Set `name` value in `setup` using `name = generator.next()`
- Run tests
  again `./gradlew --rerun-tasks :part1.1-shared-state:test :part1.1-shared-state:createTestsExecutionReport --continue`
- Check output

Sample output:

```text
name = pl.allegro.tech.workshops.testsparallelexecution:SharedStateExampleTest:should remove file
name = pl.allegro.tech.workshops.testsparallelexecution:SharedStateExampleTest:should create dir
name = pl.allegro.tech.workshops.testsparallelexecution:SharedStateExampleTest:should create file
```

[//]: # (Diagrams in this section were created using https://asciiflow.com)

---

[home](../README.md)
