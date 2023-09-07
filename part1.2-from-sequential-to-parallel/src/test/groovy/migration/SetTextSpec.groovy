package migration

class SetTextSpec extends BaseSpec {

    def cleanup() {
        rootDir.listFiles().each {
            it.delete()
        }
    }

    def "create file with text"() {
        given:
        def fileName = "test 1"

        when:
        FileService.setText(rootDir, fileName, "some text")

        then:
        new File(rootDir, fileName).text == 'some text'
    }

    def "create empty file"() {
        given:
        def fileName = "test 2"

        when:
        FileService.setText(rootDir, fileName, "")

        then:
        new File(rootDir, fileName).text == ''
    }

}