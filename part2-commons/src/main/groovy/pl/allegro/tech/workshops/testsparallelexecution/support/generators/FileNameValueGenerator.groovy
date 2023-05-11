package pl.allegro.tech.workshops.testsparallelexecution.support.generators

class FileNameValueGenerator implements UniqueValueGenerator {

    private static final String FILE_SYSTEM_ILLEGAL_CHARS_REGEX = "[^\\w.-]"
    private final UniqueValueGenerator uniqueValueGenerator

    FileNameValueGenerator(UniqueValueGenerator uniqueValueGenerator) {
        this.uniqueValueGenerator = uniqueValueGenerator
    }

    @Override
    String next() {
        return uniqueValueGenerator.next().replaceAll(FILE_SYSTEM_ILLEGAL_CHARS_REGEX, "_")
    }

}
