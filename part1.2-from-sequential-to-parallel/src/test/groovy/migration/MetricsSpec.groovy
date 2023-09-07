package migration

class MetricsSpec extends BaseSpec {

    def "increase counter on appendText"() {
        given:
        def count = MetricService.getCount()

        when:
        FileService.appendText(tempDir, "test", "some text")
        sleep 100

        then:
        MetricService.getCount() == count + 1
    }

    def "increase counter on setText"() {
        given:
        def count = MetricService.getCount()

        when:
        FileService.setText(tempDir, "test", "some text")
        sleep 100

        then:
        MetricService.getCount() == count + 1
    }

}