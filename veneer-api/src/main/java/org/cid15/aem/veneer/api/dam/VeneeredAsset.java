package org.cid15.aem.veneer.api.dam;

import org.cid15.aem.veneer.api.Replicable;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import com.day.cq.dam.api.Asset;

import java.util.Optional;

/**
 * Asset decorator to improve accessibility of metadata.
 */
public interface VeneeredAsset extends Replicable {

    /**
     * Get the underlying DAM asset.
     *
     * @return asset
     */
    Asset getAsset();

    /**
     * Get the metadata resource.
     *
     * @return metadata resource
     */
    Optional<VeneeredResource> getMetadataResource();
}
