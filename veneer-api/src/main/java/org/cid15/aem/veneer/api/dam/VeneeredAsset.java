package org.cid15.aem.veneer.api.dam;

import com.day.cq.dam.api.Asset;
import org.cid15.aem.veneer.api.Replicable;

/**
 * Asset decorator to improve accessibility of metadata.
 */
public interface VeneeredAsset extends AssetContent, Replicable {

    /**
     * Get the underlying DAM asset.
     *
     * @return asset
     */
    Asset getAsset();
}
