package org.cid15.aem.veneer.api.dam;

import com.adobe.cq.dam.cfm.ContentFragment;
import org.cid15.aem.veneer.api.Replicable;
import org.cid15.aem.veneer.api.resource.VeneeredResource;

import java.util.Optional;

/**
 * Content Fragment decorator to improve accessibility of fragment properties and metadata.
 */
public interface VeneeredContentFragment extends Replicable {

    /**
     * Get the underlying Content Fragment.
     *
     * @return content fragment
     */
    ContentFragment getContentFragment();

    /**
     * Get the jcr:content resource.
     *
     * @return content resource
     */
    Optional<VeneeredResource> getContentResource();

    /**
     * Get the metadata resource.
     *
     * @return metadata resource
     */
    Optional<VeneeredResource> getMetadataResource();
}
