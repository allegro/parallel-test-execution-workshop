package pl.allegro.tech.workshops.testsparallelexecution.email

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.stubbing.Scenario
import pl.allegro.tech.workshops.testsparallelexecution.support.Request
import pl.allegro.tech.workshops.testsparallelexecution.support.Response

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static org.springframework.http.HttpHeaders.ACCEPT
import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

trait EmailServerStub {

    abstract WireMockServer getWiremockServer()

    void stubPostJson(Request request, List<Response> responses) {
        stubPostJson(new Request(path: request.path, scenario: new Request.RequestScenario(name: 'test', inState: Scenario.STARTED, toState: "after request 0")), responses.first())
        responses.drop(1).eachWithIndex { response, index ->
            stubPostJson(new Request(path: request.path, scenario: new Request.RequestScenario(name: 'test', inState: "after request ${index}", toState: "after request ${index + 1}")), response)
        }
    }

    void stubPostJson(Request request, Response response) {
        def responseDefinitionBuilder = aResponse()
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        if (response.fault != null) {
            responseDefinitionBuilder.withFault(response.fault)
        } else if (response.status > 0) {
            responseDefinitionBuilder.withStatus(response.status)
        }
        if (response.delay != null) {
            responseDefinitionBuilder.withFixedDelay(response.delay.toMillis().intValue())
        }

        def mappingBuilder = post(urlEqualTo(request.path))
        if (request?.scenario) {
            mappingBuilder
                    .inScenario(request.scenario.name)
                    .whenScenarioStateIs(request.scenario.inState)
                    .willSetStateTo(request.scenario.toState)
        }
        if (request?.body) {
            def requestBody = new ObjectMapper().writeValueAsString(request.body)
            mappingBuilder.withRequestBody(equalToJson(requestBody))
        }
        wiremockServer.stubFor(mappingBuilder
                .willReturn(responseDefinitionBuilder))
    }

    void verifyPostJson(Request request, Object response) {
        def body = new ObjectMapper().writeValueAsString(response)
        wiremockServer.verify(postRequestedFor(urlEqualTo(request.path))
                .withHeader(ACCEPT, equalTo("application/json, application/*+json"))
                .withRequestBody(equalToJson(body)))
    }

    void verifyNoPostJson(String path) {
        wiremockServer.verify(0, postRequestedFor(urlEqualTo(path))
                .withHeader(ACCEPT, equalTo("application/json, application/*+json")))
    }


}
