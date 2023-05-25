package pl.allegro.tech.workshops.testsparallelexecution

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

class SharedStateExampleTest extends Specification {

    @Shared
    @TempDir
    File tempDir

    private File file
    private String name

    def setup() {
    }

    def cleanup() {
        file?.delete()
    }

    def "should create file"() {
        given:
        name = "testName"
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
        name = "testName"
        file = new File(tempDir, name)
        println "name = $name"

        when:
        def fileCreated = file.mkdir()

        then:
        fileCreated
        file.exists()
    }

    def "should create readable/non-readable file"() {
        given:
        name = "testName"
        file = new File(tempDir, name)
        file.setReadable(readable)
        println "name = $name"

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
