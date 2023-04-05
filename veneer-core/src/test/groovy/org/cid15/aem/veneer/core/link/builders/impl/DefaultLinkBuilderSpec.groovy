package org.cid15.aem.veneer.core.link.builders.impl

import veneer.com.google.common.collect.LinkedHashMultimap
import veneer.com.google.common.collect.SetMultimap
import io.wcm.testing.mock.aem.junit.AemContext
import io.wcm.testing.mock.aem.junit.AemContextBuilder
import org.apache.sling.testing.mock.sling.ResourceResolverType
import org.cid15.aem.veneer.core.specs.VeneerSpec
import spock.lang.Unroll

@Unroll
class DefaultLinkBuilderSpec extends VeneerSpec {

    @Override
    AemContext getAemContext() {
        new AemContextBuilder(ResourceResolverType.JCR_OAK)
            .resourceResolverFactoryActivatorProps(["resource.resolver.mapping": ["/content/:/", "/-/"] as String[]])
            .build()
    }

    def setupSpec() {
        pageBuilder.content {
            global("Global") {
                "jcr:content"(navTitle: "Global Navigation")
            }
            us("US") {
                "jcr:content"()
            }
        }
    }

    def "build link with no extension"() {
        setup:
        def link = new DefaultLinkBuilder("/content/global")
            .noExtension()
            .build()

        expect:
        link.path == "/content/global"
        link.href == "/content/global"
        !link.extension.present
    }

    def "build link for resource resolver mapped page"() {
        setup:
        def link = new DefaultLinkBuilder(path)
            .mapped(resourceResolver)
            .build()

        expect:
        link.href == mappedHref

        where:
        path              | mappedHref
        "/content/us"     | "/us.html"
        "/content/global" | "/global.html"
    }

    def "build link for request mapped page"() {
        setup:
        def request = requestBuilder.build()

        def link = new DefaultLinkBuilder(path)
            .mapped(request)
            .build()

        expect:
        link.href == mappedHref

        where:
        path              | mappedHref
        "/content/us"     | "/us.html"
        "/content/global" | "/global.html"
    }

    def "build link with selector in path"() {
        setup:
        def link = new DefaultLinkBuilder("/content/dam/image.png").build()

        expect:
        link.path == "/content/dam/image.png"
        link.href == "/content/dam/image.png"
        link.extension.get() == "png"
    }

    def "build link for path"() {
        setup:
        def link = new DefaultLinkBuilder("/content")
            .setExtension(extension)
            .setSuffix(suffix)
            .setScheme(scheme)
            .setHost(host)
            .setPort(port)
            .setSecure(secure)
            .build()

        expect:
        link.href == href

        where:
        extension | suffix    | scheme | host        | port | secure | href
        null      | ""        | null   | "localhost" | 0    | false  | "http://localhost/content.html"
        null      | "/suffix" | null   | "localhost" | 0    | false  | "http://localhost/content.html/suffix"
        ""        | ""        | null   | "localhost" | 0    | false  | "http://localhost/content"
        ""        | "/suffix" | null   | "localhost" | 0    | false  | "http://localhost/content/suffix"
        "html"    | ""        | null   | "localhost" | 0    | false  | "http://localhost/content.html"
        "html"    | "/suffix" | null   | "localhost" | 0    | false  | "http://localhost/content.html/suffix"
        "json"    | ""        | null   | "localhost" | 0    | false  | "http://localhost/content.json"
        null      | ""        | null   | "localhost" | 4502 | false  | "http://localhost:4502/content.html"
        null      | ""        | null   | "localhost" | 0    | true   | "https://localhost/content.html"
        null      | ""        | "ftp"  | "localhost" | 0    | false  | "ftp://localhost/content.html"
        null      | ""        | "ftp"  | "localhost" | 0    | true   | "ftp://localhost/content.html"
    }

    def "build link and set external"() {
        setup:
        def link = new DefaultLinkBuilder("/content")
            .setExternal(external)
            .build()

        expect:
        link.href == href

        where:
        external | href
        true     | "/content"
        false    | "/content.html"
    }

    def "build link with property"() {
        setup:
        def link = new DefaultLinkBuilder("/content")
            .addProperty("name", "Mark")
            .build()

        expect:
        link.properties["name"] == "Mark"
    }

