package org.cid15.aem.veneer.injectors.impl

import com.day.cq.tagging.TagManager
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.Model
import org.cid15.aem.veneer.api.page.VeneeredPageManager
import org.cid15.aem.veneer.injectors.specs.VeneerModelSpec

import javax.inject.Inject

class InjectorIntegrationSpec extends VeneerModelSpec {

    @Model(adaptables = SlingHttpServletRequest)
    static class InjectorIntegrationComponent {

        @Inject
        VeneeredPageManager pageManager

        @Inject
        TagManager tagManager
    }

    def "injected values from multiple injectors are correct types"() {
        setup:
        def request = requestBuilder.build()
        def model = request.adaptTo(InjectorIntegrationComponent)

        expect:
        model.pageManager instanceof VeneeredPageManager

        and:
        model.tagManager instanceof TagManager
    }
}
