## Parallel execution
Enable parallel execution
in [`SpockConfig.groovy`](file://part1.0-introduction/parallel-execution/src/test/resources/SpockConfig.groovy):

```groovy
runner {
    parallel {
        enabled true
    }
}
```

Click `Check` to run tests. Now check [report](file://part1.0-introduction/no-parallelization/build/reports/tests-execution/html/test.html) with visualization of test execution schedule. 