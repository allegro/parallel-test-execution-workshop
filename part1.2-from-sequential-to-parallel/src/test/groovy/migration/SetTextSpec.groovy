package migration

class SetTextSpec extends BaseSpec {

    def "create file with text"() {
        given:
        def name = "test"

        when:
        FileService.setText(tempDir, name, "some text")
        sleep 200

        then:
        new File(tempDir, name).text == 'some text'
    }

    def "create empty file"() {
        given:
        def name = "test"

        when:
        FileService.setText(tempDir, name, "")
        sleep 200

        then:
        new File(tempDir, name).text == ''
    }

}