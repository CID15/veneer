package org.cid15.aem.veneer.core.page.impl

import com.day.cq.wcm.api.NameConstants
import org.cid15.aem.veneer.api.page.VeneeredPage
import org.cid15.aem.veneer.api.page.enums.TitleType
import org.cid15.aem.veneer.api.resource.VeneeredResource
import org.cid15.aem.veneer.core.page.predicates.TemplatePredicate
import org.cid15.aem.veneer.core.specs.VeneerSpec
import spock.lang.Unroll

import java.util.function.Predicate

@Unroll
class DefaultVeneeredPageSpec extends VeneerSpec {

    private static final Predicate<VeneeredPage> TRUE = new Predicate<VeneeredPage>() {
        @Override
        boolean test(VeneeredPage veneeredPage) {
            true
        }
    }

    def setupSpec() {
        pageBuilder.content {
            cid15("CID15") {
                "jcr:content"(otherPagePath: "/content/ales/esb", pageTitle: "Page Title",
                    navTitle: "Navigation Title") {
                    component {
                        one("sling:resourceType": "one")
                        two("sling:resourceType": "two")
                    }
                }
                child1 {
                    "jcr:content"(hideInNav: true, "cq:template": "template")
                    grandchild()
                }
                child2 {
                    "jcr:content"(pageTitle: "Child 2", "jcr:title": "Also Child 2") {
                        image(fileReference: "/content/dam/image")
                        secondimage(fileReference: "/content/dam/image")
                    }
                }
            }
            other()
            inheritance {
                "jcr:content"("jcr:title": "Inheritance") {
                    component("jcr:title": "Component", "number": 5, "boolean": false) {
                        image(fileReference: "/content/dam/image")
                        secondimage(fileReference: "/content/dam/image")
                    }
                }
                child {
                    "jcr:content" {
                        component()
                        other()
                    }
                    sub()
                }
            }
            pageTitles {
                "jcr:content"("jcr:title": "Titles")
                noPageNoNavigationTitle("jcr:title": "Simple Title")
                pageTitleNoNavigationTitle("jcr:title": "Simple Title", pageTitle: "Page Title")
                pageTitleNavigationTitle("jcr:title": "Simple Title", pageTitle: "Page Title", navTitle: "Navigation Title")
            }
        }

        nodeBuilder.content {
            cid15 {
                empty(NameConstants.NT_PAGE)
            }
            dam("sling:Folder") {
                image("dam:Asset") {
                    "jcr:content" {
                        renditions("nt:folder") {
                            original("nt:file") {
                                "jcr:content"("nt:resource", "jcr:data": "data")
                            }
                        }
                    }
                }
            }
        }
    }

    def "get underlying page properties"() {
        setup:
        def veneeredPage = getVeneeredPage(path)

        expect:
        veneeredPage.path == path
        veneeredPage.name == name
        veneeredPage.title == title

        where:
        path                   | name          | title
        "/content/cid15"       | "cid15"       | "CID15"
        "/content/inheritance" | "inheritance" | "Inheritance"
    }

    def "get link"() {
        setup:
        def link = getVeneeredPage("/content/cid15").link

        expect:
        link.title == "CID15"
        link.href == "/content/cid15.html"
    }

    def "get link for title type"() {
        setup:
        def link = getVeneeredPage(path).getLink(TitleType.NAVIGATION_TITLE)

        expect:
        link.title == title

        where:
        path                   | title
        "/content/cid15"       | "Navigation Title"
        "/content/inheritance" | "Inheritance"
    }

    def "get navigation link"() {
        setup:
        def link = getVeneeredPage("/content/cid15").getNavigationLink(active)

        expect:
        link.active == active

        where:
        active << [true, false]
    }

    def "get href"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15")

