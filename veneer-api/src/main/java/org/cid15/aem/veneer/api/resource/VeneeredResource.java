package org.cid15.aem.veneer.api.resource;

import org.cid15.aem.veneer.api.Accessible;
import org.cid15.aem.veneer.api.Inheritable;
import org.cid15.aem.veneer.api.Linkable;
import org.cid15.aem.veneer.api.Traversable;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents a veneered "component" resource in the JCR, typically an unstructured resource in the hierarchy of an AEM
 * page.
 */
public interface VeneeredResource extends Linkable, Accessible, Inheritable, Traversable<VeneeredResource>, Adaptable {

    /**
     * Get the underlying resource for this instance.
     *
     * @return current resource
     */
    Resource getResource();

    /**
     * Get the Resource Resolver for the underlying Sling resource.
     *
     * @return resource resolver
     */
    ResourceResolver getResourceResolver();

    /**
     * Get the underlying Sling resource type.
     *
     * @return resource type
     */
    String getResourceType();

    /**
     * Get the underlying Sling resource super type.
     *
     * @return resource super type
     */
    String getResourceSuperType();

    /**
     * Get the unique ID for this resource based on the path.  If this resource is the descendant of a page, the page
     * path will be removed from the identifier, since the relative path of a veneered resource is always unique for a
     * page.
     *
     * @return unique ID
     */
    String getId();

    /**
     * Get the index of this resource in relation to sibling resources.
     *
     * @return index in sibling resources or -1 if resource is null or has null parent resource
     */
    int getIndex();

    /**
     * Get the index of this resource in relation to sibling resources, ignoring resource types that do not match the
     * specified value.
     *
     * @param resourceType sling:resourceType to filter on
     * @return index in sibling resources or -1 if resource is null or has null parent resource
     */
    int getIndex(String resourceType);

    /**
     * Get the veneered resource for the resource at the given path relative to the current resource.
     *
     * @param relativePath relative path to component
     * @return <code>Optional</code> resource for component
     */
    Optional<VeneeredResource> getVeneeredResource(String relativePath);

    /**
     * Get a list of child resources for the current resource.
     *
     * @return list of veneered resources or empty list if none exist
     */
    List<VeneeredResource> getVeneeredResources();

    /**
     * Get a predicate-filtered list of child resources for the current resource.
     *
     * @param predicate predicate used to filter resources
     * @return list of veneered resources that meet the predicate criteria or empty list if none exist
     */
    List<VeneeredResource> getVeneeredResources(Predicate<VeneeredResource> predicate);

    /**
     * Get a list of child resources for the resource at the given path relative to this resource.
     *
     * @param relativePath relative path to parent of desired resources
     * @return list of veneered resources below the specified relative path or empty list if none exist
     */
    List<VeneeredResource> getVeneeredResources(String relativePath);

    /**
     * Get a list of child resources for the resource at the given path relative to this resource, returning only the
     * resources that have the specified resource type.
     *
     * @param relativePath relative path to parent of desired resources
     * @param resourceType sling:resourceType of resources to get from parent resource
     * @return list of veneered resources matching the given resource type below the specified relative path or empty
     * list if none exist
     */
    List<VeneeredResource> getVeneeredResources(String relativePath, String resourceType);

    /**
     * Get a list of child resources for the resource at the given path relative to this resource, returning only the
     * resources that meet the predicate criteria.
     *
     * @param relativePath relative path to parent of desired resources
     * @param predicate predicate used to filter resources
     * @return list of veneered resources that meet the predicate criteria below the specified relative path or empty
     * list if none exist
     */
    List<VeneeredResource> getVeneeredResources(String relativePath, Predicate<VeneeredResource> predicate);

    /**
     * Get the parent of this resource.
     *
     * @return parent veneered resource or absent optional if resource has no parent
     */
    Optional<VeneeredResource> getParent();
}
