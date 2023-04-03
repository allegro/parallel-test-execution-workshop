#!/usr/bin/env Rscript
buildtime = read.csv("../.build-time.csv")
testtime = buildtime[buildtime$task == ":part3-measuring:test" & buildtime$success == "true" & buildtime$did_work == "true" & buildtime$skipped == "false", "ms"]
summary(strtoi(testtime))
