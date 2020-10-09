package org.cid15.aem.veneer.injectors.impl

import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.DefaultInjectionStrategy
import org.apache.sling.models.annotations.Model
import org.cid15.aem.veneer.api.link.Link
import org.cid15.aem.veneer.injectors.annotations.LinkInject
import org.cid15.aem.veneer.injectors.specs.VeneerModelSpec

class LinkInjectorSpec extends VeneerModelSpec {

    @Model(adaptables = Resource, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    static class Component {

        @LinkInject
        Link link1

        @LinkInject(titleProperty = "jcr:title")
        Link link2

        @LinkInject(inherit = true)
        Link link3
    }

    def setupSpec() {
        pageBuilder.content {
            cid15 {
                "jcr:content" {
                    component("jcr:title": "Testing Component",
                        link1: "/content/cid15",
                        link2: "/content/cid15",
                        link3: "/content/cid15")
                }
                child {
                    "jcr:content" { component() }
                }
            }
        }
    }

    def "link is null if component node is null"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/jcr:content/component/sub")
        def component = resource.adaptTo(Component)

        expect:
        !component.link1
    }

    def "link has correct path value"() {
        setup:
        def resource = getResource("/content/cid15/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.link1.path == "/content/cid15"
    }

    def "link has correct path value and title"() {
        setup:
        def resource = getResource("/content/cid15/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.link2.path == "/content/cid15"
        component.link2.title == "Testing Component"
    }

    def "inherited link has correct path value"() {
        setup:
        def resource = getResource("/content/cid15/child/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.link3.path == "/content/cid15"
    }
}
