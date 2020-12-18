package org.cid15.aem.veneer.core.resource.impl;

import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.foundation.Image;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.cid15.aem.veneer.api.link.Link;
import org.cid15.aem.veneer.api.link.builders.LinkBuilder;
import org.cid15.aem.veneer.api.page.VeneeredPage;
import org.cid15.aem.veneer.api.page.VeneeredPageManager;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.cid15.aem.veneer.core.link.builders.factory.LinkBuilderFactory;
import org.cid15.aem.veneer.core.resource.predicates.VeneeredResourcePropertyExistsPredicate;
import org.cid15.aem.veneer.core.resource.predicates.VeneeredResourcePropertyValuePredicate;
import org.cid15.aem.veneer.core.resource.predicates.VeneeredResourceTypePredicate;
import org.cid15.aem.veneer.core.utils.PathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkNotNull;

@Model(adaptables = Resource.class, adapters = VeneeredResource.class)
public final class DefaultVeneeredResource implements VeneeredResource {

    private static final Function<Resource, VeneeredResource> TO_VENEERED_RESOURCE = resource -> resource
        .adaptTo(VeneeredResource.class);

    /**
     * Default page/component image name.
     */
    private static final String DEFAULT_IMAGE_NAME = "image";

    @Self
    private Resource resource;

    private InheritanceValueMap properties;

    private VeneeredPageManager pageManager;

    @SuppressWarnings("unchecked")
    @Override
    public <AdapterType> AdapterType adaptTo(final Class<AdapterType> type) {
        final AdapterType result;

        if (type == Resource.class) {
            result = (AdapterType) resource;
        } else {
            result = resource.adaptTo(type);
        }

        return result;
    }

    @Override
    public boolean equals(final Object other) {
        return new EqualsBuilder().append(getPath(), ((VeneeredResource) other).getPath()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getPath()).hashCode();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("path", getPath())
            .add("properties", Maps.newHashMap(getProperties()))
            .toString();
    }

    @Override
    public InheritanceValueMap getProperties() {
        if (properties == null) {
            properties = new HierarchyNodeInheritanceValueMap(resource);
        }

        return properties;
    }

    @Override
    public <T> T get(final String propertyName, final T defaultValue) {
        return getProperties().get(checkNotNull(propertyName), defaultValue);
    }

    @Override
    public <T> Optional<T> get(final String propertyName, final Class<T> type) {
        return Optional.ofNullable(getProperties().get(propertyName, type));
    }

    @Override
    public <AdapterType> Optional<AdapterType> getAsType(final String propertyName, final Class<AdapterType> type) {
        return getAsTypeOptional(getProperties().get(checkNotNull(propertyName), String.class), type);
    }

