package org.cid15.aem.veneer.core.resource.impl

import groovy.transform.TupleConstructor
import org.cid15.aem.veneer.api.resource.VeneeredResource
import org.cid15.aem.veneer.core.resource.predicates.VeneeredResourcePropertyExistsPredicate
import spock.lang.Unroll

import java.util.function.Predicate

@Unroll
class TraversableSpec extends AbstractVeneeredResourceSpec {

    @TupleConstructor
    static class TestPredicate implements Predicate<VeneeredResource> {

        String propertyValue

        @Override
        boolean test(VeneeredResource veneeredResource) {
            veneeredResource.get("jcr:title", "") == propertyValue
        }
    }

    def "find ancestor for predicate"() {
        setup:
        def veneeredResource = getVeneeredResource(path)
        def predicate = new TestPredicate(propertyValue)
        def ancestorNodeOptional = veneeredResource.findAncestor(predicate, excludeCurrentResource)

        expect:
        ancestorNodeOptional.present == isPresent

        where:
        path                                               | propertyName | propertyValue | excludeCurrentResource | isPresent
        "/content/inheritance/jcr:content/component"       | "jcr:title"  | "Component"   | true                   | false
        "/content/inheritance/jcr:content/component"       | "jcr:title"  | "Component"   | false                  | true
        "/content/inheritance/child/jcr:content/component" | "jcr:title"  | "Component"   | true                   | true
        "/content/inheritance/child/jcr:content/component" | "jcr:title"  | "Component"   | false                  | true
    }

    def "find ancestor with property"() {
        setup:
        def veneeredResource = getVeneeredResource(path)
        def ancestorNodeOptional = veneeredResource.findAncestorWithProperty("jcr:title", excludeCurrentResource)

        expect:
        ancestorNodeOptional.get().path == ancestorPath

        where:
        path                                               | excludeCurrentResource | ancestorPath
        "/content/inheritance/jcr:content"                 | false                  | "/content/inheritance/jcr:content"
        "/content/inheritance/child/jcr:content"           | false                  | "/content/inheritance/jcr:content"
        "/content/inheritance/child/jcr:content/component" | false                  | "/content/inheritance/jcr:content/component"
        "/content/inheritance/child/jcr:content"           | true                   | "/content/inheritance/jcr:content"
        "/content/inheritance/child/jcr:content/component" | true                   | "/content/inheritance/jcr:content/component"
    }

    def "find ancestor with property returns absent when current resource excluded"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/inheritance/jcr:content")
        def ancestorNodeOptional = veneeredResource.findAncestorWithProperty("jcr:title", true)

        expect:
        !ancestorNodeOptional.present
    }

    def "find ancestor returns absent"() {
        setup:
        def veneeredResource = getVeneeredResource(path)
        def ancestorNodeOptional = veneeredResource.findAncestorWithProperty("jcr:description")

        expect:
        !ancestorNodeOptional.present

        where:
        path << [
            "/content/inheritance/child/jcr:content",
            "/content/inheritance/child/jcr:content/component"
        ]
    }

    def "find ancestor with property value"() {
        setup:
        def veneeredResource = getVeneeredResource(path)
        def ancestorNodeOptional = veneeredResource.findAncestorWithPropertyValue(propertyName, propertyValue)

        expect:
        ancestorNodeOptional.present == isPresent

        where:
        path                                               | propertyName | propertyValue   | isPresent
        "/content/inheritance/jcr:content/component"       | "jcr:title"  | "Component"     | true
        "/content/inheritance/jcr:content/component"       | "jcr:title"  | "Komponent"     | false
        "/content/inheritance/jcr:content/component"       | "number"     | Long.valueOf(5) | true
        "/content/inheritance/jcr:content/component"       | "boolean"    | false           | true
        "/content/inheritance/child/jcr:content/component" | "jcr:title"  | "Component"     | true
        "/content/inheritance/child/jcr:content/component" | "jcr:title"  | "Komponent"     | false
        "/content/inheritance/child/jcr:content/component" | "number"     | Long.valueOf(5) | true
        "/content/inheritance/child/jcr:content/component" | "boolean"    | false           | true
    }

    def "find ancestor with property value excluding current resource"() {
        setup:
        def veneeredResource = getVeneeredResource(path)
        def ancestorNodeOptional = veneeredResource.findAncestorWithPropertyValue(propertyName, propertyValue, excludeCurrentResource)

        expect:
        ancestorNodeOptional.present == isPresent

        where:
        path                                               | propertyName | propertyValue   | excludeCurrentResource | isPresent
        "/content/inheritance/jcr:content/component"       | "jcr:title"  | "Component"     | true                   | false
        "/content/inheritance/jcr:content/component"       | "number"     | Long.valueOf(5) | true                   | false
        "/content/inheritance/jcr:content/component"       | "boolean"    | false           | true                   | false
        "/content/inheritance/jcr:content/component"       | "jcr:title"  | "Component"     | false                  | true
        "/content/inheritance/jcr:content/component"       | "number"     | Long.valueOf(5) | false                  | true
        "/content/inheritance/jcr:content/component"       | "boolean"    | false           | false                  | true
        "/content/inheritance/child/jcr:content/component" | "jcr:title"  | "Component"     | true                   | true
        "/content/inheritance/child/jcr:content/component" | "number"     | Long.valueOf(5) | true                   | true
        "/content/inheritance/child/jcr:content/component" | "boolean"    | false           | true                   | true
        "/content/inheritance/child/jcr:content/component" | "jcr:title"  | "Component"     | false                  | true
        "/content/inheritance/child/jcr:content/component" | "number"     | Long.valueOf(5) | false                  | true
        "/content/inheritance/child/jcr:content/component" | "boolean"    | false           | false                  | true
    }

    def "find ancestor with property value returns absent"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/inheritance/child/jcr:content/component")
        def ancestorNodeOptional = veneeredResource.findAncestorWithPropertyValue("jcr:title", "Komponent")

        expect:
        !ancestorNodeOptional.present
    }

    def "find descendants"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")
        def predicate = new VeneeredResourcePropertyExistsPredicate("sling:resourceType")

        expect:
        veneeredResource.findDescendants(predicate).size() == 3
    }
}