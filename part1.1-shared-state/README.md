# Shared state

In this part you will learn and exercise techniques of eliminating shared state in tests.

```text
             time
 ─────────────────────────────►
 ┌───────────┐   ┌───────────┐
 │  test 1   │   │  test 2   │
 └─┬───┬───┬─┘   └─┬───┬───┬─┘
   │   │   │       │   │   │
   │+  │?  │-      │+  │?  │-
   │a  │   │       │a  │   │
   │   │   │       │   │   │
   ▼   ▼   ▼       ▼   ▼   ▼
   ✓   ✓   ✓       ✓   ✓   ✓


  + add entity
  ? verify
  - remove entity
```

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
   │a│ │b│
   │ │ │ │
   ▼ ▼ ▼ ▼
   ✓ ✓ ✓ !
```

First, familiarize yourself with [tests](src/test/groovy) in this module.

- Run tests `./gradlew --rerun-tasks :part1.1-shared-state:test`
- Enable parallel execution
  in [`SpockConfig.groovy`](src/test/resources/SpockConfig.groovy)
- Run tests again `./gradlew --rerun-tasks :part1.1-shared-state:test`

Some tests failed. Eliminate shared state by using different `name` in test cases.

## Approach no. 1

- Set a unique `name` value in each test.
- Run tests again `./gradlew --rerun-tasks :part1.1-shared-state:test`
- Check output

Sample output:

```text
name = testName 3 readable: false
name = testName 3 readable: true
name = testName 1
name = testName 2
```

## Approach no. 2

- Add `private UniqueValueGenerator generator = new RandomUniqueValueGenerator()` field.
- Set `name` value in `setup` using `name = generator.next()`
- Remove `name` assignments in tests.
- Run tests again `./gradlew --rerun-tasks :part1.1-shared-state:test`
- Check output

Sample output:

```text
name = BM8LX
name = Kbc28
name = boR1b
name = 7Oc3i
```

## Approach no. 3

- Add `private UniqueValueGenerator generator = NextIntSingletonValueGenerator.instance` field.
- Set `name` value in `setup` using `name = generator.next()`
- Run tests again `./gradlew --rerun-tasks :part1.1-shared-state:test`
- Check output

Sample output:

```text
name = 3
name = 1
name = 2
name = 4
```

## Approach no. 4

- Add `private UniqueValueGenerator generator = new TestNameUniqueValueGenerator(specificationContext)` field.
- Set `name` value in `setup` using `name = generator.next()`
- Run tests again `./gradlew --rerun-tasks :part1.1-shared-state:test`
- Check output

Sample output:

```text
name = pl.allegro.tech.workshops.testsparallelexecution:SharedStateExampleTest:should create readable - non-readable file [readable: true, #0]
name = pl.allegro.tech.workshops.testsparallelexecution:SharedStateExampleTest:should create readable - non-readable file [readable: false, #1]
name = pl.allegro.tech.workshops.testsparallelexecution:SharedStateExampleTest:should create dir
name = pl.allegro.tech.workshops.testsparallelexecution:SharedStateExampleTest:should create file
```

---

[home](../README.md)
