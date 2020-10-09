package org.cid15.aem.veneer.core.servlets.datasource

import com.adobe.granite.ui.components.ds.DataSource
import org.apache.sling.api.SlingHttpServletRequest
import org.cid15.aem.veneer.core.servlets.optionsprovider.Option
import org.cid15.aem.veneer.core.specs.VeneerSpec

class OptionsDataSourceServletSpec extends VeneerSpec {

    static final def MAP = ["one": "One", "two": "Two"]

    static final def OPTIONS = Option.fromMap(MAP)

    class NoOptionsProviderServlet extends AbstractOptionsDataSourceServlet {

        @Override
        List<Option> getOptions(SlingHttpServletRequest request) {
            []
        }
    }

    class BasicOptionsProviderServlet extends AbstractOptionsDataSourceServlet {

        @Override
        List<Option> getOptions(SlingHttpServletRequest request) {
            OPTIONS
        }
    }

    def "no options"() {
        def servlet = new NoOptionsProviderServlet()
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        assertDataSourceOptions(request, [])
    }

    def "options"() {
        def servlet = new BasicOptionsProviderServlet()
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        assertDataSourceOptions(request, OPTIONS)
    }

    void assertDataSourceOptions(SlingHttpServletRequest request, List<Option> options) {
        def dataSource = request.getAttribute(DataSource.class.name) as DataSource
        def resources = dataSource.iterator()

        assert resources.size() == options.size()

        resources.eachWithIndex { resource, i ->
            def properties = resource.valueMap
            def option = options.get(i)

            assert option.text == properties.get("text", "")
            assert option.value == properties.get("value", "")
        }
    }
}
