package migration

import java.util.concurrent.atomic.AtomicInteger

class MetricService {

    private static AtomicInteger invocationsCount = new AtomicInteger()

    static void incrementInvocationsCount() {
        invocationsCount.incrementAndGet()
    }

    static int getInvocationsCount() {
        invocationsCount.get()
    }

}
