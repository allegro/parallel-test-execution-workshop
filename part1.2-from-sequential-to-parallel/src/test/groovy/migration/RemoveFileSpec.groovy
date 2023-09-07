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

    def "remove file 2"() {
        given:
        def fileName = "test 2"
        def file = new File(rootDir, fileName)
        file.createNewFile()
        def numberOfFilesBeforeRemove = rootDir.list().length

        when:
        FileService.removeFile(rootDir, fileName)


        then:
        def numberOfFilesAfterRemove = rootDir.list().length
        numberOfFilesAfterRemove == numberOfFilesBeforeRemove - 1
    }

}