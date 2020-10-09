package org.cid15.aem.veneer.injectors.impl


import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model
import org.cid15.aem.veneer.injectors.annotations.InheritInject
import org.cid15.aem.veneer.injectors.specs.VeneerModelSpec

import javax.inject.Inject

import static org.apache.sling.models.annotations.DefaultInjectionStrategy.OPTIONAL

class InheritInjectorSpec extends VeneerModelSpec {

    enum Option {
        ONE, TWO
    }

    @Model(adaptables = Resource, defaultInjectionStrategy = OPTIONAL)
    static class InheritModel {

        @InheritInject
        String title

        @InheritInject
        Option option

        @InheritInject
        String[] items

        @Inject
        String text

        @InheritInject
        List<InheritModel> models

        @InheritInject
        InheritModel resourceModel
    }

    def setupSpec() {
        pageBuilder.content {
            cid15 {
                "jcr:content" {
                    component("title": "Testing Component", "items": ["item1", "item2"], "text": "Not Inherited",
                        "option": "TWO") {
                        models {
                            item1("title": "Item 1")
                            item2("title": "Item 2")
                            item3("title": "Item 3")
                        }
                        resourceModel("title": "Resource Model Title")
                    }
                }
                page1()
            }
        }
    }

    def "basic inheritance"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/page1/jcr:content/component")
        def component = resource.adaptTo(InheritModel)

        expect:
        component.title == "Testing Component"

        and:
        component.items.length == 2

        and:
        component.text == null
    }

    def "enum inheritance"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/page1/jcr:content/component")
        def component = resource.adaptTo(InheritModel)

        expect:
        component.option == Option.TWO
    }

    def "model list inheritance"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/page1/jcr:content/component")
        def component = resource.adaptTo(InheritModel)

        expect:
        component.models.size() == 3

        and:
        component.models*.title == ["Item 1", "Item 2", "Item 3"]
    }

    def "model child resource inheritance"() {
        setup:
        def resource = resourceResolver.resolve("/content/cid15/page1/jcr:content/component")
        def component = resource.adaptTo(InheritModel)

        expect:
        component.resourceModel.title == "Resource Model Title"
    }
}