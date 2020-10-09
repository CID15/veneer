package org.cid15.aem.veneer.api.dam;

import com.adobe.cq.dam.cfm.ContentFragment;
import org.cid15.aem.veneer.api.Replicable;

/**
 * Content Fragment decorator to improve accessibility of fragment properties and metadata.
 */
public interface VeneeredContentFragment extends AssetContent, Replicable {

    /**
     * Get the underlying Content Fragment.
     *
     * @return content fragment
     */
    ContentFragment getContentFragment();
}
