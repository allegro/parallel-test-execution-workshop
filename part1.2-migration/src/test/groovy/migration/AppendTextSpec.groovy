package migration

class AppendTextSpec extends BaseSpec {

    def "append text"() {
        given:
        def name = "test"

        when:
        FileService.appendText(tempDir, name, "some text")

        then:
        new File(tempDir, name).text == 'some text'
    }

    def "append empty text"() {
        given:
        def name = "test"

        when:
        FileService.appendText(tempDir, name, '')

        then:
        new File(tempDir, name).text == ''
    }

}