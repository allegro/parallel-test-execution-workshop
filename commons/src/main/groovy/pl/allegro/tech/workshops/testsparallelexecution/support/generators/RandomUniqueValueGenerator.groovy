package pl.allegro.tech.workshops.testsparallelexecution.support.generators

import org.apache.commons.lang3.RandomStringUtils

@Singleton
class RandomUniqueValueGenerator implements UniqueValueGenerator {

    @Override
    String next() {
        return RandomStringUtils.randomAlphanumeric(5)
    }
}
