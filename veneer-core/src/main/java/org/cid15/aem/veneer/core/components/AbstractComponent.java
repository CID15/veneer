package org.cid15.aem.veneer.core.components;

import com.day.cq.tagging.Tag;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.cid15.aem.veneer.api.link.Link;
import org.cid15.aem.veneer.api.link.builders.LinkBuilder;
import org.cid15.aem.veneer.api.page.VeneeredPage;
import org.cid15.aem.veneer.api.resource.VeneeredResource;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

/**
 * Base class for AEM component classes.
 */
@JsonAutoDetect(fieldVisibility = NONE, getterVisibility = NONE, isGetterVisibility = NONE)
public abstract class AbstractComponent implements VeneeredResource {

    @Inject
    private VeneeredResource veneeredResource;

    @Override
    public final <AdapterType> AdapterType adaptTo(final Class<AdapterType> type) {
        return veneeredResource.adaptTo(type);
    }

    @Override
    public final String getHref() {
        return veneeredResource.getHref();
    }

    @Override
    public final Optional<String> getAsHrefInherited(final String propertyName) {
        return veneeredResource.getAsHrefInherited(propertyName);
    }

    @Override
    public final Link getLink() {
        return veneeredResource.getLink();
    }

    @Override
    public final ValueMap getProperties() {
        return veneeredResource.getProperties();
    }

    @Override
    public final <T> T get(final String propertyName, final T defaultValue) {
        return veneeredResource.get(propertyName, defaultValue);
    }

    @Override
    public final Optional<VeneeredResource> getResource(final String relativePath) {
        return veneeredResource.getResource(relativePath);
    }

    @Override
    public final List<VeneeredResource> getResources() {
        return veneeredResource.getResources();
    }

    @Override
    public final LinkBuilder getLinkBuilder() {
        return veneeredResource.getLinkBuilder();
    }

    @Override
    public final <T> Optional<T> get(final String propertyName, final Class<T> type) {
        return veneeredResource.get(propertyName, type);
    }

    @Override
    public final String getResourceType() {
        return veneeredResource.getResourceType();
    }

    @Override
    public final String getResourceSuperType() {
        return veneeredResource.getResourceSuperType();
    }

    @Override
    public final String getId() {
        return veneeredResource.getId();
    }

    @Override
    public final List<VeneeredResource> getResources(final Predicate<VeneeredResource> predicate) {
        return veneeredResource.getResources(predicate);
    }

    @Override
    public final boolean isHasImage() {
        return veneeredResource.isHasImage();
    }

    @Override
    public final int getIndex() {
        return veneeredResource.getIndex();
    }

    @Override
    public final boolean isHasImage(final String name) {
        return veneeredResource.isHasImage(name);
    }

    @Override
    public final List<Tag> getTags() {
        return veneeredResource.getTags();
    }

    @Override
    public final List<VeneeredResource> getResources(final String relativePath) {
        return veneeredResource.getResources(relativePath);
    }

    @Override
    public final Optional<String> getAsHref(final String propertyName) {
        return veneeredResource.getAsHref(propertyName);
    }

    @Override
    public final int getIndex(final String resourceType) {
        return veneeredResource.getIndex(resourceType);
    }

    @Override
    public final List<VeneeredResource> getResources(final String relativePath, final String resourceType) {
        return veneeredResource.getResources(relativePath, resourceType);
    }

    @Override
    public final String getName() {
        return veneeredResource.getName();
    }

    @Override
    public final String getPath() {
        return veneeredResource.getPath();
    }

    @Override
    public final List<VeneeredResource> getResources(final String relativePath,
        final Predicate<VeneeredResource> predicate) {
        return veneeredResource.getResources(relativePath, predicate);
    }

    @Override
    public final Resource getResource() {
        return veneeredResource.getResource();
    }

    @Override
    public final ResourceResolver getResourceResolver() {
        return veneeredResource.getResourceResolver();
    }

    @Override
    public final Optional<Link> getAsLinkInherited(final String propertyName) {
        return veneeredResource.getAsLinkInherited(propertyName);
    }

    @Override
    public final Optional<LinkBuilder> getAsLinkBuilderInherited(final String propertyName) {
        return veneeredResource.getAsLinkBuilderInherited(propertyName);
    }

    @Override
    public final Optional<VeneeredResource> getResourceInherited(final String relativePath) {
        return veneeredResource.getResourceInherited(relativePath);
    }

    @Override
    public final List<VeneeredResource> getResourcesInherited() {
        return veneeredResource.getResourcesInherited();
    }

    @Override
    public final List<VeneeredResource> getResourcesInherited(final Predicate<VeneeredResource> predicate) {
        return veneeredResource.getResourcesInherited(predicate);
    }

    @Override
    public final List<VeneeredResource> getResourcesInherited(final String relativePath) {
        return veneeredResource.getResourcesInherited(relativePath);
    }

    @Override
    public final List<VeneeredResource> getResourcesInherited(final String relativePath,
        final Predicate<VeneeredResource> predicate) {
        return veneeredResource.getResourcesInherited(relativePath, predicate);
    }

    @Override
    public final VeneeredResource getParent() {
        return veneeredResource.getParent();
    }

    @Override
    public final Optional<VeneeredPage> getContainingPage() {
        return veneeredResource.getContainingPage();
    }

    @Override
    public final Optional<Link> getAsLink(final String propertyName) {
        return veneeredResource.getAsLink(propertyName);
    }

    @Override
    public final Optional<LinkBuilder> getAsLinkBuilder(final String propertyName) {
        return veneeredResource.getAsLinkBuilder(propertyName);
    }

