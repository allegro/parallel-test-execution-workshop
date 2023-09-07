package pl.allegro.tech.workshops.testsparallelexecution.support.generators

import java.util.concurrent.atomic.AtomicInteger

@Singleton
class NextIntValueGenerator implements UniqueValueGenerator {

    private final AtomicInteger value = new AtomicInteger(1)

    @Override
    String next() {
        return value.getAndIncrement()
    }
}
