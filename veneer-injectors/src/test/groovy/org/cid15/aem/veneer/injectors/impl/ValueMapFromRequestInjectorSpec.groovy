package org.cid15.aem.veneer.injectors.impl

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.Optional
import org.cid15.aem.veneer.injectors.specs.VeneerModelSpec

import javax.inject.Inject

class ValueMapFromRequestInjectorSpec extends VeneerModelSpec {

    @Model(adaptables = SlingHttpServletRequest)
    static class ValueMapInjectorModel {

        @Inject
        String name

        @Inject
        @Optional
        String title
    }

    def setupSpec() {
        pageBuilder.content {
            cid15 {
                "jcr:content" {
                    component(name: "Veneer")
                }
            }
        }

        slingContext.registerInjectActivateService(new ValueMapFromRequestInjector())
    }

    def "inject values for component"() {
        setup:
        def request = requestBuilder.build {
            path = "/content/cid15/jcr:content/component"
        }

        def model = request.adaptTo(ValueMapInjectorModel)

        expect:
        model.name == "Veneer"

        and:
        !model.title
    }
}