    @Override
    public final <T> List<T> getAsListInherited(final String propertyName, final Class<T> type) {
        return veneeredResource.getAsListInherited(propertyName, type);
    }

    @Override
    public final Optional<VeneeredPage> getAsPageInherited(final String propertyName) {
        return veneeredResource.getAsPageInherited(propertyName);
    }

    @Override
    public final List<VeneeredPage> getAsPageListInherited(final String propertyName) {
        return veneeredResource.getAsPageListInherited(propertyName);
    }

    @Override
    public final Optional<VeneeredResource> getAsResourceInherited(final String propertyName) {
        return veneeredResource.getAsResourceInherited(propertyName);
    }

    @Override
    public final List<VeneeredResource> getAsResourceListInherited(final String propertyName) {
        return veneeredResource.getAsResourceListInherited(propertyName);
    }

    @Override
    public final <T> List<T> getAsList(final String propertyName, final Class<T> type) {
        return veneeredResource.getAsList(propertyName, type);
    }

    @Override
    public final <AdapterType> Optional<AdapterType> getAsTypeInherited(final String propertyName,
        final Class<AdapterType> type) {
        return veneeredResource.getAsTypeInherited(propertyName, type);
    }

    @Override
    public final <AdapterType> List<AdapterType> getAsTypeListInherited(final String propertyName,
        final Class<AdapterType> type) {
        return veneeredResource.getAsTypeListInherited(propertyName, type);
    }

    @Override
    public final Optional<String> getImageReferenceInherited(final boolean isSelf) {
        return veneeredResource.getImageReferenceInherited(isSelf);
    }

    @Override
    public final Optional<VeneeredPage> getAsPage(final String propertyName) {
        return veneeredResource.getAsPage(propertyName);
    }

    @Override
    public final List<VeneeredPage> getAsPageList(final String propertyName) {
        return veneeredResource.getAsPageListInherited(propertyName);
    }

    @Override
    public final Optional<VeneeredResource> getAsResource(final String propertyName) {
        return veneeredResource.getAsResource(propertyName);
    }

    @Override
    public final List<VeneeredResource> getAsResourceList(final String propertyName) {
        return veneeredResource.getAsResourceList(propertyName);
    }

    @Override
    public final Optional<String> getImageReferenceInherited() {
        return veneeredResource.getImageReferenceInherited();
    }

    @Override
    public final Optional<String> getImageReferenceInherited(final String name) {
        return veneeredResource.getImageReferenceInherited(name);
    }

    @Override
    public final <AdapterType> Optional<AdapterType> getAsType(final String propertyName,
        final Class<AdapterType> type) {
        return veneeredResource.getAsType(propertyName, type);
    }

    @Override
    public final <AdapterType> List<AdapterType> getAsTypeList(final String propertyName,
        final Class<AdapterType> type) {
        return veneeredResource.getAsTypeList(propertyName, type);
    }

    @Override
    public final Optional<String> getImageReference(final boolean isSelf) {
        return veneeredResource.getImageReference(isSelf);
    }

    @Override
    public final Optional<String> getImageReference() {
        return veneeredResource.getImageReference();
    }

    @Override
    public final Optional<String> getImageReference(final String name) {
        return veneeredResource.getImageReference(name);
    }

    @Override
    public final Optional<String> getImageRendition(final String renditionName) {
        return veneeredResource.getImageRendition(renditionName);
    }

    @Override
    public final Optional<String> getImageRendition(final String name, final String renditionName) {
        return veneeredResource.getImageRendition(name, renditionName);
    }

    @Override
    public final List<Tag> getTags(final String propertyName) {
        return veneeredResource.getTags(propertyName);
    }

    @Override
    public final <T> T getInherited(final String propertyName, final T defaultValue) {
        return veneeredResource.getInherited(propertyName, defaultValue);
    }

    @Override
    public final <T> Optional<T> getInherited(final String propertyName, final Class<T> type) {
        return veneeredResource.getInherited(propertyName, type);
    }

    @Override
    public final List<Tag> getTagsInherited() {
        return veneeredResource.getTagsInherited();
    }

    @Override
    public final List<Tag> getTagsInherited(final String propertyName) {
        return veneeredResource.getTagsInherited(propertyName);
    }

    @Override
    public final Optional<VeneeredResource> findAncestor(final Predicate<VeneeredResource> predicate) {
        return veneeredResource.findAncestor(predicate);
    }

    @Override
    public final Optional<VeneeredResource> findAncestor(final Predicate<VeneeredResource> predicate,
        final boolean excludeCurrentResource) {
        return veneeredResource.findAncestor(predicate, excludeCurrentResource);
    }

    @Override
    public final Optional<VeneeredResource> findAncestorWithProperty(final String propertyName) {
        return veneeredResource.findAncestorWithProperty(propertyName);
    }

    @Override
    public final Optional<VeneeredResource> findAncestorWithProperty(final String propertyName,
        final boolean excludeCurrentResource) {
        return veneeredResource.findAncestorWithProperty(propertyName, excludeCurrentResource);
    }

    @Override
    public final <V> Optional<VeneeredResource> findAncestorWithPropertyValue(final String propertyName,
        final V propertyValue) {
        return veneeredResource.findAncestorWithPropertyValue(propertyName, propertyValue);
    }

    @Override
    public final <V> Optional<VeneeredResource> findAncestorWithPropertyValue(final String propertyName,
        final V propertyValue, final boolean excludeCurrentResource) {
        return veneeredResource.findAncestorWithPropertyValue(propertyName, propertyValue, excludeCurrentResource);
    }

    @Override
    public final List<VeneeredResource> findDescendants(final Predicate<VeneeredResource> predicate) {
        return veneeredResource.findDescendants(predicate);
    }
}
