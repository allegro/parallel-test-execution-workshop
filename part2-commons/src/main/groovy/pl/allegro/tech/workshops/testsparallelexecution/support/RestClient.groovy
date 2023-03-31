package pl.allegro.tech.workshops.testsparallelexecution.support

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

import static org.springframework.http.HttpMethod.PUT

class RestClient {
    String url
    TestRestTemplate restTemplate

    <T> ResponseEntity<T> get(String path, Class<T> responseType) {
        restTemplate.getForEntity("$url$path", responseType)
    }

    <T> ResponseEntity<T> post(String path, Object request, Class<T> responseType) {
        restTemplate.postForEntity("$url$path", request, responseType)
    }

    <T> ResponseEntity<T> put(String path, Object request, Class<T> responseType) {
        restTemplate.exchange("$url$path", PUT, new HttpEntity(request), responseType)
    }

    ResponseEntity<Void> delete(String path) {
        restTemplate.exchange("$url$path", HttpMethod.DELETE, HttpEntity.EMPTY, Void)
    }

}
