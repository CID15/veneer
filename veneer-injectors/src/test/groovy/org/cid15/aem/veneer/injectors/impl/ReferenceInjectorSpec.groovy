package org.cid15.aem.veneer.injectors.impl

import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.DefaultInjectionStrategy
import org.apache.sling.models.annotations.Model
import org.cid15.aem.veneer.api.page.VeneeredPage
import org.cid15.aem.veneer.injectors.annotations.ReferenceInject
import org.cid15.aem.veneer.injectors.specs.VeneerModelSpec

import javax.inject.Named

class ReferenceInjectorSpec extends VeneerModelSpec {

    @Model(adaptables = Resource, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    static class Component {

        @ReferenceInject
        Resource singleResource

        @ReferenceInject
        VeneeredPage singlePage

        @ReferenceInject
        List<Resource> multipleResources

        @ReferenceInject
        List<VeneeredPage> multiplePages

        @ReferenceInject(inherit = true)
        Resource inheritedSingleResource

        @ReferenceInject(inherit = true)
        VeneeredPage inheritedSinglePage

        @ReferenceInject(inherit = true)
        List<Resource> inheritedMultipleResources

        @ReferenceInject(inherit = true)
        List<VeneeredPage> inheritedMultiplePages

        @ReferenceInject
        @Named("mySingleResource")
        Resource namedSingleResource

        @ReferenceInject(inherit = true)
        @Named("inheritedMySingleResource")
        Resource inheritedNamedSingleResource
    }

    def setupSpec() {
        pageBuilder.content {
            cid15 {
                "jcr:content" {
                    component(
                        inheritedSingleResource: "/content/cid15/page1",
                        inheritedSinglePage: "/content/cid15/page1",
                        inheritedMultipleResources: [
                            "/content/cid15/page1",
                            "/content/cid15/page2",
                            "/content/cid15/page3"
                        ],
                        inheritedMultiplePages: [
                            "/content/cid15/page1",
                            "/content/cid15/page2",
                            "/content/cid15/page3"
                        ],
                        inheritedMySingleResource: "/content/cid15/page1"
                    )
                    unconfiguredComponent()
                }
                page1 {
                    "jcr:content" {}
                }
                page2 {
                    "jcr:content" {}
                }
                page3 {
                    "jcr:content" {}
                }
                componentPage {
                    "jcr:content" {
                        component(
                            singleResource: "/content/cid15/page1",
                            singlePage: "/content/cid15/page1",
                            multipleResources: [
                                "/content/cid15/page1",
                                "/content/cid15/page2",
                                "/content/cid15/page3"
                            ],
                            multiplePages: [
                                "/content/cid15/page1",
                                "/content/cid15/page2",
                                "/content/cid15/page3"
                            ],
                            mySingleResource: "/content/cid15/page1"
                        )
                    }
                }
            }
        }
    }

    def "all properties should be null if component is unconfigured"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/jcr:content/unconfiguredComponent")
        def component = resource.adaptTo(Component)

        expect:
        !component.singleResource
        !component.singlePage
        !component.multipleResources
        !component.multiplePages
        !component.inheritedSingleResource
        !component.inheritedSinglePage
        !component.inheritedMultipleResources
        !component.inheritedMultiplePages
        !component.namedSingleResource
    }

    def "component has a single Resource configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.singleResource instanceof Resource
        component.singleResource.path == "/content/cid15/page1"
    }

    def "component has a single VeneeredPage configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.singlePage instanceof VeneeredPage
        component.singlePage.path == "/content/cid15/page1"
    }

    def "component has multiple Resources configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.multipleResources.size() == 3
        component.multipleResources[0] instanceof Resource
        component.multipleResources[1] instanceof Resource
        component.multipleResources[2] instanceof Resource
        component.multipleResources[0].path == "/content/cid15/page1"
        component.multipleResources[1].path == "/content/cid15/page2"
        component.multipleResources[2].path == "/content/cid15/page3"
    }

    def "component has multiple VeneeredPages configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.multiplePages.size() == 3
        component.multiplePages[0] instanceof VeneeredPage
        component.multiplePages[1] instanceof VeneeredPage
        component.multiplePages[2] instanceof VeneeredPage
        component.multiplePages[0].path == "/content/cid15/page1"
        component.multiplePages[1].path == "/content/cid15/page2"
        component.multiplePages[2].path == "/content/cid15/page3"
    }

    def "component has a single inherited Resource configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.inheritedSingleResource instanceof Resource
        component.inheritedSingleResource.path == "/content/cid15/page1"
    }

    def "component has a single inherited VeneeredPage configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.inheritedSinglePage instanceof VeneeredPage
        component.inheritedSinglePage.path == "/content/cid15/page1"
    }

    def "component has multiple inherited Resources configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.inheritedMultipleResources.size() == 3
        component.inheritedMultipleResources[0] instanceof Resource
        component.inheritedMultipleResources[1] instanceof Resource
        component.inheritedMultipleResources[2] instanceof Resource
        component.inheritedMultipleResources[0].path == "/content/cid15/page1"
        component.inheritedMultipleResources[1].path == "/content/cid15/page2"
        component.inheritedMultipleResources[2].path == "/content/cid15/page3"
    }

    def "component has multiple inherited VeneeredPages configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.inheritedMultiplePages.size() == 3
        component.inheritedMultiplePages[0] instanceof VeneeredPage
        component.inheritedMultiplePages[1] instanceof VeneeredPage
        component.inheritedMultiplePages[2] instanceof VeneeredPage
        component.inheritedMultiplePages[0].path == "/content/cid15/page1"
        component.inheritedMultiplePages[1].path == "/content/cid15/page2"
        component.inheritedMultiplePages[2].path == "/content/cid15/page3"
    }

    def "component has a named single Resource configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.namedSingleResource instanceof Resource
        component.namedSingleResource.path == "/content/cid15/page1"
    }

    def "component has a named single inherited Resource configured"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/componentPage/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.inheritedNamedSingleResource instanceof Resource
        component.inheritedNamedSingleResource.path == "/content/cid15/page1"
    }
}
