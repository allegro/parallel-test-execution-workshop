package pl.allegro.tech.workshops.testsparallelexecution.support.generators


import spock.lang.Specification

class GeneratorsExampleTest extends Specification {

//    @Shared
//    def generator = new NextIntValueGenerator()
    // or
//    def generator = new RandomUniqueValueGenerator()
    //or
//    def generator = new TestNameUniqueValueGenerator(this.specificationContext)

    private String id = "2"

    def setup() {
//        id = generator.next()
    }

    def "test case a"() {
        println "id = $id"
        expect:
        true
    }

    def "test case b"() {
        println "id = $id"
        expect:
        true
    }

    def "test case #name"() {
        println "id = $id"
        expect:
        true

        where:
        name | _
        "c"  | _
        "d"  | _
    }

}
