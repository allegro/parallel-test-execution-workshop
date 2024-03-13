## Parallel thread pool

Supported options:

- `dynamic(BigDecimal factor)`
- `dynamicWithReservedProcessors(BigDecimal factor, int reservedProcessors)`
- `fixed(int parallelism)`
- `custom(int parallelism, int minimumRunnable, int maxPoolSize, int corePoolSize, int keepAliveSeconds)` ([docs](https://spockframework.org/spock/javadoc/2.3/spock/config/ParallelConfiguration.html#custom(int,int,int,int,int)))

Default: `dynamicWithReservedProcessors(1.0, 2)` (`Runtime.getRuntime().availableProcessors() * 1.0 - 2`)

Example thread pool configuration:

```groovy
runner {
    parallel {
        enabled true
        fixed(4)
    }
}
```

Check number of available processors:

```sh
jshell print-available-processors.jsh
```

Sample output:

```
Runtime.getRuntime().availableProcessors() = 10
```

### Task

- Add test case to class `A`

```groovy
def "test 3"() {
    sleep SLEEP_DURATION

    expect:
    true

    where:
    data << (1..20)
}
```

- Click `Check` to run tests. Now check [report](file://part1.0-introduction/parallel-thread-pool/build/reports/tests-execution/html/test.html) with visualization of test execution schedule.
- Configure a thread pool of your choice, run tests and check reports
