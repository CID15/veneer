package org.cid15.aem.veneer.core.dam.impl;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationStatus;
import org.apache.sling.api.resource.Resource;
import org.cid15.aem.veneer.api.dam.VeneeredContentFragment;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.cid15.aem.veneer.core.constants.PathConstants;

import java.util.Optional;

public final class DefaultVeneeredContentFragment implements VeneeredContentFragment {

    private final ContentFragment delegate;

    private final Resource contentFragmentResource;

    public DefaultVeneeredContentFragment(final ContentFragment delegate) {
        this.delegate = delegate;

        contentFragmentResource = delegate.adaptTo(Resource.class);
    }

    @Override
    public ContentFragment getContentFragment() {
        return delegate;
    }

    @Override
    public Optional<VeneeredResource> getContentResource() {
        return Optional.ofNullable(contentFragmentResource.getChild(JcrConstants.JCR_CONTENT))
            .map(contentResource -> contentResource.adaptTo(VeneeredResource.class));
    }

    @Override
    public Optional<VeneeredResource> getMetadataResource() {
        return Optional.ofNullable(contentFragmentResource.getChild(PathConstants.RELATIVE_PATH_METADATA))
            .map(metadataResource -> metadataResource.adaptTo(VeneeredResource.class));
    }

    @Override
    public ReplicationStatus getReplicationStatus() {
        return contentFragmentResource.adaptTo(ReplicationStatus.class);
    }
}
