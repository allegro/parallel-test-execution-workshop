package pl.allegro.tech.workshops.testsparallelexecution.support

import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

import static org.springframework.http.HttpMethod.DELETE
import static org.springframework.http.HttpMethod.GET
import static org.springframework.http.HttpMethod.POST
import static org.springframework.http.HttpMethod.PUT

class RestClient {
    String url
    TestRestTemplate restTemplate

    <T> ResponseEntity<T> get(String path, Class<T> responseType, Map<String, String> headers = [:]) {
        restTemplate.exchange("$url$path", GET, createHttpEntity(headers), responseType)
    }

    <T> ResponseEntity<T> post(String path, Object request, Class<T> responseType, Map<String, String> headers = [:]) {
        restTemplate.exchange("$url$path", POST, createHttpEntity(request, headers), responseType)
    }

    <T> ResponseEntity<T> put(String path, Object request, Class<T> responseType, Map<String, String> headers = [:]) {
        restTemplate.exchange("$url$path", PUT, createHttpEntity(request, headers), responseType)
    }

    ResponseEntity<Void> delete(String path, Map<String, String> headers = [:]) {
        restTemplate.exchange("$url$path", DELETE, createHttpEntity(headers), Void)
    }

    private static HttpEntity createHttpEntity(Object request, Map<String, String> headers) {
        new HttpEntity(request, createHeaders(headers))
    }

    private static HttpEntity createHttpEntity(Map<String, String> headers = [:]) {
        new HttpEntity(createHeaders(headers))
    }

    private static HttpHeaders createHeaders(Map<String, String> headers) {
        new HttpHeaders().tap {
            headers.each { header ->
                set(header.key, header.value)
            }
        }
    }


}
