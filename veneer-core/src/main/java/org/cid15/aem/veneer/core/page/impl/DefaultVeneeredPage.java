package org.cid15.aem.veneer.core.page.impl;

import com.day.cq.commons.Filter;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.commons.DeepResourceIterator;
import com.google.common.base.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.cid15.aem.veneer.api.Accessible;
import org.cid15.aem.veneer.api.Inheritable;
import org.cid15.aem.veneer.api.link.Link;
import org.cid15.aem.veneer.api.link.builders.LinkBuilder;
import org.cid15.aem.veneer.api.page.VeneeredPage;
import org.cid15.aem.veneer.api.page.VeneeredPageManager;
import org.cid15.aem.veneer.api.page.enums.TitleType;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.cid15.aem.veneer.core.link.builders.factory.LinkBuilderFactory;
import org.cid15.aem.veneer.core.resource.predicates.VeneeredResourcePropertyExistsPredicate;
import org.cid15.aem.veneer.core.resource.predicates.VeneeredResourcePropertyValuePredicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class DefaultVeneeredPage implements VeneeredPage {

    private static final Filter<Page> ALL_PAGES = page -> true;

    private final Page delegate;

    private final Optional<VeneeredResource> veneeredResource;

    public DefaultVeneeredPage(final Page delegate) {
        this.delegate = delegate;

        veneeredResource = Optional.ofNullable(delegate.getContentResource())
            .map(resource -> resource.adaptTo(VeneeredResource.class));
    }

    @Override
    public boolean equals(final Object other) {
        return new EqualsBuilder()
            .append(delegate.getPath(), ((VeneeredPage) other).getPage().getPath())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(delegate.getPath())
            .hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("path", delegate.getPath())
            .add("title", delegate.getTitle())
            .toString();
    }

    @Override
    public Page getPage() {
        return delegate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <AdapterType> AdapterType adaptTo(final Class<AdapterType> type) {
        final AdapterType result;

        if (type == Page.class) {
            result = (AdapterType) delegate;
        } else if (type == VeneeredResource.class) {
            result = (AdapterType) delegate.adaptTo(Resource.class).adaptTo(VeneeredResource.class);
        } else {
            result = delegate.adaptTo(type);
        }

        return result;
    }

    @Override
    public Optional<VeneeredPage> findAncestor(final Predicate<VeneeredPage> predicate) {
        return findAncestor(predicate, false);
    }

    @Override
    public Optional<VeneeredPage> findAncestor(final Predicate<VeneeredPage> predicate,
        final boolean excludeCurrentResource) {
        VeneeredPage page = excludeCurrentResource ? getParent() : this;
        VeneeredPage ancestorPage = null;

        while (page != null) {
            if (predicate.test(page)) {
                ancestorPage = page;
                break;
            } else {
                page = page.getParent();
            }
        }

        return Optional.ofNullable(ancestorPage);
    }

    @Override
    public Optional<VeneeredPage> findAncestorWithProperty(final String propertyName) {
        return findAncestorForPredicate(new VeneeredResourcePropertyExistsPredicate(propertyName), false);
    }

    @Override
    public Optional<VeneeredPage> findAncestorWithProperty(final String propertyName,
        final boolean excludeCurrentResource) {
        return findAncestorForPredicate(new VeneeredResourcePropertyExistsPredicate(propertyName),
            excludeCurrentResource);
    }

    @Override
    public <V> Optional<VeneeredPage> findAncestorWithPropertyValue(final String propertyName, final V propertyValue) {
        return findAncestorForPredicate(new VeneeredResourcePropertyValuePredicate<>(propertyName, propertyValue),
            false);
    }

    @Override
    public <V> Optional<VeneeredPage> findAncestorWithPropertyValue(final String propertyName, final V propertyValue,
        final boolean excludeCurrentResource) {
        return findAncestorForPredicate(new VeneeredResourcePropertyValuePredicate<>(propertyName, propertyValue),
            excludeCurrentResource);
    }

    @Override
    public List<VeneeredPage> findDescendants(final Predicate<VeneeredPage> predicate) {
        final List<VeneeredPage> pages = new ArrayList<>();

        final VeneeredPageManager pageManager = getVeneeredPageManager();

        final Iterator<Page> iterator = delegate.listChildren(ALL_PAGES, true);

        while (iterator.hasNext()) {
            final VeneeredPage page = pageManager.getVeneeredPage(iterator.next());

            if (predicate.test(page)) {
                pages.add(page);
            }
        }

        return pages;
    }

    @Override
    public ValueMap getProperties() {
        return getVeneeredResource()
            .map(Accessible :: getProperties)
            .orElse(ValueMap.EMPTY);
    }

    @Override
    public <T> T get(final String propertyName, final T defaultValue) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.get(propertyName, defaultValue))
            .orElse(defaultValue);
    }

    @Override
    public <T> Optional<T> get(final String propertyName, final Class<T> type) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.get(propertyName, type));
    }

    @Override
    public Optional<String> getAsHref(final String propertyName) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsHref(propertyName));
    }

    @Override
    public Optional<String> getAsHref(final String propertyName, final boolean strict) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsHref(propertyName, strict));
    }

    @Override
    public Optional<String> getAsHref(final String propertyName, final boolean strict, final boolean mapped) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsHref(propertyName, strict,
            mapped));
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsHrefInherited(propertyName));
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName, final boolean strict) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsHrefInherited(propertyName,
            strict));
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName, final boolean strict, final boolean mapped) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsHrefInherited(propertyName,
            strict, mapped));
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsLinkInherited(propertyName));
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsLinkInherited(propertyName,
            strict));
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict, final boolean mapped) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsLinkInherited(propertyName,
            strict, mapped));
    }

    @Override
    public <T> List<T> getAsListInherited(final String propertyName, final Class<T> type) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getAsListInherited(propertyName, type))
            .orElse(Collections.emptyList());
    }

    @Override
    public Optional<VeneeredPage> getAsVeneeredPageInherited(final String propertyName) {
        return getVeneeredResource().flatMap(
            veneeredResource -> veneeredResource.getAsVeneeredPageInherited(propertyName));
    }

    @Override
    public List<VeneeredPage> getAsVeneeredPageListInherited(final String propertyName) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getAsVeneeredPageListInherited(propertyName))
            .orElse(Collections.emptyList());
    }

    @Override
    public Optional<Resource> getAsResourceInherited(final String propertyName) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource
            .getAsResourceInherited(propertyName));
    }

    @Override
    public List<Resource> getAsResourceListInherited(final String propertyName) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getAsResourceListInherited(propertyName))
            .orElse(Collections.emptyList());
    }

    @Override
    public <AdapterType> Optional<AdapterType> getAsTypeInherited(final String propertyName,
        final Class<AdapterType> type) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource
            .getAsTypeInherited(propertyName, type));
    }

    @Override
    public <AdapterType> List<AdapterType> getAsTypeListInherited(final String propertyName,
        final Class<AdapterType> type) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getAsTypeListInherited(propertyName, type))
            .orElse(Collections.emptyList());
    }

    @Override
    public String getTitle() {
        return delegate.getTitle();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getPath() {
        return delegate.getPath();
    }

    @Override
    public String getHref() {
        return getHref(false);
    }

    @Override
    public String getHref(final boolean mapped) {
        return getLink(mapped).getHref();
    }

    @Override
    public Link getLink() {
        return getLink(false);
    }

    @Override
    public Link getLink(final boolean mapped) {
        return getLinkBuilder(mapped).build();
    }

    @Override
    public LinkBuilder getLinkBuilder() {
        return getLinkBuilder(false);
    }

    @Override
    public LinkBuilder getLinkBuilder(final boolean mapped) {
        return LinkBuilderFactory.forPage(this, mapped, TitleType.TITLE);
    }

    @Override
    public Optional<String> getImageReferenceInherited(final boolean isSelf) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource
            .getImageReferenceInherited(isSelf));
    }

    @Override
    public Optional<String> getImageReferenceInherited() {
        return getVeneeredResource().flatMap(Inheritable :: getImageReferenceInherited);
    }

    @Override
    public Optional<String> getImageReferenceInherited(final String name) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getImageReferenceInherited(name));
    }

    @Override
    public <T> T getInherited(final String propertyName, final T defaultValue) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getInherited(propertyName, defaultValue))
            .orElse(defaultValue);
    }

    @Override
    public <T> Optional<T> getInherited(final String propertyName, final Class<T> type) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getInherited(propertyName, type));
    }

    @Override
    public List<Tag> getTagsInherited() {
        return getTagsInherited(NameConstants.PN_TAGS);
    }

    @Override
    public List<Tag> getTagsInherited(final String propertyName) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getTagsInherited(propertyName))
            .orElse(Collections.emptyList());
    }

    @Override
    public Optional<VeneeredResource> getVeneeredResourceInherited(final String relativePath) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource
            .getVeneeredResourceInherited(relativePath));
    }

    @Override
    public List<VeneeredResource> getVeneeredResourcesInherited() {
        return getVeneeredResource()
            .map(Inheritable :: getVeneeredResourcesInherited)
            .orElse(Collections.emptyList());
    }

    @Override
    public List<VeneeredResource> getVeneeredResourcesInherited(final Predicate<VeneeredResource> predicate) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getVeneeredResourcesInherited(predicate))
            .orElse(Collections.emptyList());
    }

    @Override
    public List<VeneeredResource> getVeneeredResourcesInherited(final String relativePath) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getVeneeredResourcesInherited(relativePath))
            .orElse(Collections.emptyList());
    }

    @Override
    public List<VeneeredResource> getVeneeredResourcesInherited(final String relativePath,
        final Predicate<VeneeredResource> predicate) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getVeneeredResourcesInherited(relativePath, predicate))
            .orElse(Collections.emptyList());
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsLink(propertyName));
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName, final boolean strict) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsLink(propertyName, strict));
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName, final boolean strict, final boolean mapped) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource
            .getAsLink(propertyName, strict, mapped));
    }

    @Override
    public <T> List<T> getAsList(final String propertyName, final Class<T> type) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getAsList(propertyName, type))
            .orElse(Collections.emptyList());
    }

    @Override
    public Optional<VeneeredPage> getAsVeneeredPage(final String propertyName) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsVeneeredPage(propertyName));
    }

    @Override
    public List<VeneeredPage> getAsVeneeredPageList(final String propertyName) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getAsVeneeredPageList(propertyName))
            .orElse(Collections.emptyList());
    }

    @Override
    public Optional<Resource> getAsResource(final String propertyName) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsResource(propertyName));
    }

    @Override
    public List<Resource> getAsResourceList(final String propertyName) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getAsResourceList(propertyName))
            .orElse(Collections.emptyList());
    }

    @Override
    public <AdapterType> Optional<AdapterType> getAsType(final String propertyName, final Class<AdapterType> type) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getAsType(propertyName, type));
    }

    @Override
    public <AdapterType> List<AdapterType> getAsTypeList(final String propertyName, final Class<AdapterType> type) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getAsTypeList(propertyName, type))
            .orElse(Collections.emptyList());
    }

    @Override
    public Optional<String> getImageReference(final boolean isSelf) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getImageReference(isSelf));
    }

    @Override
    public Optional<String> getImageReference() {
        return getVeneeredResource().flatMap(Accessible :: getImageReference);
    }

    @Override
    public Optional<String> getImageReference(final String name) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getImageReference(name));
    }

    @Override
    public Optional<String> getImageRendition(final String renditionName) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getImageRendition(renditionName));
    }

    @Override
    public Optional<String> getImageRendition(final String name, final String renditionName) {
        return getVeneeredResource().flatMap(veneeredResource -> veneeredResource.getImageRendition(name,
            renditionName));
    }

    @Override
    public List<Tag> getTags() {
        return getTags(NameConstants.PN_TAGS);
    }

    @Override
    public List<Tag> getTags(final String propertyName) {
        return getVeneeredResource()
            .map(veneeredResource -> veneeredResource.getTags(propertyName))
            .orElse(Collections.emptyList());
    }

    @Override
    public boolean isHasImage() {
        return getVeneeredResource().map(Accessible :: isHasImage).orElse(false);
    }

    @Override
    public boolean isHasImage(final String name) {
        return getVeneeredResource().map(veneeredResource -> veneeredResource.isHasImage(name)).orElse(false);
    }

    @Override
    public Iterator<VeneeredPage> listChildren() {
        return listChildren(page -> true);
    }

    @Override
    public Iterator<VeneeredPage> listChildren(final Predicate<VeneeredPage> predicate) {
        return listChildren(predicate, false);
    }

    @Override
    public Iterator<VeneeredPage> listChildren(final Predicate<VeneeredPage> predicate, final boolean deep) {
        final Resource resource = delegate.adaptTo(Resource.class);
        final Iterator<Resource> iterator = deep ? new DeepResourceIterator(resource) : resource.listChildren();

        return new VeneeredPageIterator(iterator, predicate);
    }

    @Override
    public Optional<VeneeredPage> getChild(final String name) {
        Optional<VeneeredPage> child = Optional.empty();

        if (delegate.hasChild(name)) {
            child = Optional.of(delegate.adaptTo(Resource.class).getChild(name).adaptTo(VeneeredPage.class));
        }

        return child;
    }

    @Override
    public Optional<VeneeredResource> getVeneeredResource() {
        return veneeredResource;
    }

    @Override
    public Optional<VeneeredResource> getVeneeredResource(final String relativePath) {
        return Optional.ofNullable(delegate.getContentResource(relativePath))
            .map(resource -> resource.adaptTo(VeneeredResource.class));
    }

    @Override
    public Link getLink(final TitleType titleType) {
        return getLinkBuilder(titleType).build();
    }

    @Override
    public Link getLink(final TitleType titleType, final boolean mapped) {
        return getLinkBuilder(titleType, mapped).build();
    }

    @Override
    public LinkBuilder getLinkBuilder(final TitleType titleType) {
        return getLinkBuilder(titleType, false);
    }

    @Override
    public LinkBuilder getLinkBuilder(final TitleType titleType, final boolean mapped) {
        return LinkBuilderFactory.forPage(this, mapped, titleType);
    }

    @Override
    public Link getNavigationLink(final boolean isActive) {
        return getNavigationLink(isActive, false);
    }

    @Override
    public Link getNavigationLink(final boolean isActive, final boolean mapped) {
        return LinkBuilderFactory.forPage(this, mapped, TitleType.NAVIGATION_TITLE)
            .setActive(isActive)
            .build();
    }

    @Override
    public String getTemplatePath() {
        return delegate.getProperties().get(NameConstants.NN_TEMPLATE, String.class);
    }

    @Override
    public Optional<String> getTitle(final TitleType titleType) {
        return get(titleType.getPropertyName(), String.class);
    }

    @Override
    public VeneeredPage getParent() {
        return Optional.ofNullable(delegate.getParent())
            .map(parent -> parent.adaptTo(VeneeredPage.class))
            .orElse(null);
    }

    @Override
    public VeneeredPage getParent(final int level) {
        return Optional.ofNullable(delegate.getParent(level))
            .map(parent -> parent.adaptTo(VeneeredPage.class))
            .orElse(null);
    }

    @Override
    public VeneeredPage getAbsoluteParent(final int level) {
        return Optional.ofNullable(delegate.getAbsoluteParent(level))
            .map(parent -> parent.adaptTo(VeneeredPage.class))
            .orElse(null);
    }

    @Override
    public VeneeredPageManager getVeneeredPageManager() {
        return delegate.getContentResource().getResourceResolver().adaptTo(VeneeredPageManager.class);
    }

    @Override
    public ReplicationStatus getReplicationStatus() {
        return delegate.adaptTo(ReplicationStatus.class);
    }

    // internals

    private Optional<VeneeredPage> findAncestorForPredicate(final Predicate<VeneeredResource> predicate,
        final boolean excludeCurrentResource) {
        VeneeredPage page = excludeCurrentResource ? getParent() : this;
        VeneeredPage ancestorPage = null;

        while (page != null) {
            final Optional<VeneeredResource> optionalVeneeredResource = page.getVeneeredResource();

            if (optionalVeneeredResource.isPresent() && predicate.test(optionalVeneeredResource.get())) {
                ancestorPage = page;
                break;
            } else {
                page = page.getParent();
            }
        }

        return Optional.ofNullable(ancestorPage);
    }
}
