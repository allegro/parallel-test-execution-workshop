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
        String name = "testName"
        file = new File(tempDir, name)
        println "name = $name"

        when:
        def fileCreated = file.createNewFile()

        then:
        fileCreated
        file.exists()
    }

    def "should create dir"() {
        given:
        String name = "testName"
        file = new File(tempDir, name)
        println "id = $name"

        when:
        def fileCreated = file.mkdir()

        then:
        fileCreated
        file.exists()
    }

    def "should create readable/non-readable file"() {
        given:
        String name = "testName"
        file = new File(tempDir, name)
        file.setReadable(readable)
        println "id = $name"

        when:
        def fileCreated = file.createNewFile()

        then:
        fileCreated
        file.exists()

        where:
        readable | _
        true     | _
        false    | _
    }

}
