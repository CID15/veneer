package org.cid15.aem.veneer.core.specs

import com.icfolson.aem.prosper.specs.ProsperSpec
import org.cid15.aem.veneer.api.page.VeneeredPage
import org.cid15.aem.veneer.api.page.VeneeredPageManager
import org.cid15.aem.veneer.api.resource.VeneeredResource
import org.cid15.aem.veneer.core.adapter.VeneerDamAdapterFactory
import org.cid15.aem.veneer.core.adapter.VeneerPageAdapterFactory
import org.cid15.aem.veneer.core.resource.impl.DefaultVeneeredResource

/**
 * Spock specification for testing Veneer-based components and services.
 */
abstract class VeneerSpec extends ProsperSpec {

    def setupSpec() {
        slingContext.registerAdapterFactory(new VeneerPageAdapterFactory())
        slingContext.registerAdapterFactory(new VeneerDamAdapterFactory())
        slingContext.addModelsForClasses(DefaultVeneeredResource)
    }

    VeneeredResource getVeneeredResource(String path) {
        resourceResolver.resolve(path).adaptTo(VeneeredResource)
    }

    VeneeredPage getVeneeredPage(String path) {
        veneeredPageManager.getVeneeredPage(path)
    }

    VeneeredPageManager getVeneeredPageManager() {
        resourceResolver.adaptTo(VeneeredPageManager)
    }
}
