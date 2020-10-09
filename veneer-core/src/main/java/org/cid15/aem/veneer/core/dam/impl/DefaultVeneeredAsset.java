package org.cid15.aem.veneer.core.dam.impl;

import com.day.cq.dam.api.Asset;
import com.day.cq.replication.ReplicationStatus;
import org.apache.sling.api.resource.Resource;
import org.cid15.aem.veneer.api.dam.VeneeredAsset;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.cid15.aem.veneer.core.constants.PathConstants;

import java.util.Optional;

public final class DefaultVeneeredAsset implements VeneeredAsset {

    private final Asset delegate;

    private final Resource assetResource;

    public DefaultVeneeredAsset(final Asset delegate) {
        this.delegate = delegate;

        assetResource = delegate.adaptTo(Resource.class);
    }

    @Override
    public Asset getAsset() {
        return delegate;
    }

    @Override
    public Optional<VeneeredResource> getMetadataResource() {
        return Optional.ofNullable(assetResource.getChild(PathConstants.RELATIVE_PATH_METADATA))
            .map(metadataResource -> metadataResource.adaptTo(VeneeredResource.class));
    }

    @Override
    public ReplicationStatus getReplicationStatus() {
        return assetResource.adaptTo(ReplicationStatus.class);
    }
}
