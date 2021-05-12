package org.cid15.aem.veneer.api.link;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A link represents the attributes that compose a URL with additional title and target properties to encapsulate the
 * typical attributes of an HTML anchor tag.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(isGetterVisibility = Visibility.NONE)
public interface Link extends Serializable {

    /**
     * @return extension or empty optional if link is external
     */
    Optional<String> getExtension();

    /**
     * @return href (path with extension)
     */
    String getHref();

    /**
     * @return path
     */
    String getPath();

    /**
     * @return property map
     */
    Map<String, String> getProperties();

    /**
     * @return query string starting with '?' or empty optional if no parameters present
     */
    Optional<String> getQueryString();

    /**
     * @return list of selector values or empty list if none exist
     */
    List<String> getSelectors();

    /**
     * @return the URL fragment or empty optional if not provided
     */
    Optional<String> getFragment();

    /**
     * @return suffix or empty optional if not provided
     */
    Optional<String> getSuffix();

    /**
     * @return link target
     */
    String getTarget();

    /**
     * @return link title
     */
    String getTitle();

    /**
     * @return if href is to an external URL
     */
    boolean isExternal();

    /**
     * List of child links. Useful for building navigation hierarchies.
     *
     * @return list of child links for this link or empty list if none exist
     */
    List<Link> getChildren();

    /**
     * @return true if link is active
     */
    boolean isActive();
}
