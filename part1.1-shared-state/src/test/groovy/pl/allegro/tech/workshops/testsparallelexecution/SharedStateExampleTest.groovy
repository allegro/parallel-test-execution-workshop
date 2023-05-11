package pl.allegro.tech.workshops.testsparallelexecution

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

class SharedStateExampleTest extends Specification {

    @Shared
    @TempDir
    File tempDir

    private File file

    def setup() {
    }

    def cleanup() {
        file.delete()
    }

    def "should create file"() {
        given:
        String id = "2"
        file = new File(tempDir, id)
        println "id = $id"
        assert !file.exists()

        when:
        def result = file.createNewFile()

        then:
        result
        file.exists()
    }

    def "should create dir"() {
        given:
        String id = "2"
        file = new File(tempDir, id)
        println "id = $id"
        assert !file.exists()

        when:
        def result = file.mkdir()

        then:
        result
        file.exists()
    }

    def "should create readable/non-readable file"() {
        given:
        String id = "2"
        file = new File(tempDir, id)
        file.setReadable(readable)
        println "id = $id"
        assert !file.exists()

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
