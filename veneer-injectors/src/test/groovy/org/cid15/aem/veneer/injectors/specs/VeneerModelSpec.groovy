package org.cid15.aem.veneer.injectors.specs

import org.cid15.aem.veneer.core.specs.VeneerSpec
import org.cid15.aem.veneer.injectors.impl.ComponentInjector
import org.cid15.aem.veneer.injectors.impl.ContentPolicyInjector
import org.cid15.aem.veneer.injectors.impl.EnumInjector
import org.cid15.aem.veneer.injectors.impl.ImageInjector
import org.cid15.aem.veneer.injectors.impl.InheritInjector
import org.cid15.aem.veneer.injectors.impl.LinkInjector
import org.cid15.aem.veneer.injectors.impl.ModelListInjector
import org.cid15.aem.veneer.injectors.impl.ReferenceInjector
import org.cid15.aem.veneer.injectors.impl.ResourceResolverAdaptableInjector
import org.cid15.aem.veneer.injectors.impl.TagInjector
import org.cid15.aem.veneer.injectors.impl.ValueMapFromRequestInjector

/**
 * Specs may extend this class to support injection of Veneer dependencies in Sling model-based components.
 */
abstract class VeneerModelSpec extends VeneerSpec {

    /**
     * Register default Veneer injectors and all <code>@Model>/code>-annotated classes for the current package.
     */
    def setupSpec() {
        registerDefaultInjectors()

        slingContext.addModelsForPackage(this.class.package.name)
    }

    /**
     * Register the default set of Veneer injectors.
     */
    void registerDefaultInjectors() {
        slingContext.with {
            registerInjector(new ComponentInjector(), Integer.MIN_VALUE)
            registerInjector(new ResourceResolverAdaptableInjector(), Integer.MAX_VALUE)
            registerInjector(new TagInjector())
            registerInjector(new ContentPolicyInjector())
            registerInjector(new EnumInjector())
            registerInjector(new ImageInjector())
            registerInjector(new InheritInjector())
            registerInjector(new LinkInjector())
            registerInjector(new ReferenceInjector())
            registerInjector(new ModelListInjector())
            registerInjector(new ValueMapFromRequestInjector())
        }
    }
}
