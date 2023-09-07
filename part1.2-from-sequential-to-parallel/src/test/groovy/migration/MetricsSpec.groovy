package migration

class MetricsSpec extends BaseSpec {

    def "increase counter on appendText"() {
        given:
        def count = MetricService.getInvocationsCount()

        when:
        FileService.appendText(rootDir, "test", "some text")

        then:
        MetricService.getInvocationsCount() == count + 1
    }

    def "increase counter on setText"() {
        given:
        def count = MetricService.getInvocationsCount()

        when:
        FileService.setText(rootDir, "test", "some text")

        then:
        MetricService.getInvocationsCount() == count + 1
    }

}