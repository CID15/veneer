package org.cid15.aem.veneer.core.page.impl

import com.day.cq.tagging.TagConstants
import org.cid15.aem.veneer.api.page.VeneeredPage
import org.cid15.aem.veneer.core.specs.VeneerSpec

import javax.jcr.query.Query
import java.util.function.Predicate

import static com.day.cq.tagging.TagConstants.NT_TAG

class DefaultVeneeredPageManagerSpec extends VeneerSpec {

    def setupSpec() {
        pageBuilder.content {
            cid15("CID15") {
                "jcr:content"(otherPagePath: "/content/ales/esb") {
                    component {
                        one("sling:resourceType": "one", "cq:tags": "/content/cq:tags/tag3")
                        two("sling:resourceType": "two")
                    }
                }
                child {
                    "jcr:content"(hideInNav: true, "cq:template": "template", "cq:tags": "/content/cq:tags/tag1")
                }
            }
            other {
                "jcr:content"("cq:template": "template", "cq:tags": ["/content/cq:tags/tag1", "/content/cq:tags/tag2"])
            }
            hierarchy {
                one {
                    child1()
                    child2()
                    child3()
                }
                two()
                three()
            }
        }

        nodeBuilder.content {
            "cq:tags"("sling:Folder") {
                tag1(NT_TAG)
                tag2(NT_TAG)
                tag3(NT_TAG)
            }
        }

        def taggableNodePaths = [
            "/content/cid15/child/jcr:content",
            "/content/other/jcr:content",
            "/content/cid15/jcr:content/component/one"
        ]

        taggableNodePaths.each { path ->
            session.getNode(path).addMixin(TagConstants.NT_TAGGABLE)
        }

        session.save()
    }

    def "find pages for predicate"() {
        setup:
        def predicate = new Predicate<VeneeredPage>() {
            @Override
            boolean test(VeneeredPage veneeredPage) {
                veneeredPage.page.name.startsWith("child")
            }
        }

        expect:
        veneeredPageManager.findVeneeredPages("/content/hierarchy", predicate).size() == 3
    }

    def "find pages for tag IDs"() {
        expect:
        veneeredPageManager.findVeneeredPages("/content", ["/content/cq:tags/tag1"], true).size() == 2
    }

    def "find pages for tag IDs matching all"() {
        expect:
        veneeredPageManager.findVeneeredPages("/content", ["/content/cq:tags/tag1", "/content/cq:tags/tag2"], false).size() == 1
    }

    def "tagged non-page node is excluded from search results"() {
        expect:
        !veneeredPageManager.findVeneeredPages("/content", ["/content/cq:tags/tag3"], true)
    }

    def "search"() {
        setup:
        def statement = "/jcr:root/content//element(*, cq:Page) order by @jcr:score descending"
        def query = session.workspace.queryManager.createQuery(statement, Query.XPATH)

        expect:
        veneeredPageManager.search(query).size() == 10
    }

    def "search with limit"() {
        setup:
        def statement = "/jcr:root/content//element(*, cq:Page) order by @jcr:score descending"
        def query = session.workspace.queryManager.createQuery(statement, Query.XPATH)

        expect:
        veneeredPageManager.search(query, 1).size() == 1
    }

    def "find pages for template"() {
        expect:
        veneeredPageManager.findVeneeredPages("/content", "template").size() == 2
    }

    def "find pages for non-existing template"() {
        expect:
        !veneeredPageManager.findVeneeredPages("/content", "ghost")
    }

    def "find pages for template with invalid starting path"() {
        expect:
        !veneeredPageManager.findVeneeredPages("/etc", "template")
    }
}
