package org.cid15.aem.veneer.api;

import com.day.cq.tagging.Tag;
import org.cid15.aem.veneer.api.link.Link;
import org.cid15.aem.veneer.api.link.builders.LinkBuilder;
import org.cid15.aem.veneer.api.page.VeneeredPage;
import org.cid15.aem.veneer.api.resource.VeneeredResource;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * An accessible instance (such as a <code>Resource</code> or <code>Page</code>) that supports hierarchy-based content
 * inheritance.
 */
public interface Inheritable {

    /**
     * Given a property on this resource containing the path of another resource, get the href to the resource, using
     * inheritance if the value does not exist on this resource.
     *
     * @param propertyName name of property containing a valid content path
     * @return <code>Optional</code> href
     */
    Optional<String> getAsHrefInherited(String propertyName);

    /**
     * Given a property on this resource containing the path of another resource, get a link to the resource, using
     * inheritance if the value does not exist on this resource.
     *
     * @param propertyName name of property containing a valid content path
     * @return <code>Optional</code> link
     */
    Optional<Link> getAsLinkInherited(String propertyName);

    /**
     * Given a property on this resource containing the path of another resource, get a link builder for the resource,
     * using inheritance if the value does not exist on this resource.
     *
     * @param propertyName name of property containing a valid content path
     * @return <code>Optional</code> link builder
     */
    Optional<LinkBuilder> getAsLinkBuilderInherited(String propertyName);

    /**
     * Get a multi-valued property from the current resource as a list of the given type, using inheritance if the value
     * does not exist on this resource.
     *
     * @param propertyName name of multi-valued property
     * @param type property type
     * @param <T> property type
     * @return list of property values or an empty list if the property does not exist
     */
    <T> List<T> getAsListInherited(String propertyName, Class<T> type);

    /**
     * Get a page from the value of the given property, using inheritance if the value does not exist on this resource.
     * The property value will be localized to the current page context before getting the page.
     *
     * @param propertyName property name
     * @return <code>Optional</code> page for property value
     */
    Optional<VeneeredPage> getAsPageInherited(String propertyName);

    /**
     * Get a multi-valued property from the current resource as a list of pages, using inheritance if the value does not
     * exist on this resource.
     *
     * @param propertyName name of property containing a list of page paths
     * @return list of pages, or empty list if either the property does not exist or pages do not exist for the property
     * values
     */
    List<VeneeredPage> getAsPageListInherited(String propertyName);

    /**
     * Get an <code>Optional</code> resource instance for a property on this resource containing the path of another
     * <code>Resource</code>, using inheritance if the value does not exist on this resource.
     *
     * @param propertyName name of property containing a resource path
     * @return <code>Optional</code> instance of the resource, or absent if either the property does not exist or
     * does not resolve to a resource
     */
    Optional<VeneeredResource> getAsResourceInherited(String propertyName);

    /**
     * Get a multi-valued property from the current resource as a list of resources, using inheritance if the value does
     * not exist on this resource.
     *
     * @param propertyName name of property containing a list of resource paths
     * @return list of resources, or empty list if either the property does not exist or does resolve to a list of
     * resources
     */
    List<VeneeredResource> getAsResourceListInherited(String propertyName);

    /**
     * Get an <code>Optional</code> type instance for a property on this resource containing the path of another
     * <code>Resource</code> in the repository, using inheritance if the value does not exist on this resource.
     *
     * @param propertyName name of property containing a resource path
     * @param type type to adapt from resource
     * @param <AdapterType> adapter class that is adaptable from <code>Resource</code>
     * @return <code>Optional</code> instance of the specified type, or absent if either the property does not exist or
     * the resource does not adapt to the provided type
     */
    <AdapterType> Optional<AdapterType> getAsTypeInherited(String propertyName, Class<AdapterType> type);

