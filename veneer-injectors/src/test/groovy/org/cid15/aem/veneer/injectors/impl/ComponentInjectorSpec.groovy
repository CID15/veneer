package org.cid15.aem.veneer.injectors.impl

import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import org.cid15.aem.veneer.api.page.VeneeredPage
import org.cid15.aem.veneer.api.resource.VeneeredResource
import org.cid15.aem.veneer.core.specs.VeneerSpec
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
class ComponentInjectorSpec extends VeneerSpec {

    @Shared
    ComponentInjector injector = new ComponentInjector()

    def setupSpec() {
        pageBuilder.content {
            cid15 {
                "jcr:content" {
                    component("jcr:title": "Testing Component")
                }
                page1 {
                    "jcr:content" {
                        component()
                    }
                }
            }
        }
    }

    def "get value from resource for valid type returns non-null value"() {
        setup:
        def resource = getResource("/content/cid15/jcr:content/component")
        def value = injector.getValue(resource, null, type, null, null)

        expect:
        value

        where:
        type << [ResourceResolver, ValueMap, VeneeredResource, VeneeredPage]
    }

    def "get value from resource for invalid type returns null value"() {
        setup:
        def resource = getResource("/content/cid15/jcr:content/component")
        def value = injector.getValue(resource, null, String, null, null)

        expect:
        !value
    }
}
