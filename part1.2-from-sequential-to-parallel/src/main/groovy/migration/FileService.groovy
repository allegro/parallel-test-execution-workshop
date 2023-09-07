package migration

class FileService {

    static void setText(File parent, String name, String text) {
        MetricService.incrementInvocationsCount()
        new File(parent, name).text = text
        sleep text.length() * 5
    }

    static void appendText(File parent, String name, String text) {
        MetricService.incrementInvocationsCount()
        new File(parent, name).append(text)
        sleep text.length() * 5
    }

}