    /**
     * Get a multi-valued property from the current resource as a list of the given type, using inheritance if the value
     * does not exist on this resource.
     *
     * @param propertyName name of property containing a list of resource paths
     * @param type type to adapt from resource corresponding to each path value
     * @param <AdapterType> adapter class that is adaptable from <code>Resource</code>
     * @return list of the specified type, or empty list if either the property does not exist or the resource does not
     * adapt to the provided type
     */
    <AdapterType> List<AdapterType> getAsTypeListInherited(String propertyName, Class<AdapterType> type);

    /**
     * @param isSelf if true, resource will attempt to find the image reference property on the current resource
     * @return <code>Optional</code> inherited image reference
     */
    Optional<String> getImageReferenceInherited(boolean isSelf);

    /**
     * @return <code>Optional</code> inherited image reference
     */
    Optional<String> getImageReferenceInherited();

    /**
     * @param name image name
     * @return <code>Optional</code> inherited image reference
     */
    Optional<String> getImageReferenceInherited(String name);

    /**
     * Get a property value from the current resource. If no value is found, traverse up the content tree respective to
     * the page and relative resource path until a value is found.
     *
     * @param <T> result type
     * @param propertyName property to get
     * @param defaultValue value if no result is found
     * @return inherited value
     */
    <T> T getInherited(String propertyName, T defaultValue);

    /**
     * Get a property value from the current resource.   If no value is found, traverse up the content tree respective
     * to the page and relative resource path until a value is found, returning an absent <code>Optional</code> if not.
     * This returns the same value as the underlying <code>ValueMap</code> wrapped in an <code>Optional</code> instance
     * instead of returning null.
     *
     * @param propertyName property name
     * @param type property type
     * @param <T> type
     * @return <code>Optional</code> of the given type containing the property value or absent if no value is found
     */
    <T> Optional<T> getInherited(String propertyName, Class<T> type);

    /**
     * Get a list of tags for the default property name (cq:tags).  If no tags are found, traverse up the content tree
     * respective to the page and relative path until a value is found, returning an empty list if not.
     *
     * @return list of tags or empty list if not found
     */
    List<Tag> getTagsInherited();

    /**
     * Get a list of tags for the given property name.  If no tags are found, traverse up the content tree respective to
     * the page and relative path until a value is found, returning an empty list if not.
     *
     * @param propertyName name of property containing an array of tag IDs
     * @return list of tags or empty list if not found
     */
    List<Tag> getTagsInherited(String propertyName);

    /**
     * Get a child resource relative to the current resource, inheriting from a parent page if it does not exist.
     *
     * @param relativePath path relative to current resource
     * @return direct child resource if it exists, otherwise the child resource at this relative path for an ancestor
     * page
     */
    Optional<VeneeredResource> getResourceInherited(String relativePath);

    /**
     * Get the children of the current resource.  If the current resource has no children, inherit from an ancestor
     * page.
     *
     * @return list of resources representing children of the current resource or inherited from an ancestor page (or
     * empty list if none exist)
     */
    List<VeneeredResource> getResourcesInherited();

    /**
     * Get the children of the current resource meeting the predicate condition.  If the current resource has no
     * children, inherit from an ancestor page.
     *
     * @param predicate predicate used to filter resources
     * @return list of resources representing children of the current resource or inherited from an ancestor page (or
     * empty list if none exist)
     */
    List<VeneeredResource> getResourcesInherited(Predicate<VeneeredResource> predicate);

    /**
     * Get the children of a resource relative to the current resource. If resource does not exist relative to current
     * page, inherit from a parent page.
     *
     * @param relativePath path relative to current resource
     * @return list of resources representing children of the addressed resource or inherited from a parent page (or
     * empty list if none exist)
     */
    List<VeneeredResource> getResourcesInherited(String relativePath);

    /**
     * Get the children of a resource relative to the current resource meeting the predicate condition. If resource does
     * not exist relative to current page, inherit from a parent page.
     *
     * @param relativePath path relative to current resource
     * @param predicate predicate used to filter resources
     * @return list of resources representing children of the addressed resource or inherited from a parent page (or
     * empty list if none exist)
     */
    List<VeneeredResource> getResourcesInherited(String relativePath, Predicate<VeneeredResource> predicate);
}
