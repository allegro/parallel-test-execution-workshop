# Summary

## Try to avoid

- Cleanup of shared state

```diff
def setup() {
-   removeAllDocuments()
}
```

```diff
def setup() {
-   wireMockServer.resetAll()
-   wireMockServer.resetScenarios()
}
```

- Creating entities with same id/unique element

```diff
-   def id = '2'
+   def id = getRandomId()
```

- Using not precise enough assertions

```diff
-   countDocuments() == 1
+   documentExistsInDatabase(id)
```

```diff
-   countPublishedEvents() == 1
+   countPublishedEvents(id) == 1
```

## [Sample results](./results.md)

---

[home](../README.md)
