package pl.allegro.tech.workshops.testsparallelexecution.support.generators

import java.util.concurrent.atomic.AtomicInteger

class NextIntValueGenerator implements UniqueValueGenerator {

    private AtomicInteger value

    NextIntValueGenerator() {
        this(1)
    }

    NextIntValueGenerator(int initialValue) {
        this.value = new AtomicInteger(initialValue)
    }

    @Override
    String next() {
        return value.getAndIncrement()
    }
}
