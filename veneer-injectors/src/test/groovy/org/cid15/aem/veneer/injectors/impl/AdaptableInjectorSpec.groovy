package org.cid15.aem.veneer.injectors.impl

import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.models.annotations.DefaultInjectionStrategy
import org.apache.sling.models.annotations.Model
import org.cid15.aem.veneer.api.page.VeneeredPageManager
import org.cid15.aem.veneer.api.resource.VeneeredResource
import org.cid15.aem.veneer.core.specs.VeneerSpec

import javax.inject.Inject

import static org.osgi.framework.Constants.SERVICE_RANKING

class AdaptableInjectorSpec extends VeneerSpec {

    @Model(adaptables = SlingHttpServletRequest, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    static class AdaptableModel {

        @Inject
        VeneeredPageManager pageManager

        @Inject
        VeneeredResource veneeredResource
    }

    def setupSpec() {
        slingContext.registerInjectActivateService(new ResourceResolverAdaptableInjector(), [(SERVICE_RANKING): Integer.MIN_VALUE])
        slingContext.addModelsForPackage(this.class.package.name)
    }

    def "get value returns null for invalid adapter type"() {
        setup:
        def request = requestBuilder.build()
        def model = request.adaptTo(AdaptableModel)

        expect:
        !model.veneeredResource
    }

    def "get value returns non-null for valid adapter type"() {
        setup:
        def request = requestBuilder.build()
        def model = request.adaptTo(AdaptableModel)

        expect:
        model.pageManager
    }
}
