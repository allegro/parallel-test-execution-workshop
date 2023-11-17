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
        def fileWasCreated = file.createNewFile()

        then:
        fileWasCreated
        file.exists()
    }

    def "should create dir"() {
        given:
        name = "testName"
        file = new File(tempDir, name)
        println "name = $name"

        when:
        def directoryWasCreated = file.mkdir()

        then:
        directoryWasCreated
        file.exists()
    }

    def "should remove file"() {
        given:
        name = "testName"
        file = new File(tempDir, name)
        assert file.createNewFile()
        println "name = $name"

        when:
        def fileWasDeleted = file.delete()

        then:
        fileWasDeleted
        !file.exists()
    }

}
