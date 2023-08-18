# Summary

Try to avoid:

- Cleanup in one test affects other tests

```diff
def setup() {
-   removeAllDocuments()
}
```

```diff
def setup() {
-   wireMockServer.resetAll()
-   wireMockServer.resetScenarios(
}
```

- Cannot create two entities with same id/name in test setup

```diff
-   def id = '2'
+   def id = getRandomId()
```

```diff
-   stubEventSent()
+   stubEventSent(id)
```

- Assertion is not precise enough

```diff
-   countDocuments() == 1
+   documentExistsInDatabase(id)
```

```diff
-   countEventPublished() == 1
+   countEventPublished(id) == 1
```

---

[home](../README.md)