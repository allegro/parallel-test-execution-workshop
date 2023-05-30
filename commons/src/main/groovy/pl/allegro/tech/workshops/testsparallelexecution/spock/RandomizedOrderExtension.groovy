package pl.allegro.tech.workshops.testsparallelexecution.spock

import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.model.SpecInfo

final class RandomizedOrderExtension implements IAnnotationDrivenExtension<RandomizedOrder> {

    private static final long ANNOTATION_DEFAULT_VALUE = 0

    @Override
    void visitSpecAnnotation(RandomizedOrder annotation, SpecInfo spec) {
        final Long seed = annotation.seed() == ANNOTATION_DEFAULT_VALUE ? System.nanoTime() : Long.valueOf(annotation.seed())
        println """Randomized test using seed = $seed. Use @RandomizedOrder(seed = $seed) to run tests with same order."""
        final Random random = new Random(seed)
        final List<Integer> order = (0..(spec.features.size())).toList()
        Collections.shuffle(order, random)
        spec.features.each { feature ->
            feature.executionOrder = order.removeLast()
        }
    }
}