package pl.allegro.tech.workshops.testsparallelexecution

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

class SharedStateExampleTest extends Specification {

    @Shared
    @TempDir
    File tempDir

    private File file
    private String id = "2"

    def setup() {
    }

    def cleanup() {
        file.delete()
    }

    def "should create file"() {
        given:
        file = new File(tempDir, id)
        println "id = $id"

        when:
        def result = file.createNewFile()

        then:
        result
        file.exists()
    }

    def "should create dir"() {
        given:
        file = new File(tempDir, id)
        println "id = $id"

        when:
        def result = file.mkdir()

        then:
        result
        file.exists()
    }

    def "should create readable/non-readable file"() {
        given:
        file = new File(tempDir, id)
        file.setReadable(readable)
        println "id = $id"

        when:
        def result = file.createNewFile()

        then:
        result
        file.exists()

        where:
        readable | _
        true     | _
        false    | _
    }

}