    def "build link with multiple properties"() {
        setup:
        def link = new DefaultLinkBuilder("/content")
            .addProperty("firstName", "Mark")
            .addProperty("lastName", "Daugherty")
            .build()

        expect:
        link.properties["firstName"] == "Mark"
        link.properties["lastName"] == "Daugherty"
    }

    def "build link with properties"() {
        setup:
        def link = new DefaultLinkBuilder("/content")
            .addProperties(["firstName": "Mark", "lastName": "Daugherty"])
            .build()

        expect:
        link.properties["firstName"] == "Mark"
        link.properties["lastName"] == "Daugherty"
    }

    def "build link for path with selector"() {
        setup:
        def link = new DefaultLinkBuilder("/content")
            .addSelector("a")
            .build()

        expect:
        link.href == "/content.a.html"
    }

    def "build link for path with multiple selectors"() {
        setup:
        def link = new DefaultLinkBuilder("/content")
            .addSelector("a")
            .addSelector("b")
            .build()

        expect:
        link.href == "/content.a.b.html"
    }

    def "build link for path with selectors"() {
        setup:
        def link = new DefaultLinkBuilder(path)
            .addSelectors(selectors)
            .build()

        expect:
        link.href == href

        where:
        path                    | selectors  | href
        "/content"              | []         | "/content.html"
        "/content"              | ["a"]      | "/content.a.html"
        "/content"              | ["a", "b"] | "/content.a.b.html"
        "http://www.reddit.com" | ["a", "b"] | "http://www.reddit.com"
    }

    def "build link for path with scheme"() {
        setup:
        def link = new DefaultLinkBuilder(path)
            .setScheme(scheme)
            .setOpaque(opaque)
            .build()

        expect:
        link.href == href

        where:
        path                    | scheme   | opaque | href
        "/content"              | "http"   | false  | "/content.html"
        "+48957228989"          | "tel"    | true   | "tel:+48957228989"
        "http://www.reddit.com" | ""       | false  | "http://www.reddit.com"
        "http://www.reddit.com" | ""       | true   | "http://www.reddit.com"
        "https://reddit.com"    | "ftp"    | true   | "ftp:https://reddit.com"
        "https://reddit.com"    | "ftp"    | false  | "ftp://https://reddit.com"
        "someone@domain.com"    | "mailto" | true   | "mailto:someone@domain.com"
    }

    def "build link for path with parameters"() {
        setup:
        def link = new DefaultLinkBuilder("/content")
            .addParameters(parameters)
            .build()

        expect:
        link.href == href
        link.queryString.orElse("") == queryString

        where:
        parameters           | href                    | queryString
        [:]                  | "/content.html"         | ""
        ["a": "1"]           | "/content.html?a=1"     | "?a=1"
        ["a": "1", "b": "2"] | "/content.html?a=1&b=2" | "?a=1&b=2"
    }

    def "build link for path with multimap parameters"() {
        setup:
        SetMultimap<String, String> parameters = LinkedHashMultimap.create()

        parameters.put("a", "1")
        parameters.put("a", "2")
        parameters.put("b", "1")

        def link = new DefaultLinkBuilder("/content")
            .addParameters(parameters)
            .build()

        expect:
        link.href == "/content.html?a=1&a=2&b=1"
        link.queryString.get() == "?a=1&a=2&b=1"
    }

    def "build link for path with same-name parameters"() {
        setup:
        def link = new DefaultLinkBuilder("/content")
            .addParameter("a", "1")
            .addParameter("a", "2")
            .addParameter("a", "3")
            .build()

        expect:
        link.queryString.get() == "?a=1&a=2&a=3"
    }

    def "build link without children"() {
        setup:
        def link = new DefaultLinkBuilder("/content/global").build()

        expect:
        !link.children
    }

    def "build link without children with active state"() {
        setup:
        def link = new DefaultLinkBuilder("/content/global")
            .setActive(active)
            .build()

        expect:
        link.active == active

        where:
        active << [true, false]
    }

    def "build link with children"() {
        setup:
        def link = new DefaultLinkBuilder("/content/global")
            .addChild(new DefaultLinkBuilder("/content/1").build())
            .addChild(new DefaultLinkBuilder("/content/2").build())
            .addChild(new DefaultLinkBuilder("/content/3").build())
            .build()

        expect:
        link.children.size() == 3
        link.children*.path == ["/content/1", "/content/2", "/content/3"]
    }
}