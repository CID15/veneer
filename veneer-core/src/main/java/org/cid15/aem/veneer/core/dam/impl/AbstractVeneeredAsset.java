package org.cid15.aem.veneer.core.dam.impl;

import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.cid15.aem.veneer.api.dam.AssetContent;

/**
 * Base class for veneered assets.
 */
abstract class AbstractVeneeredAsset implements AssetContent {

    private final Resource resource;

    AbstractVeneeredAsset(final Adaptable delegate) {
        resource = delegate.adaptTo(Resource.class);
    }

    @Override
    public Resource getResource() {
        return resource;
    }
}
