package org.cid15.aem.veneer.core.resource.predicates

import org.apache.sling.api.resource.NonExistingResource
import org.cid15.aem.veneer.api.resource.VeneeredResource
import org.cid15.aem.veneer.core.specs.VeneerSpec

class VeneeredResourcePropertyExistsPredicateSpec extends VeneerSpec {

    def setupSpec() {
        nodeBuilder.content {
            cid15("jcr:title": "CID15")
        }
    }

    def "resource where property exists is included"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15")
        def predicate = new VeneeredResourcePropertyExistsPredicate("jcr:title")

        expect:
        predicate.test(veneeredResource)
    }

    def "resource where property does not exist is not included"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15")
        def predicate = new VeneeredResourcePropertyExistsPredicate("jcr:description")

        expect:
        !predicate.test(veneeredResource)
    }

    def "resource for non-existing resource is not included"() {
        setup:
        def resource = new NonExistingResource(resourceResolver, "/content/non-existing")
        def veneeredResource = resource.adaptTo(VeneeredResource)
        def predicate = new VeneeredResourcePropertyExistsPredicate("propertyName")

        expect:
        !predicate.test(veneeredResource)
    }
}
