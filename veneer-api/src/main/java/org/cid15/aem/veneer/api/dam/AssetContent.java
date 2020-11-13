package org.cid15.aem.veneer.api.dam;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationStatus;
import org.apache.sling.api.resource.Resource;
import org.cid15.aem.veneer.api.Replicable;
import org.cid15.aem.veneer.api.constants.PathConstants;
import org.cid15.aem.veneer.api.resource.VeneeredResource;

import java.util.Optional;

/**
 * Content definition for assets and content fragments.
 */
public interface AssetContent extends Replicable {

    /**
     * Get the underlying resource for this asset.
     *
     * @return resource
     */
    Resource getResource();

    /**
     * Get the jcr:content resource.
     *
     * @return content resource
     */
    default Optional<VeneeredResource> getContentResource() {
        return Optional.ofNullable(getResource().getChild(JcrConstants.JCR_CONTENT))
            .map(contentResource -> contentResource.adaptTo(VeneeredResource.class));
    }

    /**
     * Get the resource for the given relative path.
     *
     * @return resource
     */
    default Optional<VeneeredResource> getContentResource(final String relativePath) {
        return Optional.ofNullable(getResource().getChild(relativePath))
            .map(contentResource -> contentResource.adaptTo(VeneeredResource.class));
    }

    /**
     * Get the metadata resource.
     *
     * @return metadata resource
     */
    default Optional<VeneeredResource> getMetadataResource() {
        return Optional.ofNullable(getResource().getChild(PathConstants.RELATIVE_PATH_METADATA))
            .map(metadataResource -> metadataResource.adaptTo(VeneeredResource.class));
    }

    @Override
    default ReplicationStatus getReplicationStatus() {
        return getResource().adaptTo(ReplicationStatus.class);
    }
}
