import spock.lang.Specification

class B extends Specification {

    private static final int SLEEP_DURATION = 1

    def "test 1"() {
        sleep SLEEP_DURATION

        expect:
        true
    }

    def "test 2"() {
        sleep SLEEP_DURATION

        expect:
        true
    }

}