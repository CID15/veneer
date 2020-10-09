package org.cid15.aem.veneer.core.resource.impl

import com.day.cq.dam.api.Asset
import org.apache.sling.api.resource.Resource
import org.cid15.aem.veneer.api.resource.VeneeredResource
import spock.lang.Unroll

import java.util.function.Predicate

@Unroll
class InheritableSpec extends AbstractVeneeredResourceSpec {

    def "get image reference inherited"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.imageReferenceInherited.present == isPresent

        where:
        path                               | isPresent
        "/content/cid15/jcr:content"       | true
        "/content/cid15/about/jcr:content" | true
        "/content/ales/esb/jcr:content"    | false
    }

    def "get self image reference inherited"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getImageReferenceInherited(isSelf).present == isPresent

        where:
        path                                       | isSelf | isPresent
        "/content/cid15/jcr:content"               | false  | true
        "/content/cid15/jcr:content"               | true   | true
        "/content/cid15/about/jcr:content"         | false  | true
        "/content/cid15/about/jcr:content"         | true   | true
        "/content/ales/esb/jcr:content"            | false  | false
        "/content/ales/esb/jcr:content"            | true   | false
        "/content/ales/esb/jcr:content/greeneking" | true   | false
        "/content/ales/esb/jcr:content/greeneking" | false  | true
    }

    def "get as resource inherited"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/ales/esb/lace/jcr:content")

        expect:
        veneeredResource.getAsResourceInherited("otherPagePath").present

        and:
        !veneeredResource.getAsResourceInherited("nonExistentProperty").present
    }

    def "get as resource list inherited"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/ales/esb/lace/jcr:content")

        expect:
        veneeredResource.getAsResourceListInherited("pagePaths").size() == 2

        and:
        veneeredResource.getAsResourceListInherited("nonExistentPagePath").empty

        where:
        path                                 | size
        "/content/ales/esb/lace/jcr:content" | 2
        "/content/ales/esb/jcr:content"      | 2
        "/content/ales/jcr:content"          | 0
    }

    def "get as type inherited"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/lagers/jcr:content/dynamo")

        expect:
        veneeredResource.getAsTypeInherited("related", type).present == result

        where:
        type             | result
        Resource         | true
        VeneeredResource | true
        Asset            | false
    }

    def "get as type list inherited"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getAsTypeListInherited("related", type).size() == size

        where:
        path                                 | type             | size
        "/content/lagers/jcr:content/stiegl" | VeneeredResource | 2
        "/content/lagers/jcr:content/stiegl" | Asset            | 0
    }

    def "get as href inherited"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getAsHrefInherited(propertyName).get() == href

        where:
        path                                 | propertyName    | href
        "/content/ales/esb/jcr:content"      | "otherPagePath" | "/content/cid15.html"
        "/content/ales/esb/suds/jcr:content" | "otherPagePath" | ""
        "/content/ales/esb/lace/jcr:content" | "otherPagePath" | "/content/cid15.html"
        "/content/ales/esb/jcr:content"      | "externalPath"  | "http://www.reddit.com"
        "/content/ales/esb/suds/jcr:content" | "externalPath"  | "http://www.reddit.com"
        "/content/ales/esb/lace/jcr:content" | "externalPath"  | "http://www.reddit.com"
    }

    def "get as href inherited returns absent where appropriate"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        !veneeredResource.getAsHrefInherited(propertyName).present

        where:
        path                                 | propertyName
        "/content/ales/esb/jcr:content"      | "nonExistentPath"
        "/content/ales/esb/suds/jcr:content" | "nonExistentPath"
    }

    def "get as link inherited"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getAsLinkInherited(propertyName).get().href == href

        where:
        path                                 | propertyName    | href
        "/content/ales/esb/jcr:content"      | "otherPagePath" | "/content/cid15.html"
        "/content/ales/esb/suds/jcr:content" | "otherPagePath" | ""
        "/content/ales/esb/lace/jcr:content" | "otherPagePath" | "/content/cid15.html"
        "/content/ales/esb/jcr:content"      | "externalPath"  | "http://www.reddit.com"
        "/content/ales/esb/suds/jcr:content" | "externalPath"  | "http://www.reddit.com"
        "/content/ales/esb/lace/jcr:content" | "externalPath"  | "http://www.reddit.com"
    }

    def "get as link inherited returns absent where appropriate"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        !veneeredResource.getAsLinkInherited("nonExistentPath").present

        where:
        path                                 | propertyName
        "/content/ales/esb/jcr:content"      | "nonExistentPath"
        "/content/ales/esb/suds/jcr:content" | "nonExistentPath"
    }

    def "get as page inherited"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/ales/esb/lace/jcr:content")

        expect:
        veneeredResource.getAsVeneeredPageInherited("otherPagePath").get().path == "/content/cid15"

        and:
        !veneeredResource.getAsVeneeredPageInherited("nonExistentPagePath").present
    }

    def "get as page list inherited"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getAsVeneeredPageListInherited("pagePaths").size() == size

        and:
        veneeredResource.getAsVeneeredPageListInherited("nonExistentPagePath").empty

        where:
        path                                 | size
        "/content/ales/esb/lace/jcr:content" | 2
        "/content/ales/esb/jcr:content"      | 2
        "/content/ales/jcr:content"          | 0
    }

    def "get component resource inherited"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getVeneeredResourceInherited("child1").get().path == inheritedNodePath

        where:
        path                                                       | inheritedNodePath
        "/content/ales/esb/suds/pint/keg/jcr:content/container"    | "/content/ales/esb/suds/jcr:content/container/child1"
        "/content/ales/esb/suds/pint/barrel/jcr:content/container" | "/content/ales/esb/suds/pint/barrel/jcr:content/container/child1"
    }

    def "get component resource inherited is absent when ancestor not found"() {
        expect:
        !getVeneeredResource("/content/ales/esb/jcr:content").getVeneeredResourceInherited("child1").present
    }

    def "get component resources inherited"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.veneeredResourcesInherited.size() == size

        where:
        path                                                       | size
        "/content/ales/esb/suds/pint/jcr:content/container"        | 2
        "/content/ales/esb/suds/pint/keg/jcr:content/container"    | 2
        "/content/ales/esb/suds/pint/barrel/jcr:content/container" | 1
        "/content/ales/esb/bar/tree/jcr:content/wood/container"    | 3
    }

    def "get component resources inherited for predicate"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        def predicate = new Predicate<VeneeredResource>() {
            @Override
            boolean test(VeneeredResource cr) {
                cr.get("jcr:title", "") == "Zeus"
            }
        }

        expect:
        veneeredResource.getVeneeredResourcesInherited(predicate).size() == size

        where:
        path                                                       | size
        "/content/ales/esb/suds/pint/jcr:content/container"        | 1
        "/content/ales/esb/suds/pint/keg/jcr:content/container"    | 1
        "/content/ales/esb/suds/pint/barrel/jcr:content/container" | 0
    }

    def "get component resources inherited for relative path"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getVeneeredResourcesInherited("container").size() == size

        where:
        path                                             | size
        "/content/ales/esb/suds/pint/jcr:content"        | 2
        "/content/ales/esb/suds/pint/keg/jcr:content"    | 0
        "/content/ales/esb/suds/pint/barrel/jcr:content" | 1
        "/content/ales/esb/bar/tree/jcr:content/wood"    | 3
    }

    def "get component resources inherited for relative path and predicate"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        def predicate = new Predicate<VeneeredResource>() {
            @Override
            boolean test(VeneeredResource cr) {
                cr.get("jcr:title", "") == "Zeus"
            }
        }

        expect:
        veneeredResource.getVeneeredResourcesInherited("container", predicate).size() == size

        where:
        path                                             | size
        "/content/ales/esb/suds/pint/jcr:content"        | 1
        "/content/ales/esb/suds/pint/keg/jcr:content"    | 0
        "/content/ales/esb/suds/pint/barrel/jcr:content" | 0
    }

    def "get inherited"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getInherited(propertyName, "") == propertyValue

        where:
        path                                                              | propertyName  | propertyValue
        "/content/ales/esb/suds/pint/barrel/jcr:content/container/child1" | "jcr:title"   | "Zeus"
        "/content/ales/esb/suds/pint/barrel/jcr:content/container/child1" | "nonExistent" | ""
        "/content/ales/esb/jcr:content/fullers"                           | "any"         | ""
    }

    def "get inherited optional"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/ales/esb/lace/jcr:content")

        expect:
        veneeredResource.getInherited("otherPagePath", String).present
        !veneeredResource.getInherited("nonExistentProperty", String).present
    }

    def "get tags inherited"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getTagsInherited("tags").size() == size

        where:
        path                                 | size
        "/content/ales/esb/jcr:content"      | 1
        "/content/ales/esb/suds/jcr:content" | 1
        "/content/cid15/jcr:content/malort"  | 0
    }
}