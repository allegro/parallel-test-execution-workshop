package pl.allegro.tech.workshops.testsparallelexecution.support

import com.github.tomakehurst.wiremock.http.Fault
import groovy.transform.ToString

import java.time.Duration

class AbstractResponse {
}

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
class FaultResponse extends AbstractResponse {
    Fault fault
}

@ToString(includePackage = false, includeNames = true, ignoreNulls = true)
class Response extends AbstractResponse {
    static final OK = new Response(status: 200)
    Integer status = 200
    Duration delay = Duration.ZERO
}