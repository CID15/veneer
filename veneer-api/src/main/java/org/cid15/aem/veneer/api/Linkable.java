package org.cid15.aem.veneer.api;

import org.cid15.aem.veneer.api.link.Link;
import org.cid15.aem.veneer.api.link.builders.LinkBuilder;

/**
 * Definition for "linkable" items, such as pages and components (i.e. path-addressable resources).
 */
public interface Linkable {

    /**
     * Get the page or resource name.
     *
     * @return JCR name
     */
    String getName();

    /**
     * Get the page or resource path.
     *
     * @return JCR path
     */
    String getPath();

    /**
     * Get the URL for this item.
     *
     * @return href
     */
    String getHref();

    /**
     * Get a link for this item.
     *
     * @return link
     */
    Link getLink();

    /**
     * Get a link builder for the current resource path.
     *
     * @return builder instance for this item
     */
    LinkBuilder getLinkBuilder();
}
