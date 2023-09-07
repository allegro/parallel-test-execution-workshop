package migration


import spock.lang.Specification

import java.nio.file.Files

class BaseSpec extends Specification {

    static File tempDir = Files.createTempDirectory("parallel-test-execution").toFile()

    def cleanup() {
        tempDir.listFiles().each {
            println "Removing $it"
            it.delete()
        }
    }

}