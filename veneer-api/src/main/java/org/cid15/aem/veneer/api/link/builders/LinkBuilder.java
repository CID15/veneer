package org.cid15.aem.veneer.api.link.builders;

import com.google.common.collect.SetMultimap;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.cid15.aem.veneer.api.link.Link;

import java.util.List;
import java.util.Map;

/**
 * Builder for creating <code>Link</code>, <code>ImageLink</code>, and <code>NavigationLink</code> objects.
 */
public interface LinkBuilder {

    /**
     * Build a link using the properties of the current builder.
     *
     * @return link
     */
    Link build();

    /**
     * Use the given request (and underlying resource resolver) to map the link path.
     *
     * @param request request
     * @return builder
     */
    LinkBuilder mapped(SlingHttpServletRequest request);

    /**
     * Use the given resource resolver to map the link path.
     *
     * @param resourceResolver resource resolver
     * @return builder
     */
    LinkBuilder mapped(ResourceResolver resourceResolver);

    /**
     * Add a child link.
     *
     * @param child child link instance
     * @return builder
     */
    LinkBuilder addChild(Link child);

    /**
     * Add a query parameter.
     *
     * @param name parameter name
     * @param value parameter value
     * @return builder
     */
    LinkBuilder addParameter(String name, String value);

    /**
     * Add query parameters.
     *
     * @param parameters map of parameter names to their values
     * @return builder
     */
    LinkBuilder addParameters(Map<String, String> parameters);

    /**
     * Add query parameters.
     *
     * @param parameters map of parameter names to their values
     * @return builder
     */
    LinkBuilder addParameters(SetMultimap<String, String> parameters);

    /**
     * Add properties (map of properties name-value pairs that are stored on the returned link instance).
     *
     * @param properties map of properties names to their values
     * @return builder
     */
    LinkBuilder addProperties(Map<String, String> properties);

    /**
     * Add a property (arbitrary name-value pair stored on the returned link instance).
     *
     * @param name property name
     * @param value property value
     * @return builder
     */
    LinkBuilder addProperty(String name, String value);

    /**
     * Add a selector.
     *
     * @param selector selector value
     * @return builder
     */
    LinkBuilder addSelector(String selector);

    /**
     * Add selectors.
     *
     * @param selectors list of selector values
     * @return builder
     */
    LinkBuilder addSelectors(List<String> selectors);

    /**
     * Set the active state for the link.
     *
     * @param isActive active state
     * @return builder
     */
    LinkBuilder setActive(boolean isActive);

    /**
     * Set the extension, without '.'.  Defaults to "html" if none is provided.
     *
     * @param extension link extension
     * @return builder
     */
    LinkBuilder setExtension(String extension);

    /**
     * Do not set an extension for this link.
     *
     * @return builder
     */
    LinkBuilder noExtension();

    /**
     * Set whether the link should be considered external, i.e. not a valid content path.
     *
     * @param isExternal if true, link is marked as external
     * @return builder
     */
    LinkBuilder setExternal(boolean isExternal);

    /**
     * Set the host.  If the host is set, the href of the built link will be absolute rather than relative.
     *
     * @param host host name
     * @return builder
     */
    LinkBuilder setHost(String host);

    /**
     * Set the scheme (e.g. "ftp" or "tel").
     * <p>
     * If set, the scheme will override the default value of "http" (or "https" if secure=true).
     *
     * @param scheme scheme
     * @return builder
     */
    LinkBuilder setScheme(String scheme);

    /**
     * Set whether the link URI is opaque (an absolute URI whose scheme-specific part does not begin with a slash
     * character).
     *
     * @param isOpaque if true, ":" instead of "://" will be appended to the scheme for external links (e.g. "mailto:"
     * links)
     * @return builder
     */
    LinkBuilder setOpaque(boolean isOpaque);

    /**
     * Set the port.
     *
     * @param port port number
     * @return builder
     */
    LinkBuilder setPort(int port);

    /**
     * Set secure.  If true, the returned link will be "https" instead of "http".  This only applies when a host name is
     * set.
     *
     * @param isSecure secure
     * @return builder
     */
    LinkBuilder setSecure(boolean isSecure);

    /**
     * Set the suffix.
     *
     * @param suffix suffix
     * @return builder
     */
    LinkBuilder setSuffix(String suffix);

    /**
     * Set the link target.
     *
     * @param target link target
     * @return builder
     */
    LinkBuilder setTarget(String target);

    /**
     * Set the link title.
     *
     * @param title title
     * @return builder
     */
    LinkBuilder setTitle(String title);

    /**
     * Set the link fragment.
     *
     * @param fragment fragment
     * @return builder
     */
    LinkBuilder setFragment(String fragment);
}
