package pl.allegro.tech.workshops.testsparallelexecution.support

import com.github.tomakehurst.wiremock.http.Fault
import groovy.transform.ToString

import java.time.Duration

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
class Response {
    static final OK = new Response(status: 200)
    Integer status
    Fault fault
    Duration delay
    Object body
}