package org.cid15.aem.veneer.core.link.builders.factory

import com.day.cq.dam.api.Asset
import com.day.cq.wcm.api.NameConstants
import org.cid15.aem.veneer.api.page.enums.TitleType
import org.cid15.aem.veneer.core.specs.VeneerSpec
import spock.lang.Unroll

@Unroll
class LinkBuilderFactorySpec extends VeneerSpec {

    def setupSpec() {
        pageBuilder.content {
            global("Global") {
                "jcr:content"(navTitle: "Global Navigation")
            }
            us("US") {
                "jcr:content"(pageTitle: "United States")
            }
            de("DE") {
                "jcr:content"(redirectTarget: "/content/global")
            }
        }

        nodeBuilder.content {
            dam("sling:Folder") {
                "titled-asset"("dam:Asset") {
                    "jcr:content" {
                        metadata("dc:title": "Test Asset")
                    }
                }
                "untitled-asset.pdf"("dam:Asset") {
                    "jcr:content" {
                        metadata()
                    }
                }
            }
        }

        session.getNode("/content").addNode("se", NameConstants.NT_PAGE)
        session.save()
    }

    def "for link"() {
        setup:
        def link = LinkBuilderFactory.forPath("/content/global").build()

        expect:
        LinkBuilderFactory.forLink(link).build() == link
    }

    def "for page"() {
        setup:
        def link = LinkBuilderFactory.forPage(getVeneeredPage(forPath)).build()

        expect:
        link.path == path
        link.href == href
        link.extension.get() == "html"
        link.title == title

        where:
        forPath           | path              | href                   | title
        "/content/global" | "/content/global" | "/content/global.html" | "Global"
        "/content/de"     | "/content/global" | "/content/global.html" | "DE"
    }

    def "for page with title type"() {
        setup:
        def link = LinkBuilderFactory.forPage(getVeneeredPage(path), titleType).build()

        expect:
        link.href == href
        link.title == title

        where:
        path              | titleType                  | href                   | title
        "/content/global" | TitleType.TITLE            | "/content/global.html" | "Global"
        "/content/global" | TitleType.PAGE_TITLE       | "/content/global.html" | "Global"
        "/content/global" | TitleType.NAVIGATION_TITLE | "/content/global.html" | "Global Navigation"
        "/content/us"     | TitleType.TITLE            | "/content/us.html"     | "US"
        "/content/us"     | TitleType.PAGE_TITLE       | "/content/us.html"     | "United States"
        "/content/us"     | TitleType.NAVIGATION_TITLE | "/content/us.html"     | "US"
    }

    def "for path"() {
        setup:
        def link = LinkBuilderFactory.forPath(path).build()

        expect:
        link.href == href
        link.title == ""

        where:
        path              | href
        "/content/global" | "/content/global.html"
        "/content/us"     | "/content/us.html"
    }

    def "for email"() {
        setup:
        def link = LinkBuilderFactory.forEmail("test@test.com").build()

        expect:
        link.path == "mailto:test@test.com"
        link.href == "mailto:test@test.com"
        link.title == "test@test.com"
    }

    def "for telephone number"() {
        setup:
        def link = LinkBuilderFactory.forTelephoneNumber("1234567890").build()

        expect:
        link.path == "tel:1234567890"
        link.href == "tel:1234567890"
        link.title == "1234567890"
    }

    def "for asset"() {
        setup:
        def asset = getResource(path).adaptTo(Asset)
        def link = LinkBuilderFactory.forAsset(asset).build()

        expect:
        link.path == path
        link.href == href
        link.title == title

        where:
        path                              | href                              | title
        "/content/dam/titled-asset"       | "/content/dam/titled-asset"       | "Test Asset"
        "/content/dam/untitled-asset.pdf" | "/content/dam/untitled-asset.pdf" | "untitled-asset.pdf"
    }

    def "for resource"() {
        setup:
        def link = LinkBuilderFactory.forResource(getResource(path)).build()

        expect:
        link.path == path
        link.href == href
        link.title == ""

        where:
        path                          | href
        "/content/global"             | "/content/global.html"
        "/content/global/jcr:content" | "/content/global/jcr:content.html"
    }

    def "for empty"() {
        setup:
        def link = LinkBuilderFactory.forEmpty().build()

        expect:
        link.path == "#"
        link.href == "#"
        link.title == ""
        !link.extension.present
    }
}
