package org.cid15.aem.veneer.core.page.predicates

import org.cid15.aem.veneer.core.specs.VeneerSpec

class TemplatePredicateSpec extends VeneerSpec {

    def setupSpec() {
        pageBuilder.content {
            cid15 {
                "jcr:content"("cq:template": "homepage")
                child1 {
                    "jcr:content"("cq:template": "template")
                }
                child2()
            }
        }
    }

    def "page has no template property"() {
        setup:
        def page = getVeneeredPage("/content/cid15/child2")
        def predicate = new TemplatePredicate("template")

        expect:
        !predicate.test(page)
    }

    def "template matches page template"() {
        setup:
        def page = getVeneeredPage("/content/cid15/child1")
        def predicate = new TemplatePredicate("template")
        def predicateForPage = new TemplatePredicate(page)

        expect:
        predicate.test(page) && predicateForPage.test(page)
    }

    def "template does not match page template"() {
        setup:
        def page = getVeneeredPage("/content/cid15")
        def predicate = new TemplatePredicate("template")

        expect:
        !predicate.test(page)
    }
}
