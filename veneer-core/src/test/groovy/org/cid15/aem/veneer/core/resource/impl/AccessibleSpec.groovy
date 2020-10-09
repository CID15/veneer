package org.cid15.aem.veneer.core.resource.impl

import com.day.cq.dam.api.Asset
import io.wcm.testing.mock.aem.junit.AemContext
import io.wcm.testing.mock.aem.junit.AemContextBuilder
import org.apache.sling.api.resource.Resource
import org.apache.sling.testing.mock.sling.ResourceResolverType
import org.cid15.aem.veneer.api.resource.VeneeredResource
import spock.lang.Unroll

@Unroll
class AccessibleSpec extends AbstractVeneeredResourceSpec {

    @Override
    AemContext getAemContext() {
        new AemContextBuilder(ResourceResolverType.JCR_OAK)
            .resourceResolverFactoryActivatorProps(["resource.resolver.mapping": ["/content/:/", "/-/"] as String[]])
            .build()
    }

    def "get properties"() {
        setup:
        def map = getVeneeredResource("/content/cid15/jcr:content").properties

        expect:
        map["jcr:title"] == "CID15"
        map["otherPagePath"] == "/content/ales/esb"
    }

    def "get"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.get(propertyName, defaultValue) == result

        where:
        propertyName          | defaultValue | result
        "otherPagePath"       | ""           | "/content/ales/esb"
        "nonExistentProperty" | ""           | ""
    }

    def "get optional"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.get(propertyName, type).present == result

        where:
        propertyName          | type    | result
        "otherPagePath"       | String  | true
        "otherPagePath"       | Integer | false
        "nonExistentProperty" | String  | false
    }

    def "get as list"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getAsList("multiValue", String) == ["one", "two"]
    }

    def "get as href"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getAsHref(propertyName).get() == href

        where:
        propertyName    | href
        "otherPagePath" | "/content/ales/esb.html"
        "externalPath"  | "http://www.reddit.com"
    }

    def "get as href strict"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getAsHref(propertyName, true).get() == href

        where:
        propertyName          | href
        "otherPagePath"       | "/content/ales/esb.html"
        "nonExistentPagePath" | "/content/home"
        "externalPath"        | "http://www.reddit.com"
    }

    def "get as href returns absent where appropriate"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        !veneeredResource.getAsHref(propertyName).present

        where:
        propertyName << ["beer", ""]
    }

    def "get as mapped href"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getAsHref("otherPagePath", false, true).get() == "/ales/esb.html"
    }

    def "get as mapped href strict"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getAsHref(propertyName, true, true).get() == href

        where:
        propertyName          | href
        "otherPagePath"       | "/ales/esb.html"
        "nonExistentPagePath" | "/home"
        "externalPath"        | "http://www.reddit.com"
    }

    def "get as href for null"() {
        when:
        getVeneeredResource("/content/cid15/jcr:content").getAsHref(null)

        then:
        thrown(NullPointerException)
    }

    def "get as link"() {
        setup:
        def link = getVeneeredResource("/content/cid15/jcr:content").getAsLink("otherPagePath").get()

        expect:
        link.path == "/content/ales/esb"
    }

    def "get as link strict"() {
        setup:
        def link = getVeneeredResource("/content/cid15/jcr:content").getAsLink("nonExistentPagePath", true).get()

        expect:
        link.path == "/content/home"
        link.external
        !link.extension.present
    }

    def "get as mapped link"() {
        setup:
        def link = getVeneeredResource("/content/cid15/jcr:content").getAsLink("otherPagePath", false, true).get()

        expect:
        link.path == "/content/ales/esb"
        link.href == "/ales/esb.html"
    }

    def "get as mapped link strict"() {
        setup:
        def link = getVeneeredResource("/content/cid15/jcr:content").getAsLink("nonExistentPagePath", true,
            true).get()

        expect:
        link.path == "/home"
        link.external
        !link.extension.present
    }

    def "get as link for null"() {
        when:
        getVeneeredResource("/content/cid15/jcr:content").getAsLink(null)

        then:
        thrown(NullPointerException)
    }

    def "get as link for non-existent property"() {
        setup:
        def linkOptional = getVeneeredResource("/content/cid15/jcr:content").getAsLink("beer")

        expect:
        !linkOptional.present
    }

    def "get as page"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getAsVeneeredPage("otherPagePath").get().path == "/content/ales/esb"
        !veneeredResource.getAsVeneeredPage("nonExistentProperty").present
    }

    def "get as page list"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/ales/esb/jcr:content")

        expect:
        veneeredResource.getAsVeneeredPageList(propertyName).size() == size

        where:
        propertyName          | size
        "pagePaths"           | 2
        "nonExistentProperty" | 0
    }

    def "get as resource"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getAsResource("related").present == result

        where:
        path                                 | result
        "/content/lagers/jcr:content/dynamo" | true
        "/content/lagers/jcr:content"        | false
    }

    def "get as resource list"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getAsResourceList("related").size() == size

        where:
        path                                 | size
        "/content/lagers/jcr:content/stiegl" | 2
        "/content/lagers/jcr:content/spaten" | 0
    }

    def "get as type"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/lagers/jcr:content/dynamo")

        expect:
        veneeredResource.getAsType("related", type).present == result

        where:
        type             | result
        Resource         | true
        VeneeredResource | true
        Asset            | false
    }

    def "get as type list"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getAsTypeList("related", type).size() == size

        where:
        path                                 | type             | size
        "/content/lagers/jcr:content/stiegl" | VeneeredResource | 2
        "/content/lagers/jcr:content/stiegl" | Asset            | 0
        "/content/lagers/jcr:content/spaten" | VeneeredResource | 0
        "/content/lagers/jcr:content/spaten" | Asset            | 0
    }

    def "get image reference"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.imageReference.present == isPresent

        where:
        path                            | isPresent
        "/content/cid15/jcr:content"    | true
        "/content/ales/esb/jcr:content" | false
    }

    def "get self image reference"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getImageReference(isSelf).present == isPresent

        where:
        path                                       | isSelf | isPresent
        "/content/cid15/jcr:content"               | false  | true
        "/content/cid15/jcr:content"               | true   | true
        "/content/ales/esb/jcr:content"            | false  | false
        "/content/ales/esb/jcr:content"            | true   | false
        "/content/ales/esb/jcr:content/greeneking" | true   | false
        "/content/ales/esb/jcr:content/greeneking" | false  | true
    }

    def "get named image reference"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getImageReference("nsfwImage").get() == "omg.png"

        and:
        !veneeredResource.getImageReference("sfwImage").present
    }

    def "has image"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.hasImage == hasImage

        where:
        path                                       | hasImage
        "/content/cid15/jcr:content"               | true
        "/content/ales/esb/jcr:content"            | false
        "/content/ales/esb/jcr:content/greeneking" | true
    }

    def "has named image"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.isHasImage(name) == hasImage

        where:
        path                            | name          | hasImage
        "/content/cid15/jcr:content"    | "image"       | true
        "/content/cid15/jcr:content"    | "secondimage" | true
        "/content/cid15/jcr:content"    | "thirdimage"  | false
        "/content/cid15/jcr:content"    | "fourthimage" | false
        "/content/ales/esb/jcr:content" | "image"       | false
    }

    def "get image rendition returns absent"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        !veneeredResource.getImageRendition("sfwImage", "").present
    }

    def "get image rendition"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        !veneeredResource.getImageRendition("").present
    }

    def "get named image rendition"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getImageRendition(name, renditionName).present == result

        where:
        name                  | renditionName | result
        "secondimage"         | ""            | false
        "imageWithRenditions" | "one"         | true
        "imageWithRenditions" | "four"        | false
    }

    def "get tags"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.tags.size() == size

        where:
        path                                | size
        "/content/cid15/jcr:content"        | 1
        "/content/cid15/jcr:content/malort" | 0
    }

    def "get tags for property name"() {
        setup:
        def veneeredResource = getVeneeredResource(path)

        expect:
        veneeredResource.getTags("tags").size() == size

        where:
        path                                | size
        "/content/cid15/jcr:content"        | 2
        "/content/cid15/jcr:content/malort" | 0
    }
}