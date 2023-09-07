package migration

class AppendTextSpec extends BaseSpec {

    def fileName = "test"

    def setup() {
        new File(rootDir, fileName).text = "initial text"
    }

    def "append text"() {
        when:
        FileService.appendText(rootDir, fileName, "additional text")

        then:
        new File(rootDir, fileName).text == 'initial textadditional text'
    }

    def "append empty text"() {
        when:
        FileService.appendText(rootDir, fileName, '')

        then:
        new File(rootDir, fileName).text == 'initial text'
    }

}