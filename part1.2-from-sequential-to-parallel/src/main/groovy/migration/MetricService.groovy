package migration

import java.util.concurrent.atomic.AtomicInteger

class MetricService {

    private static AtomicInteger invocations = new AtomicInteger()

    static void increment() {
        invocations.incrementAndGet()
    }

    static int getCount() {
        invocations.get()
    }

}
