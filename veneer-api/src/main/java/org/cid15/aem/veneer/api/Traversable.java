package org.cid15.aem.veneer.api;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Definition for hierarchical JCR resources that can be traversed.
 *
 * @param <T> type of traversable resource
 */
public interface Traversable<T> {

    /**
     * Find the first ancestor resource that matches the given predicate condition.
     *
     * @param predicate predicate to match ancestor resources against
     * @return <code>Optional</code> resource that matches the predicate condition
     */
    Optional<T> findAncestor(Predicate<T> predicate);

    /**
     * Find the first ancestor resource that matches the given predicate condition.
     *
     * @param predicate predicate to match ancestor resources against
     * @param excludeCurrentResource if true, the current resource will be excluded (i.e. even if the current resource
     * matches the predicate criteria, it will not be returned)
     * @return <code>Optional</code> resource that matches the predicate condition
     */
    Optional<T> findAncestor(Predicate<T> predicate, boolean excludeCurrentResource);

    /**
     * Find the first ancestor resource containing the given property name.
     *
     * @param propertyName property name to find on ancestor resources
     * @return <code>Optional</code> resource that contains the property
     */
    Optional<T> findAncestorWithProperty(String propertyName);

    /**
     * Find the first ancestor resource containing the given property name.
     *
     * @param propertyName property name to find on ancestor resources
     * @param excludeCurrentResource if true, the current resource will be excluded (i.e. even if the current resource
     * matches the predicate criteria, it will not be returned)
     * @return <code>Optional</code> resource that contains the property
     */
    Optional<T> findAncestorWithProperty(String propertyName, boolean excludeCurrentResource);

    /**
     * Find the first ancestor resource where the given property name has the specified value.
     *
     * @param propertyName property name to find on ancestor resources
     * @param propertyValue value of named property to match
     * @param <V> type of value
     * @return <code>Optional</code> resource that contains the property value
     */
    <V> Optional<T> findAncestorWithPropertyValue(String propertyName, V propertyValue);

    /**
     * Find the first ancestor resource where the given property name has the specified value.
     *
     * @param propertyName property name to find on ancestor resources
     * @param propertyValue value of named property to match
     * @param excludeCurrentResource if true, the current resource will be excluded (i.e. even if the current resource
     * matches the predicate criteria, it will not be returned)
     * @param <V> type of value
     * @return <code>Optional</code> resource that contains the property value
     */
    <V> Optional<T> findAncestorWithPropertyValue(String propertyName, V propertyValue, boolean excludeCurrentResource);

    /**
     * Get a list of descendant resources that match the given predicate condition.
     *
     * @param predicate predicate to match descendant resources against
     * @return list of resources that match the predicate condition or empty list if none exist
     */
    List<T> findDescendants(Predicate<T> predicate);
}
