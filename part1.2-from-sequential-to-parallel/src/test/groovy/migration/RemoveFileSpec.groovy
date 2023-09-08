package migration

class RemoveFileSpec extends BaseSpec {

    def setup() {
        rootDir.listFiles().each {
            it.delete()
        }
    }

    def "remove file"() {
        given:
        def fileName = "test 1"
        def file = new File(rootDir, fileName)
        file.createNewFile()

        when:
        FileService.removeFile(rootDir, fileName)

        then:
        rootDir.list().length == 0
    }

    def "remove empty directory"() {
        given:
        def dirName = "test 2"
        def file = new File(rootDir, dirName)
        file.mkdir()
        def filesBeforeRemove = rootDir.list()

        when:
        FileService.removeFile(rootDir, dirName)


        then:
        def filesAfterRemove = rootDir.list()
        filesAfterRemove.length == filesBeforeRemove.length - 1
    }

}