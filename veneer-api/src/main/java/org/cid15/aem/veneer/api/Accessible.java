package org.cid15.aem.veneer.api;

import com.day.cq.tagging.Tag;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.cid15.aem.veneer.api.link.Link;
import org.cid15.aem.veneer.api.link.builders.LinkBuilder;
import org.cid15.aem.veneer.api.page.VeneeredPage;

import java.util.List;
import java.util.Optional;

/**
 * Definition of accessor methods for content resources for <code>VeneeredResource</code> and <code>VeneeredPage</code>
 * instances.
 */
public interface Accessible {

    /**
     * Get a map of properties for the current resource.
     *
     * @return map of property names to values, or empty map if underlying resource is null or nonexistent
     */
    ValueMap getProperties();

    /**
     * Get a property value from the current resource, returning the default value if the property does not exist.
     *
     * @param <T> property type
     * @param propertyName property name
     * @param defaultValue default value
     * @return property value or default value if it does not exist
     */
    <T> T get(String propertyName, T defaultValue);

    /**
     * Get a property value from the current resource.  This returns the same value as the underlying
     * <code>ValueMap</code> wrapped in an <code>Optional</code> instance instead of returning null.
     *
     * @param propertyName property name
     * @param type property type
     * @param <T> property type
     * @return <code>Optional</code> of the given type containing the property value or absent if the property does not
     * exist
     */
    <T> Optional<T> get(String propertyName, Class<T> type);

    /**
     * Given a property on this resource containing the path of another resource, get an <code>Optional</code>
     * containing the href to the resource (i.e. the content path with ".html" appended).
     *
     * @param propertyName name of property containing a valid content path
     * @return href value wrapped in an <code>Optional</code>
     */
    Optional<String> getAsHref(String propertyName);

    /**
     * Given a property on this resource containing the path of another resource, get a link to the resource.
     *
     * @param propertyName name of property containing a valid content path
     * @return <code>Optional</code> link
     */
    Optional<Link> getAsLink(String propertyName);

    /**
     * Given a property on this resource containing the path of another resource, get a link builder for the resource.
     *
     * @param propertyName name of property containing a valid content path
     * @return <code>Optional</code> link builder
     */
    Optional<LinkBuilder> getAsLinkBuilder(String propertyName);

    /**
     * Get a multi-valued property from the current resource as a list of the given type.
     *
     * @param propertyName name of multi-valued property
     * @param type property type
     * @param <T> property type, must be supported by <code>ValueMap</code>
     * @return list of property values or an empty list if the property does not exist
     */
    <T> List<T> getAsList(String propertyName, Class<T> type);

    /**
     * Get a page instance from the value of the given property.  Will return an absent <code>Optional</code> if the
     * path value for the given property name does not resolve to a valid CQ page.
     *
     * @param propertyName property name
     * @return <code>Optional</code> page for property value
     */
    Optional<VeneeredPage> getAsVeneeredPage(String propertyName);

    /**
     * Get a list of pages from the value of the given property.  Pages will only be returned in the list if the paths
     * resolve to a non-null page.
     *
     * @param propertyName property name
     * @return list of pages for property value
     */
    List<VeneeredPage> getAsVeneeredPageList(String propertyName);

    /**
     * Get an <code>Optional</code> resource instance for a property on this resource containing the path of another
     * <code>Resource</code>.
     *
     * @param propertyName name of property containing a resource path
     * @return <code>Optional</code> instance of the resource, or absent if either the property does not exist or
     * does not resolve to a resource
     */
    Optional<Resource> getAsResource(String propertyName);

    /**
     * Get a list of resource instances for a property on this resource containing an array of paths to other
     * <code>Resource</code>s.
     *
     * @param propertyName name of property containing resource paths
     * @return list of resources, or empty list if either the property does not exist or the resources for the paths do
     * not resolve
     */
    List<Resource> getAsResourceList(String propertyName);

    /**
     * Get an <code>Optional</code> type instance for a property on this resource containing the path of another
     * <code>Resource</code>.
     *
     * @param propertyName name of property containing a resource path
     * @param type type to adapt from resource
     * @param <AdapterType> adapter class that is adaptable from <code>Resource</code>
     * @return <code>Optional</code> instance of the specified type, or absent if either the property does not exist or
     * the resource does not adapt to the provided type
     */
    <AdapterType> Optional<AdapterType> getAsType(String propertyName, Class<AdapterType> type);

    /**
     * Get a list of type instances for a property on this resource containing an array of paths to other
     * <code>Resource</code>s.
     *
     * @param propertyName name of property containing resource paths
     * @param type type to adapt from resource
     * @param <AdapterType> adapter class that is adaptable from <code>Resource</code>
     * @return list of instances of the specified type, or empty list if either the property does not exist or the
     * resources for the paths do not adapt to the provided type
     */
    <AdapterType> List<AdapterType> getAsTypeList(String propertyName, Class<AdapterType> type);

    /**
     * Get the referenced DAM asset path for the default image (named "image") for this resource.
     *
     * @param isSelf if true, image reference property will be accessed from the current resource rather than a child
     * @return <code>Optional</code> image reference path
     */
    Optional<String> getImageReference(boolean isSelf);

    /**
     * Get the referenced DAM asset path for the default image (named "image") for this resource.
     *
     * @return <code>Optional</code> image reference path
     */
    Optional<String> getImageReference();

    /**
     * @param name image name
     * @return <code>Optional</code> image reference path
     */
    Optional<String> getImageReference(String name);

    /**
     * Get the DAM asset rendition path for the default image (named "image") for this resource.
     *
     * @param renditionName rendition name for this asset (e.g. "cq5dam.thumbnail.140.100.png")
     * @return <code>Optional</code> image rendition path
     */
    Optional<String> getImageRendition(String renditionName);

    /**
     * @param name image name
     * @param renditionName rendition name for this asset
     * @return <code>Optional</code> image rendition path
     */
    Optional<String> getImageRendition(String name, String renditionName);

    /**
     * Check if the current resource has a default image.
     *
     * @return true if image has content
     */
    boolean isHasImage();

    /**
     * Check if the current resource has a named image.
     *
     * @param name image name (name of image as defined in dialog)
     * @return true if image has content
     */
    boolean isHasImage(String name);

    /**
     * Get a list of tags for the default tag property name (cq:tags).
     *
     * @return list of tags or empty list if not found
     */
    List<Tag> getTags();

    /**
     * Get a list of tags for the given property name.
     *
     * @param propertyName name of property containing an array of tag IDs
     * @return list of tags or empty list if not found
     */
    List<Tag> getTags(String propertyName);
}
