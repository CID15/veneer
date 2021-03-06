package org.cid15.aem.veneer.core.resource.impl

import io.wcm.testing.mock.aem.junit.AemContext
import io.wcm.testing.mock.aem.junit.AemContextBuilder
import org.apache.sling.testing.mock.sling.ResourceResolverType
import spock.lang.Unroll

@Unroll
class LinkableSpec extends AbstractVeneeredResourceSpec {

    @Override
    AemContext getAemContext() {
        new AemContextBuilder(ResourceResolverType.JCR_OAK)
            .resourceResolverFactoryActivatorProps(["resource.resolver.mapping": ["/content/:/", "/-/"] as String[]])
            .build()
    }

    def "get href"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.href == "/content/cid15/jcr:content.html"
    }

    def "get mapped href"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getHref(mapped) == href

        where:
        mapped | href
        true   | "/cid15/_jcr_content.html"
        false  | "/content/cid15/jcr:content.html"
    }

    def "get link"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.link.path == "/content/cid15/jcr:content"
    }

    def "get mapped link"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getLink(mapped).path == path

        where:
        mapped | path
        true   | "/content/cid15/jcr:content"
        false  | "/content/cid15/jcr:content"
    }

    def "get link builder"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.linkBuilder.build().path == "/content/cid15/jcr:content"
    }
}