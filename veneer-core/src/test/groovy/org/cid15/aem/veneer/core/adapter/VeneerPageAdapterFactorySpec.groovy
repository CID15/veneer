package org.cid15.aem.veneer.core.adapter

import org.cid15.aem.veneer.api.page.VeneeredPage
import org.cid15.aem.veneer.api.page.VeneeredPageManager
import org.cid15.aem.veneer.core.specs.VeneerSpec
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
class VeneerPageAdapterFactorySpec extends VeneerSpec {

    @Shared
    VeneerPageAdapterFactory adapterFactory = new VeneerPageAdapterFactory()

    def setupSpec() {
        pageBuilder.content {
            home()
        }
    }

    def "get veneered page"() {
        expect:
        adapterFactory.getAdapter(adaptable, VeneeredPage)

        where:
        adaptable << [resourceResolver.getResource("/content/home"), getPage("/content/home")]
    }

    def "get adapter for resource returns null for non-page node"() {
        setup:
        def resource = resourceResolver.getResource("/")

        expect:
        !adapterFactory.getAdapter(resource, VeneeredPage)
    }

    def "get adapter for resource with invalid type returns null"() {
        setup:
        def resource = resourceResolver.getResource("/")

        expect:
        !adapterFactory.getAdapter(resource, String)
    }

    def "get adapter for resource resolver with valid type returns non-null"() {
        expect:
        adapterFactory.getAdapter(resourceResolver, VeneeredPageManager)
    }

    def "get adapter for resource resolver with invalid type returns null"() {
        expect:
        !adapterFactory.getAdapter(resourceResolver, String)
    }

    def "get invalid adapter returns null"() {
        expect:
        !adapterFactory.getAdapter("", String)
    }
}
