package org.cid15.aem.veneer.injectors.impl

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model
import org.cid15.aem.veneer.injectors.specs.VeneerModelSpec

import javax.inject.Inject

class ModelListInjectorSpec extends VeneerModelSpec {

    @Model(adaptables = Resource)
    static class ValueMapInjectorModel {

        @Inject
        String name
    }

    @Model(adaptables = SlingHttpServletRequest)
    static class ListModel {

        @Inject
        List<ValueMapInjectorModel> rush
    }

    def setupSpec() {
        pageBuilder.content {
            cid15 {
                "jcr:content" {
                    component {
                        rush {
                            drums(name: "Neil")
                            bass(name: "Geddy")
                            guitar(name: "Alex")
                        }
                    }
                }
            }
        }
    }

    def "inject list of models"() {
        setup:
        def request = requestBuilder.build {
            path = "/content/cid15/jcr:content/component"
        }

        def model = request.adaptTo(ListModel)

        expect:
        model.rush.size() == 3
        model.rush*.name == ["Neil", "Geddy", "Alex"]
    }
}
