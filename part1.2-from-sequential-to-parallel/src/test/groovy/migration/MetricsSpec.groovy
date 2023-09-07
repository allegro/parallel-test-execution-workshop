package migration

class MetricsSpec extends BaseSpec {

    def "increase counter on appendText"() {
        given:
        def count = MetricService.getInvocationsCount()

        when:
        FileService.appendText(tempDir, "test", "some text")
        sleep 100

        then:
        MetricService.getInvocationsCount() == count + 1
    }

    def "increase counter on setText"() {
        given:
        def count = MetricService.getInvocationsCount()

        when:
        FileService.setText(tempDir, "test", "some text")
        sleep 100

        then:
        MetricService.getInvocationsCount() == count + 1
    }

}