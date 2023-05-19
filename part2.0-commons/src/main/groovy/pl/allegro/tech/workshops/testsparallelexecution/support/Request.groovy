package pl.allegro.tech.workshops.testsparallelexecution.support


import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
class Request {
    String path
    Object body
}