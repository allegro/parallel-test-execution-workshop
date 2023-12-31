# Measuring test execution time

This chapter shows methods of measuring test execution time.

## Incremental build

Gradle has a feature
called [Incremental build](https://docs.gradle.org/current/userguide/incremental_build.html#incremental_build). It
allows to skip a task when the task not has to be run.

Clean project:
`./gradlew :part3.0-measuring:clean`

Run build 2 times (run this command in terminal):

```
./gradlew :part3.0-measuring:test -i
```

Was there a difference between the execution time in each run?

## Forcing tasks to execute

You can [force task to execute](https://docs.gradle.org/current/userguide/command_line_interface.html#sec:rerun_tasks)
(run this command in terminal):

```
./gradlew :part3.0-measuring:clean :part3.0-measuring:test -i
```

or (run this command in terminal)

```
./gradlew :part3.0-measuring:test -i --rerun-tasks
```

or (run this command in terminal)

```
./gradlew :part3.0-measuring:test --rerun -i
```

## Measuring options

### Build time

(run this command in terminal)

```
./gradlew :part3.0-measuring:test --rerun | grep "BUILD SUCCESSFUL"
```

```
BUILD SUCCESSFUL in 5s
```

### [Profile report](https://docs.gradle.org/current/userguide/inspect.html#profile_report)

(run this command in terminal)

```
./gradlew :part3.0-measuring:test --rerun -i --profile | grep "profiling report"
```

```
See the profiling report at: file:///Users/x/workspaces/parallel-test-execution-workshop/build/reports/profile/profile-2023-03-29-11-21-02.html
```

A sample output:
> Task Execution\
> \
> Task Duration Result\
:part3.0-measuring:test 5.145s

### Build Time Tracker

[Build Time Tracker](https://github.com/passy/build-time-tracker-plugin) is a Gradle plugin to continuously track and
report your build times.

- uncomment plugin configuration in [build.gradle](build.gradle) (at the beginning and at the end of the file)

   ```groovy
   plugins {
       id "net.rdrei.android.buildtimetracker" version "0.11.0"
   }
   
   buildtimetracker {
       reporters {
           csv {
               append true
               header true
               output ".build-time.csv"
           }
       }
   }
   ```

- run tests `./gradlew :part3.0-measuring:test --rerun`
- show check results in [.build-time.csv](.build-time.csv)

```shell
cat .build-time.csv
```

```csv
"timestamp","order","task","success","did_work","skipped","ms","date","cpu","memory","os"
"1680249109103","0",":part3.0-measuring:compileJava","true","false","true","1","2023-03-31T07:51:49,103Z","Apple M1 Pro","34359738368","Mac OS X 13.3 aarch64"
"1680249109103","1",":part3.0-measuring:compileGroovy","true","false","true","0","2023-03-31T07:51:49,103Z","Apple M1 Pro","34359738368","Mac OS X 13.3 aarch64"
"1680249109103","2",":part3.0-measuring:processResources","true","false","true","0","2023-03-31T07:51:49,103Z","Apple M1 Pro","34359738368","Mac OS X 13.3 aarch64"
"1680249109103","3",":part3.0-measuring:classes","true","false","true","0","2023-03-31T07:51:49,103Z","Apple M1 Pro","34359738368","Mac OS X 13.3 aarch64"
"1680249109103","4",":part3.0-measuring:compileTestJava","true","false","true","0","2023-03-31T07:51:49,103Z","Apple M1 Pro","34359738368","Mac OS X 13.3 aarch64"
"1680249109103","5",":part3.0-measuring:compileTestGroovy","true","false","true","12","2023-03-31T07:51:49,103Z","Apple M1 Pro","34359738368","Mac OS X 13.3 aarch64"
"1680249109103","6",":part3.0-measuring:processTestResources","true","false","true","1","2023-03-31T07:51:49,103Z","Apple M1 Pro","34359738368","Mac OS X 13.3 aarch64"
"1680249109103","7",":part3.0-measuring:testClasses","true","false","true","0","2023-03-31T07:51:49,103Z","Apple M1 Pro","34359738368","Mac OS X 13.3 aarch64"
"1680249109103","8",":part3.0-measuring:test","true","true","false","1000","2023-03-31T07:51:49,103Z","Apple M1 Pro","34359738368","Mac OS X 13.3 aarch64"
```

(run this command in terminal)

```
cd part3.0-measuring
for i in {1..5}; do ../gradlew test --rerun 2>&1; done
cd -
```

`./gradlew :part3.0-measuring:stats -Pargs='-stats_file .build-time.csv -task_name :part3.0-measuring:test'`

or alternatively (requires Python 3)

```shell
./stats.py .build-time.csv :part3.0-measuring:test
```

```
min: 960
max: 1036
mean: 999
count: 5
```

### Exercise - measuring test speed-up

1. Check execution time of `test` task with parallel execution disabled.
2. Enable parallel execution in [SpockConfig.groovy](src/test/resources/SpockConfig.groovy) and remove `.build-time.csv`
   file.
3. Check execution time of `test` task with parallel execution enabled and compare results with previous results.

### Speed-up limits

1. Enable parallel execution in [SpockConfig.groovy](src/test/resources/SpockConfig.groovy).
2. Run
   tests `./gradlew --rerun-tasks :part3.0-measuring:test :part3.0-measuring:createTestsExecutionReport`
3. Check [reports](build/reports/tests-execution/html/test.html)
4. Run
   tests `./gradlew --rerun-tasks :part3.0-measuring:test :part3.0-measuring:createTestsExecutionReport -PdisplaySuites=true`.
   Report includes execution time of tests (methods/features), suites (classes/specs) and Gradle executors and tasks.
5. Check [reports](build/reports/tests-execution/html/test.html)

---

[home](../README.md)
