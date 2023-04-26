package pl.allegro.tech.workshops.testsparallelexecution.support.generators

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

class SharedStateExampleTest extends Specification {

    @Shared
    @TempDir
    File tempDir

    private String id = "2"

    def setup() {
    }

    def cleanup() {
        new File(tempDir, id).delete()
    }

    def "test case a"() {
        given:
        println "id = $id"

        expect:
        new File(tempDir, id).createNewFile()
    }

    def "test case b"() {
        given:
        println "id = $id"

        expect:
        new File(tempDir, id).createNewFile()
    }

    def "test case #name"() {
        given:
        println "id = $id"

        expect:
        new File(tempDir, id).createNewFile()

        where:
        name | _
        "c"  | _
        "d"  | _
    }

}
