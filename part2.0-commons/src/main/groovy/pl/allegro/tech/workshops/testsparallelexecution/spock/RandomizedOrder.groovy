package pl.allegro.tech.workshops.testsparallelexecution.spock

import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtensionAnnotation(RandomizedOrderExtension)
@interface RandomizedOrder {
    String seed() default "";
}