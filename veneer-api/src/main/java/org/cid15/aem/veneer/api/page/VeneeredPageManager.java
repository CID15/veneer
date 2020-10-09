package org.cid15.aem.veneer.api.page;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;

import javax.jcr.query.Query;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Decorates the WCM <code>PageManager</code> interface with additional methods for finding pages using queries, tags,
 * and template paths.
 */
public interface VeneeredPageManager {

    /**
     * Get the underlying WCM page manager.
     *
     * @return page manager
     */
    PageManager getPageManager();

    /**
     * Find all descendant pages of the given path containing the specified tags.
     *
     * @param rootPath root path
     * @param tagIds set of tag IDs
     * @param matchOne if true, 'OR' the specified tag IDs, 'AND' otherwise
     * @return pages containing specified tags
     */
    List<VeneeredPage> findVeneeredPages(String rootPath, Collection<String> tagIds, boolean matchOne);

    /**
     * Find all descendant pages of the given path matching the template path.
     *
     * @param rootPath root path
     * @param templatePath template path
     * @return pages matching specified template
     */
    List<VeneeredPage> findVeneeredPages(String rootPath, String templatePath);

    /**
     * Find all descendant pages of the given path that match the predicate.
     *
     * @param rootPath root path
     * @param predicate predicate to determine if a page should be included in the result list
     * @return pages matching filter criteria
     */
    List<VeneeredPage> findVeneeredPages(String rootPath, Predicate<VeneeredPage> predicate);

    /**
     * Convenience method that returns the page at the given path. If the resource at that path does not exist or is not
     * adaptable to Page, <code>null</code> is returned.
     *
     * @param path path of the page
     * @return page or <code>null</code>
     */
    VeneeredPage getVeneeredPage(String path);

    /**
     * Decorate the given page.
     *
     * @param page non-null CQ page
     * @return decorated page
     */
    VeneeredPage getVeneeredPage(Page page);

    /**
     * Returns the page that contains this resource. If the resource is a page the resource is returned. Otherwise it
     * walks up the parent resources until a page is found.
     *
     * @param resource resource to find the page for
     * @return page or <code>null</code> if not found.
     */
    VeneeredPage getContainingVeneeredPage(Resource resource);

    /**
     * Returns the page that contains the resource at the given path. If the path addresses a page, that page is
     * returned. Otherwise it walks up the parent resources until a page is found.
     *
     * @param path path to find the page for
     * @return page or <code>null</code> if not found.
     */
    VeneeredPage getContainingVeneeredPage(String path);

    /**
     * Search for pages using a query.
     *
     * @param query JCR query
     * @return list of pages for the query result
     */
    List<VeneeredPage> search(Query query);

    /**
     * Search for pages using a query with the given result limit.
     *
     * @param query JCR query
     * @param limit result limit
     * @return list of pages for the query result
     */
    List<VeneeredPage> search(Query query, int limit);
}
