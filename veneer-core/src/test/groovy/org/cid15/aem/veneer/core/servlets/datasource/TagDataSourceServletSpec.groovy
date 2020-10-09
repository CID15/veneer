package org.cid15.aem.veneer.core.servlets.datasource

import com.adobe.granite.ui.components.ds.DataSource
import com.day.cq.commons.Filter
import com.day.cq.tagging.Tag
import org.apache.sling.api.SlingHttpServletRequest
import org.cid15.aem.veneer.core.servlets.optionsprovider.Option
import org.cid15.aem.veneer.core.specs.VeneerSpec

import static com.day.cq.tagging.TagConstants.NT_TAG

class TagDataSourceServletSpec extends VeneerSpec {

    static final def OPTIONS = Option.fromMap([
        "lager": "Lager",
        "stout": "Stout",
        "porter": "Porter",
        "ale": "Ale"
    ])

    class BasicTagDataSourceServlet extends AbstractTagDataSourceServlet {

        @Override
        protected String getNamespace() {
            "beers:"
        }
    }

    class FilteredTagDataSourceServlet extends AbstractTagDataSourceServlet {

        @Override
        protected String getNamespace() {
            "beers:"
        }

        @Override
        protected Filter<Tag> getTagFilter() {
            new Filter<Tag>() {
                @Override
                boolean includes(Tag tag) {
                    tag.title == "Lager" || tag.title == "Ale"
                }
            }
        }
    }

    class ContainerTagDataSourceServlet extends AbstractTagDataSourceServlet {

        @Override
        protected String getNamespace() {
            "beers:"
        }

        @Override
        protected String getContainerTagRelativePath() {
            "lager"
        }
    }

    def setupSpec() {
        nodeBuilder.content {
            "cq:tags"("sling:Folder") {
                beers(NT_TAG, "sling:resourceType": "cq/tagging/components/tag", "jcr:title": "Beers") {
                    lager(NT_TAG, "sling:resourceType": "cq/tagging/components/tag", "jcr:title": "Lager") {
                        pilsner(NT_TAG, "sling:resourceType": "cq/tagging/components/tag", "jcr:title": "Pilsner")
                        helles(NT_TAG, "sling:resourceType": "cq/tagging/components/tag", "jcr:title": "Helles")
                    }
                    stout(NT_TAG, "sling:resourceType": "cq/tagging/components/tag", "jcr:title": "Stout")
                    porter(NT_TAG, "sling:resourceType": "cq/tagging/components/tag", "jcr:title": "Porter")
                    ale(NT_TAG, "sling:resourceType": "cq/tagging/components/tag", "jcr:title": "Ale")
                }
            }
        }
    }

    def "all tags options"() {
        def servlet = new BasicTagDataSourceServlet()
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        assertDataSourceOptions(request, OPTIONS)
    }

    def "filtered tags options"() {
        def servlet = new FilteredTagDataSourceServlet()
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        assertDataSourceOptions(request, Option.fromMap(["lager": "Lager", "ale": "Ale"]))
    }

    def "container tag options"() {
        def servlet = new ContainerTagDataSourceServlet()
        def request = requestBuilder.build()
        def response = responseBuilder.build()

        when:
        servlet.doGet(request, response)

        then:
        assertDataSourceOptions(request, Option.fromMap(["pilsner": "Pilsner", "helles": "Helles"]))
    }

    void assertDataSourceOptions(SlingHttpServletRequest request, List<Option> options) {
        def dataSource = request.getAttribute(DataSource.name) as DataSource
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
