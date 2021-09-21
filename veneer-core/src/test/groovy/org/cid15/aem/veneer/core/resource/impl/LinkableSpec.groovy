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

    def "get href mapped"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getLinkBuilder().mapped(resourceResolver).build().getHref() == "/cid15/_jcr_content.html"
    }

    def "get link"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.link.path == "/content/cid15/jcr:content"
    }

    def "get link mapped"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.getLinkBuilder().mapped(resourceResolver).build().path == "/content/cid15/jcr:content"
    }

    def "get link builder"() {
        setup:
        def veneeredResource = getVeneeredResource("/content/cid15/jcr:content")

        expect:
        veneeredResource.linkBuilder.build().path == "/content/cid15/jcr:content"
    }
}