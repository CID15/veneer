package org.cid15.aem.veneer.injectors.impl

import org.cid15.aem.veneer.injectors.specs.VeneerModelSpec
import spock.lang.Unroll

@Unroll
class VeneerModelComponentSpec extends VeneerModelSpec {

    def setupSpec() {
        pageBuilder.content {
            cid15 {
                "jcr:content" {
                    component("jcr:title": "Testing Component")
                }
            }
        }
    }

    def "get title from component"() {
        setup:
        def component = getResource("/content/cid15/jcr:content/component").adaptTo(VeneerModelComponent)

        expect:
        component.title == "Testing Component"
    }
}