        expect:
        veneeredPage.href == "/content/cid15.html"
    }

    def "get absolute parent"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15/child1")

        expect:
        veneeredPage.getAbsoluteParent(level).path == absoluteParentPath

        where:
        level | absoluteParentPath
        0     | "/content"
        1     | "/content/cid15"
        2     | "/content/cid15/child1"
    }

    def "get parent"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15/child1")

        expect:
        veneeredPage.getParent(level).path == parentPath

        where:
        level | parentPath
        2     | "/content"
        1     | "/content/cid15"
        0     | "/content/cid15/child1"
    }

    def "adapt to"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15")

        expect:
        veneeredPage.adaptTo(VeneeredResource).path == "/content/cid15"
    }

    def "get"() {
        setup:
        def veneeredPage = getVeneeredPage(path)

        expect:
        veneeredPage.get(propertyName, defaultValue) == result

        where:
        path                   | propertyName          | defaultValue | result
        "/content/cid15"       | "otherPagePath"       | ""           | "/content/ales/esb"
        "/content/cid15"       | "nonExistentProperty" | ""           | ""
        "/content/cid15/empty" | "otherPagePath"       | "/content"   | "/content"
    }

    def "get optional"() {
        setup:
        def veneeredPage = getVeneeredPage(path)

        expect:
        veneeredPage.get(propertyName, String).present == result

        where:
        path                   | propertyName          | result
        "/content/cid15"       | "otherPagePath"       | true
        "/content/cid15"       | "nonExistentProperty" | false
        "/content/cid15/empty" | "otherPagePath"       | false
    }

    def "get as href"() {
        setup:
        def veneeredPage = getVeneeredPage(path)

        expect:
        veneeredPage.getAsHref(propertyName).present == result

        where:
        path                   | propertyName          | result
        "/content/cid15"       | "otherPagePath"       | true
        "/content/cid15"       | "nonExistentProperty" | false
        "/content/cid15/empty" | "otherPagePath"       | false
    }

    def "get as href strict"() {
        setup:
        def veneeredPage = getVeneeredPage(path)

        expect:
        veneeredPage.getAsHref(propertyName, true).present == result

        where:
        path                   | propertyName          | result
        "/content/cid15"       | "otherPagePath"       | true
        "/content/cid15"       | "nonExistentProperty" | false
        "/content/cid15/empty" | "otherPagePath"       | false
    }

    def "get as mapped href"() {
        setup:
        def veneeredPage = getVeneeredPage(path)

        expect:
        veneeredPage.getAsHref(propertyName, false, true).present == result

        where:
        path                   | propertyName          | result
        "/content/cid15"       | "otherPagePath"       | true
        "/content/cid15"       | "nonExistentProperty" | false
        "/content/cid15/empty" | "otherPagePath"       | false
    }

    def "get as mapped href strict"() {
        setup:
        def veneeredPage = getVeneeredPage(path)

        expect:
        veneeredPage.getAsHref(propertyName, true, true).present == result

        where:
        path                   | propertyName          | result
        "/content/cid15"       | "otherPagePath"       | true
        "/content/cid15"       | "nonExistentProperty" | false
        "/content/cid15/empty" | "otherPagePath"       | false
    }

    def "find ancestor optional"() {
        setup:
        def veneeredPage = getVeneeredPage(path)

        def predicate = new Predicate<VeneeredPage>() {
            @Override
            boolean test(VeneeredPage page) {
                page.page.title == "CID15"
            }
        }

        expect:
        veneeredPage.findAncestor(predicate, excludeCurrentResource).present == isPresent

        where:
        path                    | excludeCurrentResource | isPresent
        "/content/cid15"        | false                  | true
        "/content/cid15/child1" | false                  | true
        "/content/other"        | false                  | false
        "/content/cid15"        | true                   | false
        "/content/cid15/child1" | true                   | true
        "/content/other"        | true                   | false
    }

    def "find ancestor with property"() {
        setup:
        def veneeredPage = getVeneeredPage(path)
        def ancestorPageOptional = veneeredPage.findAncestorWithProperty("jcr:title", excludeCurrentResource)

        expect:
        ancestorPageOptional.get().path == ancestorPath

        where:
        path                             | excludeCurrentResource | ancestorPath
        "/content/inheritance"           | false                  | "/content/inheritance"
        "/content/inheritance/child"     | false                  | "/content/inheritance"
        "/content/inheritance/child/sub" | false                  | "/content/inheritance"
        "/content/inheritance/child"     | true                   | "/content/inheritance"
        "/content/inheritance/child/sub" | true                   | "/content/inheritance"
    }

    def "find ancestor returns absent"() {
        setup:
        def veneeredPage = getVeneeredPage(path)
        def ancestorPageOptional = veneeredPage.findAncestorWithProperty("jcr:description", excludeCurrentResource)

        expect:
        !ancestorPageOptional.present

        where:
        path                             | excludeCurrentResource
        "/content/inheritance"           | false
        "/content/inheritance/child"     | false
        "/content/inheritance/child/sub" | false
        "/content/inheritance"           | true
        "/content/inheritance/child"     | true
        "/content/inheritance/child/sub" | true
    }

    def "find ancestor with property value"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/inheritance/child/sub")

        expect:
        veneeredPage.findAncestorWithPropertyValue("jcr:title", "Inheritance").get().path == "/content/inheritance"

        and:
        veneeredPage.findAncestorWithPropertyValue("jcr:title", "Inheritance", true).get().path == "/content/inheritance"
    }

    def "find ancestor with property value returns absent"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/inheritance/child/sub")
        def ancestorPageOptional = veneeredPage.findAncestorWithPropertyValue("jcr:title", "Foo")

        expect:
        !ancestorPageOptional.present
    }

    def "get template path"() {
        setup:
        def veneeredPage = getVeneeredPage(path)

        expect:
        veneeredPage.templatePath == templatePath

        where:
        path                    | templatePath
        "/content/cid15"        | null
        "/content/cid15/child1" | "template"
    }

    def "get component resource"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15")

        expect:
        veneeredPage.veneeredResource.get().path == "/content/cid15/jcr:content"
    }

    def "get component resource returns absent optional for page with no jcr:content node"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15/empty")

        expect:
        !veneeredPage.veneeredResource.present
    }

    def "get component resource at relative path"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15")

        expect:
        veneeredPage.getVeneeredResource("component/one").get().path == "/content/cid15/jcr:content/component/one"
    }

    def "get child"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15")

        expect:
        veneeredPage.getChild("child2").get().getTitle(TitleType.TITLE).orElse(null) == "Also Child 2"
    }

    def "list children"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15")

        expect:
        veneeredPage.listChildren().size() == 3
    }

    def "list children filtered for predicate"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15")
        def predicate = new TemplatePredicate("template")

        expect:
        veneeredPage.listChildren(predicate).size() == 1
    }

    def "list children recursively, filtered for predicate"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15")

        expect:
        veneeredPage.listChildren(predicate, true).size() == size

        where:
        predicate                         | size
        new TemplatePredicate("template") | 1
        TRUE                              | 4
    }

    def "find descendants"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15")

        expect:
        veneeredPage.findDescendants(TRUE).size() == 4
    }

    def "get properties"() {
        setup:
        def veneeredPage = getVeneeredPage(path)

        expect:
        veneeredPage.properties.containsKey(propertyName) == result

        where:
        path                   | propertyName          | result
        "/content/cid15"       | "otherPagePath"       | true
        "/content/cid15"       | "nonExistentProperty" | false
        "/content/cid15/empty" | "otherPagePath"       | false
    }

    def "get properties for page with no jcr:content node"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15/empty")

        expect:
        veneeredPage.page.properties.isEmpty()
    }

    def "get properties at relative path"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15")

        expect:
        veneeredPage.page.getProperties("component/one").containsKey("sling:resourceType")
    }

    def "get title"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15")

        expect:
        veneeredPage.getTitle(titleType).get() == title

        where:
        titleType                  | title
        TitleType.TITLE            | "CID15"
        TitleType.NAVIGATION_TITLE | "Navigation Title"
        TitleType.PAGE_TITLE       | "Page Title"
    }

    def "get title returns absent where appropriate"() {
        setup:
        def veneeredPage = getVeneeredPage("/content/cid15/child1")

        expect:
        !veneeredPage.getTitle(titleType).present

        where:
        titleType << [
            TitleType.NAVIGATION_TITLE,
            TitleType.PAGE_TITLE
        ]
    }
}
