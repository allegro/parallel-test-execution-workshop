package pl.allegro.tech.workshops.testsparallelexecution.support.generators

class NextIntValueGenerator implements UniqueValueGenerator {

    private int value = 0

    NextIntValueGenerator() {
        this(1)
    }

    NextIntValueGenerator(int initialValue) {
        this.value = initialValue
    }

    @Override
    String next() {
        return value++
    }
}
