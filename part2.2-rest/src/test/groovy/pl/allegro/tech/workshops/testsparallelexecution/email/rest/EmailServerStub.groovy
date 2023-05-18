package pl.allegro.tech.workshops.testsparallelexecution.email.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.stubbing.Scenario
import pl.allegro.tech.workshops.testsparallelexecution.support.*

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static org.springframework.http.HttpHeaders.ACCEPT
import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

trait EmailServerStub {

    abstract WireMockServer getWiremockServer()

    void stubPostJson(Request request, List<AbstractResponse> responses, scenarioName = 'test') {
        def scenario = new ScenarioRequest.RequestScenario(name: scenarioName, inState: Scenario.STARTED, toState: "after request 0")
        stubPostJson(new ScenarioRequest(request: request, scenario: scenario), responses.first())
        responses.drop(1).eachWithIndex { response, index ->
            scenario = new ScenarioRequest.RequestScenario(name: scenarioName, inState: "after request ${index}", toState: "after request ${index + 1}")
            stubPostJson(new ScenarioRequest(request: request, scenario: scenario), response)
        }
    }

    void stubPostJson(Request request, AbstractResponse response) {
        stubPostJson(new ScenarioRequest(request: request), response)
    }

    void stubPostJson(ScenarioRequest scenarioRequest, AbstractResponse response) {
        def responseDefinitionBuilder = aResponse()
        if (response instanceof FaultResponse && response.fault != null) {
            responseDefinitionBuilder.withFault(response.fault)
        } else if (response instanceof Response && response.status > 0) {
            responseDefinitionBuilder.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            responseDefinitionBuilder.withStatus(response.status)
            if (response.delay != null) {
                responseDefinitionBuilder.withFixedDelay(response.delay.toMillis().intValue())
            }
        }

        def mappingBuilder = post(urlEqualTo(scenarioRequest.request.path))
        if (scenarioRequest?.scenario) {
            mappingBuilder
                    .inScenario(scenarioRequest.scenario.name)
                    .whenScenarioStateIs(scenarioRequest.scenario.inState)
                    .willSetStateTo(scenarioRequest.scenario.toState)
        }
        if (scenarioRequest.request?.body) {
//            def requestBody = new ObjectMapper().writeValueAsString(scenarioRequest.request.body)
//            mappingBuilder.withRequestBody(equalToJson(requestBody))
        }
        wiremockServer.stubFor(mappingBuilder
                .willReturn(responseDefinitionBuilder))
    }

    void verifyPostJson(Request request) {
        def body = new ObjectMapper().writeValueAsString(request.body)
        wiremockServer.verify(postRequestedFor(urlEqualTo(request.path))
                .withHeader(ACCEPT, equalTo("application/json, application/*+json"))
//                .withRequestBody(equalToJson(body))
        )
    }

    void verifyNoPostJson(Request request) {
        def body = new ObjectMapper().writeValueAsString(request.body)
        wiremockServer.verify(0, postRequestedFor(urlEqualTo(request.path))
                .withHeader(ACCEPT, equalTo("application/json, application/*+json"))
//                .withRequestBody(equalToJson(body))
        )
    }

}
