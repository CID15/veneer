package org.cid15.aem.veneer.core.resource.impl

import org.cid15.aem.veneer.api.resource.VeneeredResource
import spock.lang.Unroll

import java.util.function.Predicate

@Unroll
class DefaultVeneeredResourceSpec extends AbstractVeneeredResourceSpec {

    def "equals"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")
        def other = getVeneeredResource(otherPath)

        expect:
        (veneeredResource == other) == equals

        where:
        otherPath                         | equals
        "/content/cid15/jcr:content"      | true
        "/content/cid15/jcr:content/beer" | false
    }

    def "hash code"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")
        def other = getVeneeredResource(otherPath)

        expect:
        (veneeredResource.hashCode() == other.hashCode()) == equals

        where:
        otherPath                         | equals
        "/content/cid15/jcr:content"      | true
        "/content/cid15/jcr:content/beer" | false
    }

    def "to string"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/lagers/jcr:content/spaten")

        expect:
        veneeredResource.toString() == "DefaultVeneeredResource{path=/content/lagers/jcr:content/spaten, properties={sling:resourceType=de, jcr:primaryType=nt:unstructured}}"
    }

    def "get id"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.id == id

        where:
        path                                    | id
        "/content/cid15"                        | "content-cid15"
        "/content/cid15/jcr:content"            | "content-cid15"
        "/content/cid15/jcr:content/malort/one" | "malort-one"
        "/"                                     | ""
    }

    def "get index"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/ales/esb/jcr:content/morland")

        expect:
        veneeredResource.index == 2
    }

    def "get index for resource type"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/lagers/jcr:content/stiegl")

        expect:
        veneeredResource.getIndex("de") == 0
    }

    def "get underlying resource properties"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.path == path
        veneeredResource.name == name
        veneeredResource.resourceType == resourceType

        where:
        path                                 | name          | resourceType
        "/content/cid15/jcr:content"         | "jcr:content" | "cq:PageContent"
        "/content/cid15/jcr:content/whiskey" | "whiskey"     | "rye"
    }

    def "get resource"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.resource.path == "/content/cid15/jcr:content"
    }

    def "get component resource at relative path"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getVeneeredResource("whiskey").present
        !veneeredResource.getVeneeredResource("vodka").present
    }

    def "get component resources"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.veneeredResources.size() == size

        where:
        path                              | size
        "/content/cid15/jcr:content"      | 8
        "/content/cid15/jcr:content/beer" | 0
    }

    def "get component resources for predicate"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content/malort")
        def predicate = new Predicate<VeneeredResource>() {
            @Override
            boolean test(VeneeredResource input) {
                input.resource.resourceType == "tew"
            }
        }

        expect:
        veneeredResource.getVeneeredResources(predicate).size() == 1
    }

    def "get component resources at relative path"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getVeneeredResources(relativePath).size() == size

        where:
        relativePath | size
        "whiskey"    | 0
        "malort"     | 2
    }

    def "get component resources at relative path for resource type"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getVeneeredResources("malort", resourceType).size() == size

        where:
        resourceType   | size
        "non-existent" | 0
        "tew"          | 1
    }

    def "get component resources at relative path for predicate"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/ales/esb/lace/jcr:content")
        def predicate = new Predicate<VeneeredResource>() {
            @Override
            boolean test(VeneeredResource input) {
                input.resource.resourceType == "unknown"
            }
        }

        expect:
        veneeredResource.getVeneeredResources("parent", predicate).size() == 2
    }

    def "get parent"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.parent.path == parentPath

        where:
        path                         | parentPath
        "/content/cid15/jcr:content" | "/content/cid15"
        "/content/cid15"             | "/content"
    }

    def "get parent returns null for root resource"() {
        setup:
        def veneeredResource = getVeneeredResource("/")

        expect:
        !veneeredResource.parent
    }
}