package org.cid15.aem.veneer.core.servlets.optionsprovider

import groovy.json.JsonBuilder
import org.apache.sling.api.SlingHttpServletRequest
import org.cid15.aem.veneer.core.specs.VeneerSpec

class OptionsProviderServletSpec extends VeneerSpec {

    static final def MAP = ["one": "One", "two": "Two"]

    static final def LIST = MAP.collect { value, text -> [value: value, text: text] }

    static final def OPTIONS = Option.fromMap(MAP)

    class NoOptionsProviderServlet extends AbstractOptionsProviderServlet {

        @Override
        List<Option> getOptions(SlingHttpServletRequest request) {
            []
        }

        @Override
        Optional<String> getOptionsRoot(SlingHttpServletRequest request) {
            Optional.empty()
        }
    }

    class RootOptionsProviderServlet extends AbstractOptionsProviderServlet {

        @Override
        List<Option> getOptions(SlingHttpServletRequest request) {
            OPTIONS
        }

        @Override
        Optional<String> getOptionsRoot(SlingHttpServletRequest request) {
            Optional.of("root")
        }
    }

    class NoRootOptionsProviderServlet extends AbstractOptionsProviderServlet {

        @Override
        List<Option> getOptions(SlingHttpServletRequest request) {
            OPTIONS
        }

        @Override
        Optional<String> getOptionsRoot(SlingHttpServletRequest request) {
            Optional.empty()
        }
    }

    def "no options"() {
        def servlet = new NoOptionsProviderServlet()
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        response.outputAsString == new JsonBuilder([]).toString()
    }

    def "options with root"() {
        def servlet = new RootOptionsProviderServlet()
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        response.outputAsString == new JsonBuilder(["root": LIST]).toString()
    }

    def "options with no root"() {
        def servlet = new NoRootOptionsProviderServlet()
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        response.outputAsString == new JsonBuilder(LIST).toString()
    }
}