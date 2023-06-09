package migration

class SetTextSpec extends BaseSpec {

    def "create file with text"() {
        given:
        def name = "test"

        when:
        FileService.setText(tempDir, name, "some text")

        then:
        new File(tempDir, name).text == 'some text'
    }

    def "create empty file"() {
        given:
        def name = "test"

        when:
        FileService.setText(tempDir, name, "")

        then:
        new File(tempDir, name).text == ''
    }

}