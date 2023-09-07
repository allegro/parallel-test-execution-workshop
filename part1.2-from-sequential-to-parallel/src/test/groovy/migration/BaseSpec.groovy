package migration


import spock.lang.Specification

import java.nio.file.Files

class BaseSpec extends Specification {

    static File rootDir = Files.createTempDirectory("parallel-test-execution").toFile()

}