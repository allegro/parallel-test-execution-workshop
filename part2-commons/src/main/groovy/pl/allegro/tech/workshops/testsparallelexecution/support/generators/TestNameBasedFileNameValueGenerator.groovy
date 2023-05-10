package pl.allegro.tech.workshops.testsparallelexecution.support.generators

import org.spockframework.lang.ISpecificationContext

class TestNameBasedFileNameValueGenerator implements UniqueValueGenerator {

    private static final String FILE_SYSTEM_ILLEGAL_CHARS_REGEX = "[^\\w.-]"
    private final UniqueValueGenerator testNameUniqueValueGenerator

    TestNameBasedFileNameValueGenerator(ISpecificationContext specificationContext) {
        this.testNameUniqueValueGenerator = new TestNameUniqueValueGenerator(specificationContext)
    }

    @Override
    String next() {
        return testNameUniqueValueGenerator.next().replaceAll(FILE_SYSTEM_ILLEGAL_CHARS_REGEX, "_")
    }

}
