package pl.allegro.tech.workshops.testsparallelexecution.support.generators

import org.spockframework.lang.ISpecificationContext

class TestNameUniqueValueGenerator implements UniqueValueGenerator {

    private final ISpecificationContext specificationContext

    TestNameUniqueValueGenerator(ISpecificationContext specificationContext) {
        this.specificationContext = specificationContext
    }

    @Override
    String next() {
        return "${specificationContext.currentSpec.package}:${specificationContext.currentSpec.name}:$specificationContext.currentIteration.displayName"
    }

}
