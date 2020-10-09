package org.cid15.aem.veneer.injectors.impl

import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model
import org.cid15.aem.veneer.core.components.AbstractComponent

@Model(adaptables = Resource)
class VeneerModelComponent extends AbstractComponent {

    String getTitle() {
        get("jcr:title", "")
    }
}