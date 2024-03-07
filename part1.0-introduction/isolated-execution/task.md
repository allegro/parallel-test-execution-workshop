## Isolated execution

![diagram](../.readme/README-Isolated.svg)

- Enable parallel execution (concurrent features, concurrent specifications)
- Create a copy of class `A` and name it `C`
- Add `@Isolated` (`spock.lang.Isolated`) annotation to `C` class
- Click `Check` to run tests. Now check [report](file://part1.0-introduction/no-parallelization/build/reports/tests-execution/html/test.html) with visualization of test execution schedule. 
