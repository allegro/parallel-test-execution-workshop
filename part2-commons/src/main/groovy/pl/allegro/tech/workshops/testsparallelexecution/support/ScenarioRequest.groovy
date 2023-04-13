package pl.allegro.tech.workshops.testsparallelexecution.support


import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
class ScenarioRequest {
    Request request
    RequestScenario scenario

    @ToString(includePackage = false, includeNames = true, ignoreNulls = true)
    static class RequestScenario {
        String name, inState, toState
    }
}