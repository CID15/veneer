package org.cid15.aem.veneer.api.page;

import com.day.cq.commons.LabeledResource;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.adapter.Adaptable;
import org.cid15.aem.veneer.api.Accessible;
import org.cid15.aem.veneer.api.Inheritable;
import org.cid15.aem.veneer.api.Linkable;
import org.cid15.aem.veneer.api.Replicable;
import org.cid15.aem.veneer.api.Traversable;
import org.cid15.aem.veneer.api.link.Link;
import org.cid15.aem.veneer.api.link.builders.LinkBuilder;
import org.cid15.aem.veneer.api.page.enums.TitleType;
import org.cid15.aem.veneer.api.resource.VeneeredResource;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Decorates the WCM <code>Page</code> interface with additional convenience methods for traversing the content
 * hierarchy and getters for Veneer classes.
 */
public interface VeneeredPage extends Accessible, Inheritable, Linkable, Traversable<VeneeredPage>, Adaptable,
    LabeledResource, Replicable {

    /**
     * Get the underlying WCM page.
     *
     * @return page
     */
    Page getPage();

    /**
     * List children of the current page.
     *
     * @return iterator of child pages
     */
    Iterator<VeneeredPage> listChildren();

    /**
     * List child pages of the current page filtered using the given predicate.
     *
     * @param predicate predicate to filter pages
     * @return filtered iterator of child pages
     */
    Iterator<VeneeredPage> listChildren(Predicate<VeneeredPage> predicate);

    /**
     * List child pages of the current page filtered using the given predicate.
     *
     * @param predicate predicate to filter pages
     * @param deep if true, recursively iterate over all descendant pages
     * @return filtered iterator of child pages
     */
    Iterator<VeneeredPage> listChildren(Predicate<VeneeredPage> predicate, boolean deep);

    /**
     * Get the child page of the current page by name.
     *
     * @param name name of the child
     * @return page or absent <code>Optional</code>
     */
    Optional<VeneeredPage> getChild(String name);

    /**
     * Get the veneered resource for the "jcr:content" node for this page. If the page does not have a content node, an
     * "absent" Optional is returned.
     *
     * @return optional veneered resource for page content
     */
    Optional<VeneeredResource> getContentResource();

    /**
     * Get the veneered resource for the node at the given path relative to the "jcr:content" node for this page. If the
     * node does not exist, an "absent" Optional is returned.
     *
     * @param relativePath relative path to resource
     * @return optional veneered resource for resource relative to page content
     */
    Optional<VeneeredResource> getContentResource(String relativePath);

    /**
     * Get a link with a specified title type for this item.
     *
     * @param titleType type of title to set on link
     * @return link
     */
    Link getLink(TitleType titleType);

    /**
     * Get a link builder for the current page path.
     *
     * @param titleType type of title to set on builder
     * @return builder instance for this item
     */
    LinkBuilder getLinkBuilder(TitleType titleType);

    /**
     * Get a navigation link for this page containing an active state. The returned link will use the navigation title
     * as the link title, defaulting to the JCR title if it does not exist.
     *
     * @param isActive active state to be set on returned link
     * @return navigation link
     */
    Link getNavigationLink(boolean isActive);

    /**
     * Get the template path for this page. This method is preferred over getTemplate().getPath(), which is dependent on
     * access to /apps and will therefore fail in publish mode.
     *
     * @return value of cq:template property or empty string if none exists
     */
    String getTemplatePath();

    /**
     * Get the title with the given type for this page. If the title value is empty or non-existent, an absent
     * <code>Optional</code> is returned.
     *
     * @param titleType type of title to retrieve
     * @return title value or absent <code>Optional</code>
     */
    Optional<String> getTitle(TitleType titleType);

    /**
     * Returns the absolute parent page. If no page exists at that level,
     * <code>null</code> is returned.
     * <p>
     * Example (this path == /content/geometrixx/en/products)
     * <p>
     * <pre>
     * | level | returned                        |
     * |     0 | /content                        |
     * |     1 | /content/geometrixx             |
     * |     2 | /content/geometrixx/en          |
     * |     3 | /content/geometrixx/en/products |
     * |     4 | null                            |
     * </pre>
     *
     * @param level hierarchy level of the parent page to retrieve
     * @return the respective parent page or <code>null</code>
     */
    VeneeredPage getAbsoluteParent(int level);

    /**
     * Convenience method that returns the manager of this page.
     *
     * @return the page manager
     */
    VeneeredPageManager getVeneeredPageManager();

    /**
     * Returns the parent page if it's resource adapts to page.
     *
     * @return the parent page or <code>null</code>
     */
    VeneeredPage getParent();

    /**
     * Returns the relative parent page. If no page exists at that level,
     * <code>null</code> is returned.
     * <p>
     * Example (this path == /content/geometrixx/en/products)
     * <p>
     * <pre>
     * | level | returned                        |
     * |     0 | /content/geometrixx/en/products |
     * |     1 | /content/geometrixx/en          |
     * |     2 | /content/geometrixx             |
     * |     3 | /content                        |
     * |     4 | null                            |
     * </pre>
     *
     * @param level hierarchy level of the parent page to retrieve
     * @return the respective parent page or <code>null</code>
     */
    VeneeredPage getParent(int level);
}