    @Override
    public <AdapterType> List<AdapterType> getAsTypeList(final String propertyName, final Class<AdapterType> type) {
        return Arrays.stream(getProperties().get(checkNotNull(propertyName), new String[0]))
            .map(path -> getAsTypeOptional(path, type).orElse(null))
            .filter(java.util.Objects :: nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<String> getAsHref(final String propertyName) {
        return getAsHref(propertyName, false);
    }

    @Override
    public Optional<String> getAsHref(final String propertyName, final boolean strict) {
        return getAsHref(propertyName, strict, false);
    }

    @Override
    public Optional<String> getAsHref(final String propertyName, final boolean strict, final boolean mapped) {
        return getAsLink(propertyName, strict, mapped).map(Link :: getHref);
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName) {
        return getAsLink(propertyName, false);
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName, final boolean strict) {
        return getAsLink(propertyName, strict, false);
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName, final boolean strict, final boolean mapped) {
        return getLinkOptional(get(propertyName, String.class), strict, mapped);
    }

    @Override
    public <T> List<T> getAsList(final String propertyName, final Class<T> type) {
        return Lists.newArrayList(getProperties().get(checkNotNull(propertyName), ObjectArrays.newArray(type, 0)));
    }

    @Override
    public Optional<VeneeredPage> getAsVeneeredPage(final String propertyName) {
        return getPageOptional(getProperties().get(checkNotNull(propertyName), String.class));
    }

    @Override
    public List<VeneeredPage> getAsVeneeredPageList(final String propertyName) {
        return Arrays.stream(getProperties().get(checkNotNull(propertyName), new String[0]))
            .map(getPageManager() :: getVeneeredPage)
            .filter(java.util.Objects :: nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Resource> getAsResource(final String propertyName) {
        return getAsResourceOptional(getProperties().get(checkNotNull(propertyName), String.class));
    }

    @Override
    public List<Resource> getAsResourceList(final String propertyName) {
        return Arrays.stream(getProperties().get(checkNotNull(propertyName), new String[0]))
            .map(path -> getAsResourceOptional(path).orElse(null))
            .filter(java.util.Objects :: nonNull)
            .collect(Collectors.toList());
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
        return LinkBuilderFactory.forResource(resource, mapped);
    }

    @Override
    public String getResourceType() {
        return resource.getResourceType();
    }

    @Override
    public String getResourceSuperType() {
        return resource.getResourceSuperType();
    }

    @Override
    public String getId() {
        final String path;

        if (resource.getName().equals(JcrConstants.JCR_CONTENT)) {
            path = resource.getParent().getPath(); // use page path for jcr:content nodes
        } else if (resource.getResourceType().equals(NameConstants.NT_PAGE)) {
            path = resource.getPath();
        } else {
            final VeneeredPage currentPage = getPageManager().getContainingVeneeredPage(resource);

            if (currentPage != null) {
                // remove page content path since resource path relative to jcr:content will always be unique
                path = StringUtils.removeStart(getPath(), currentPage.getPage().getContentResource().getPath());
            } else {
                path = resource.getPath(); // non-content path
            }
        }

        return path.substring(1).replaceAll("/", "-");
    }

    @Override
    public Optional<String> getImageReference(final boolean isSelf) {
        return isSelf ? Optional.ofNullable(getProperties().get(DownloadResource.PN_REFERENCE, String.class))
            : getImageReference();
    }

    @Override
    public Optional<String> getImageReference() {
        return getImageReference(DEFAULT_IMAGE_NAME);
    }

    @Override
    public Optional<String> getImageReference(final String name) {
        return Optional.ofNullable(getProperties().get(getImageReferencePropertyName(name), String.class));
    }

    @Override
    public Optional<String> getImageRendition(final String renditionName) {
        return getImageRendition(DEFAULT_IMAGE_NAME, checkNotNull(renditionName));
    }

    @Override
    public Optional<String> getImageRendition(final String name, final String renditionName) {
        checkNotNull(renditionName);

        return getAsType(getImageReferencePropertyName(name), Asset.class)
            .flatMap(asset -> asset.getRenditions()
                .stream()
                .filter(rendition -> rendition.getName().equals(renditionName))
                .findFirst())
            .map(Rendition :: getPath);
    }

    @Override
    public List<Tag> getTags() {
        return getTags(NameConstants.PN_TAGS);
    }

    @Override
    public List<Tag> getTags(final String propertyName) {
        final TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);

        return getAsList(propertyName, String.class)
            .stream()
            .map(tagManager :: resolve)
            .filter(java.util.Objects :: nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public List<Tag> getTagsInherited(final String propertyName) {
        final TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);

        return getAsListInherited(propertyName, String.class)
            .stream()
            .map(tagManager :: resolve)
            .filter(java.util.Objects :: nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public int getIndex() {
        return getIndexForPredicate(resource -> true);
    }

    @Override
    public int getIndex(final String resourceType) {
        return getIndexForPredicate(resource -> resource.isResourceType(resourceType));
    }

    @Override
    public String getName() {
        return resource.getName();
    }

    @Override
    public String getPath() {
        return resource.getPath();
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return resource.getResourceResolver();
    }

    @Override
    public boolean isHasImage() {
        return isHasImage(null) || isHasImage(DEFAULT_IMAGE_NAME);
    }

    @Override
    public boolean isHasImage(final String name) {
        if (name != null) {
            final Resource child = resource.getChild(name);

            return child != null && new Image(resource, name).hasContent();
        } else {
            return new Image(resource).hasContent();
        }
    }

    @Override
    public Optional<VeneeredResource> findAncestor(final Predicate<VeneeredResource> predicate) {
        return findAncestorForPredicate(predicate, false);
    }

    @Override
    public Optional<VeneeredResource> findAncestor(final Predicate<VeneeredResource> predicate,
        final boolean excludeCurrentResource) {
        return findAncestorForPredicate(predicate, excludeCurrentResource);
    }

    @Override
    public List<VeneeredResource> findDescendants(final Predicate<VeneeredResource> predicate) {
        final List<VeneeredResource> descendantVeneeredResources = new ArrayList<>();

        for (final VeneeredResource veneeredResource : getVeneeredResources()) {
            if (predicate.test(veneeredResource)) {
                descendantVeneeredResources.add(veneeredResource);
            }

            descendantVeneeredResources.addAll(veneeredResource.findDescendants(predicate));
        }

        return descendantVeneeredResources;
    }

    @Override
    public Optional<VeneeredResource> findAncestorWithProperty(final String propertyName) {
        return findAncestorForPredicate(new VeneeredResourcePropertyExistsPredicate(propertyName), false);
    }

    @Override
    public Optional<VeneeredResource> findAncestorWithProperty(final String propertyName,
        final boolean excludeCurrentResource) {
        return findAncestorForPredicate(new VeneeredResourcePropertyExistsPredicate(propertyName),
            excludeCurrentResource);
    }

    @Override
    public <V> Optional<VeneeredResource> findAncestorWithPropertyValue(final String propertyName,
        final V propertyValue) {
        return findAncestorForPredicate(new VeneeredResourcePropertyValuePredicate<>(propertyName, propertyValue),
            false);
    }

    @Override
    public <V> Optional<VeneeredResource> findAncestorWithPropertyValue(final String propertyName,
        final V propertyValue, final boolean excludeCurrentResource) {
        return findAncestorForPredicate(new VeneeredResourcePropertyValuePredicate<>(propertyName, propertyValue),
            excludeCurrentResource);
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName) {
        return getAsHrefInherited(propertyName, false);
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName, final boolean mapped) {
        return getAsHrefInherited(propertyName, mapped, false);
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName, final boolean strict, final boolean mapped) {
        return getAsLinkInherited(propertyName, strict, mapped).map(Link :: getHref);
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName) {
        return getAsLinkInherited(propertyName, false);
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict) {
        return getAsLinkInherited(propertyName, strict, false);
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict, final boolean mapped) {
        return getLinkOptional(getInherited(propertyName, String.class), strict, mapped);
    }

    @Override
    public <T> List<T> getAsListInherited(final String propertyName, final Class<T> type) {
        return Arrays.asList(getProperties().getInherited(checkNotNull(propertyName), ObjectArrays.newArray(type, 0)));
    }

    @Override
    public Optional<VeneeredPage> getAsVeneeredPageInherited(final String propertyName) {
        return getPageOptional(getProperties().getInherited(checkNotNull(propertyName), ""));
    }

    @Override
    public List<VeneeredPage> getAsVeneeredPageListInherited(final String propertyName) {
        return getAsListInherited(propertyName, String.class)
            .stream()
            .map(getPageManager() :: getVeneeredPage)
            .filter(java.util.Objects :: nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Resource> getAsResourceInherited(final String propertyName) {
        return getAsResourceOptional(getProperties().getInherited(checkNotNull(propertyName), ""));
    }

    @Override
    public List<Resource> getAsResourceListInherited(final String propertyName) {
        return getAsListInherited(propertyName, String.class)
            .stream()
            .map(path -> getAsResourceOptional(path).orElse(null))
            .filter(java.util.Objects :: nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public <AdapterType> Optional<AdapterType> getAsTypeInherited(final String propertyName,
        final Class<AdapterType> type) {
        return getAsTypeOptional(getProperties().getInherited(checkNotNull(propertyName), String.class), type);
    }

    @Override
    public <AdapterType> List<AdapterType> getAsTypeListInherited(final String propertyName,
        final Class<AdapterType> type) {
        return getAsListInherited(propertyName, String.class)
            .stream()
            .map(path -> getAsResourceOptional(path)
                .map(resource -> resource.adaptTo(type))
                .orElse(null))
            .filter(java.util.Objects :: nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<VeneeredResource> getVeneeredResource(final String relativePath) {
        return Optional.ofNullable(resource.getChild(checkNotNull(relativePath))).map(TO_VENEERED_RESOURCE);
    }

    @Override
    public List<VeneeredResource> getVeneeredResources() {
        return Lists.newArrayList(resource.getChildren())
            .stream()
            .map(TO_VENEERED_RESOURCE)
            .collect(Collectors.toList());
    }

    @Override
    public List<VeneeredResource> getVeneeredResources(final Predicate<VeneeredResource> predicate) {
        return Lists.newArrayList(resource.getChildren())
            .stream()
            .map(TO_VENEERED_RESOURCE)
            .filter(checkNotNull(predicate))
            .collect(Collectors.toList());
    }

    @Override
    public List<VeneeredResource> getVeneeredResources(final String relativePath) {
        return Optional.ofNullable(resource.getChild(checkNotNull(relativePath)))
            .map(childResource -> Lists.newArrayList(childResource.getChildren())
                .stream()
                .map(TO_VENEERED_RESOURCE)
                .collect(Collectors.toList()))
            .orElse(Collections.emptyList());
    }

    @Override
    public List<VeneeredResource> getVeneeredResources(final String relativePath, final String resourceType) {
        return getVeneeredResources(relativePath, new VeneeredResourceTypePredicate(resourceType));
    }

    @Override
    public List<VeneeredResource> getVeneeredResources(final String relativePath,
        final Predicate<VeneeredResource> predicate) {
        return getVeneeredResources(checkNotNull(relativePath))
            .stream()
            .filter(checkNotNull(predicate))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<String> getImageReferenceInherited() {
        return getImageReferenceInherited(DEFAULT_IMAGE_NAME);
    }

    @Override
    public Optional<String> getImageReferenceInherited(boolean isSelf) {
        return isSelf ? Optional.ofNullable(getProperties().getInherited(DownloadResource.PN_REFERENCE, String.class))
            : getImageReferenceInherited();
    }

    @Override
    public Optional<String> getImageReferenceInherited(final String name) {
        final String propertyName = new StringBuilder(name)
            .append("/")
            .append(DownloadResource.PN_REFERENCE)
            .toString();

        return Optional.ofNullable(getProperties().getInherited(propertyName, String.class));
    }

    @Override
    public <T> T getInherited(final String propertyName, final T defaultValue) {
        return getProperties().getInherited(propertyName, defaultValue);
    }

    @Override
    public <T> Optional<T> getInherited(final String propertyName, final Class<T> type) {
        return Optional.ofNullable(getProperties().getInherited(propertyName, type));
    }

    @Override
    public List<Tag> getTagsInherited() {
        return getTagsInherited(NameConstants.PN_TAGS);
    }

    @Override
    public Optional<VeneeredResource> getVeneeredResourceInherited(final String relativePath) {
        return findChildVeneeredResourceInherited(relativePath);
    }

    @Override
    public List<VeneeredResource> getVeneeredResourcesInherited() {
        return findAncestor(veneeredResource -> veneeredResource.getResource().hasChildren())
            .map(VeneeredResource :: getVeneeredResources)
            .orElse(Collections.emptyList());
    }

    @Override
    public List<VeneeredResource> getVeneeredResourcesInherited(final Predicate<VeneeredResource> predicate) {
        return findAncestor(veneeredResource -> veneeredResource.getResource().hasChildren())
            .map(veneeredResource -> veneeredResource.getVeneeredResources()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList()))
            .orElse(Collections.emptyList());
    }

    @Override
    public List<VeneeredResource> getVeneeredResourcesInherited(final String relativePath) {
        return findChildVeneeredResourceInherited(relativePath)
            .map(VeneeredResource :: getVeneeredResources)
            .orElse(Collections.emptyList());
    }

    @Override
    public List<VeneeredResource> getVeneeredResourcesInherited(final String relativePath,
        final Predicate<VeneeredResource> predicate) {
        return findChildVeneeredResourceInherited(relativePath)
            .map(veneeredResource -> veneeredResource.getVeneeredResources()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList()))
            .orElse(Collections.emptyList());
    }

    @Override
    public Optional<VeneeredResource> getParent() {
        return Optional.ofNullable(resource.getParent()).map(TO_VENEERED_RESOURCE);
    }

    // internals

    private Optional<Resource> getAsResourceOptional(final String path) {
        return Optional.ofNullable(path).map(resourcePath -> resource.getResourceResolver().getResource(resourcePath));
    }

    @SuppressWarnings("unchecked")
    private <AdapterType> Optional<AdapterType> getAsTypeOptional(final String path, final Class<AdapterType> type) {
        final Optional<Resource> resource = getAsResourceOptional(path);

        return type == Resource.class ? (Optional<AdapterType>) resource : resource.map(r -> r.adaptTo(type));
    }

    private Optional<Link> getLinkOptional(final Optional<String> pathOptional, final boolean strict,
        final boolean mapped) {
        return pathOptional.map(path -> {
            final ResourceResolver resourceResolver = resource.getResourceResolver();
            final Resource resource = resourceResolver.getResource(path);

            final LinkBuilder linkBuilder;

            if (resource != null) {
                // internal link
                linkBuilder = LinkBuilderFactory.forResource(resource, mapped);
            } else {
                // external link
                final String mappedPath = mapped ? resourceResolver.map(path) : path;

                linkBuilder = LinkBuilderFactory.forPath(mappedPath);

                if (strict) {
                    linkBuilder.setExternal(PathUtils.isExternal(mappedPath, resourceResolver));
                }
            }

            return linkBuilder.build();
        });
    }

    private int getIndexForPredicate(final Predicate<Resource> resourceTypePredicate) {
        final List<Resource> resources = Lists.newArrayList(resource.getParent().getChildren())
            .stream()
            .filter(resourceTypePredicate)
            .collect(Collectors.toList());

        return IntStream.range(0, resources.size())
            .filter(i -> resources.get(i).getPath().equals(this.getPath()))
            .findFirst()
            .orElse(-1);
    }

    private Optional<VeneeredPage> getPageOptional(final String path) {
        return Optional.ofNullable(path).map(pagePath -> getPageManager().getVeneeredPage(pagePath));
    }

    private Optional<VeneeredResource> findAncestorForPredicate(final Predicate<VeneeredResource> predicate,
        final boolean excludeCurrentResource) {
        final Page containingPage = getPageManager().getContainingVeneeredPage(resource).getPage();

        // get path of current resource relative to the page's jcr:content node
        final String relativePath = resource.getName().equals(JcrConstants.JCR_CONTENT) ? "" : resource.getPath()
            .substring(containingPage.getContentResource().getPath().length() + 1);

        final Function<Page, Resource> contentResourceFunction = page -> relativePath.isEmpty() ? page
            .getContentResource() : page.getContentResource(relativePath);

        Predicate<Page> pagePredicate = page -> {
            final Resource contentResource = contentResourceFunction.apply(page);

            return contentResource != null && predicate.test(contentResource.adaptTo(VeneeredResource.class));
        };

        return findAncestorPage(containingPage, pagePredicate, excludeCurrentResource)
            .map(page -> contentResourceFunction.apply(page).adaptTo(VeneeredResource.class));
    }

    private Optional<VeneeredResource> findChildVeneeredResourceInherited(final String relativePath) {
        final Page containingPage = getPageManager().getContainingVeneeredPage(resource).getPage();

        final StringBuilder builder = new StringBuilder();

        if (resource.getName().equals(JcrConstants.JCR_CONTENT)) {
            builder.append(relativePath);
        } else {
            builder.append(resource.getPath().substring(containingPage.getContentResource().getPath().length() + 1));
            builder.append('/');
            builder.append(relativePath);
        }

        // path relative to jcr:content
        final String resourcePath = builder.toString();

        return findAncestorPage(containingPage, page -> page.getContentResource(resourcePath) != null, false)
            .map(page -> page.getContentResource(resourcePath))
            .map(TO_VENEERED_RESOURCE);
    }

    private Optional<Page> findAncestorPage(final Page page, final Predicate<Page> predicate,
        final boolean excludeCurrent) {
        Page currentPage = excludeCurrent ? page.getParent() : page;
        Page ancestorPage = null;

        while (currentPage != null) {
            if (predicate.test(currentPage)) {
                ancestorPage = currentPage;
                break;
            } else {
                currentPage = currentPage.getParent();
            }
        }

        return Optional.ofNullable(ancestorPage);
    }

    private VeneeredPageManager getPageManager() {
        if (pageManager == null) {
            pageManager = resource.getResourceResolver().adaptTo(VeneeredPageManager.class);
        }

        return pageManager;
    }

    private String getImageReferencePropertyName(final String name) {
        return checkNotNull(name) + "/" + DownloadResource.PN_REFERENCE;
    }
}
