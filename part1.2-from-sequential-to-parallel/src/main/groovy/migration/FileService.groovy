package migration

class FileService {

    static void setText(File parent, String name, String text) {
        MetricService.increment()
        new File(parent, name).text = text
    }

    static void appendText(File parent, String name, String text) {
        MetricService.increment()
        new File(parent, name).append(text)
    }

}
