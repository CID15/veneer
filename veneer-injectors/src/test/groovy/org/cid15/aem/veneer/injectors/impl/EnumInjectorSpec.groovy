package org.cid15.aem.veneer.injectors.impl

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model
import org.apache.sling.models.annotations.Optional
import org.cid15.aem.veneer.injectors.specs.VeneerModelSpec

import javax.inject.Inject

class EnumInjectorSpec extends VeneerModelSpec {

    static enum Beer {
        ALE, LAGER
    }

    @Model(adaptables = SlingHttpServletRequest)
    static class EnumInjectorModel {

        @Inject
        @Optional
        Beer beer
    }

    def setupSpec() {
        pageBuilder.content {
            cid15 {
                "jcr:content" {
                    component(beer: "LAGER")
                }
            }
        }
    }

    def "enum is injected when component property value is valid"() {
        setup:
        def request = requestBuilder.build {
            path = "/content/cid15/jcr:content/component"
        }

        def model = request.adaptTo(EnumInjectorModel)

        expect:
        model.beer == Beer.LAGER
    }

    def "enum is null when component property does not exist"() {
        setup:
        def request = requestBuilder.build {
            path = "/content/cid15/jcr:content"
        }

        def model = request.adaptTo(EnumInjectorModel)

        expect:
        !model.beer
    }
}
