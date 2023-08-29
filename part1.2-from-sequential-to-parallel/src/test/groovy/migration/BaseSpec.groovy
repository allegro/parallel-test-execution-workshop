package migration

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

class BaseSpec extends Specification {

    protected static final int SLEEP_DURATION = 100

    @Shared
    @TempDir
    File tempDir

    def setup() {
        sleep SLEEP_DURATION
    }

    def cleanup() {
        tempDir.listFiles().each { it.delete() }
    }

}