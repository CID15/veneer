package org.cid15.aem.veneer.core.link.builders.factory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.cid15.aem.veneer.api.constants.PropertyConstants;
import org.cid15.aem.veneer.api.link.Link;
import org.cid15.aem.veneer.api.link.builders.LinkBuilder;
import org.cid15.aem.veneer.api.page.VeneeredPage;
import org.cid15.aem.veneer.api.page.enums.TitleType;
import org.cid15.aem.veneer.core.link.builders.impl.DefaultLinkBuilder;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Factory for acquiring <code>LinkBuilder</code> instances.
 */
public final class LinkBuilderFactory {

    /**
     * Get a builder instance for an existing <code>Link</code>.  The path, extension, title, and target are copied from
     * the link argument.
     *
     * @param link existing link
     * @return builder
     */
    public static LinkBuilder forLink(final Link link) {
        checkNotNull(link);

        return new DefaultLinkBuilder(link.getPath(), null)
            .setExtension(link.getExtension().orElse(null))
            .setTitle(link.getTitle())
            .setTarget(link.getTarget());
    }

    /**
     * Get a builder instance for a page.  If the page contains a redirect, the builder will contain the redirect target
     * rather than the page path.
     *
     * @param page page
     * @return builder containing the path of the given page
     */
    public static LinkBuilder forPage(final VeneeredPage page) {
        return forPage(page, false, TitleType.TITLE);
    }

    /**
     * Get a builder instance for a page using the specified title type on the returned builder.
     *
     * @param page page
     * @param titleType type of page title to set on the builder
     * @return builder containing the path and title of the given page
     */
    public static LinkBuilder forPage(final VeneeredPage page, final TitleType titleType) {
        return forPage(page, false, titleType);
    }

    /**
     * Get a builder instance for a page.  If the page contains a redirect, the builder will contain the redirect target
     * rather than the page path.
     *
     * @param page page
     * @param mapped if true, link path will be mapped through resource resolver
     * @return builder containing the mapped path of the given page
     */
    public static LinkBuilder forPage(final VeneeredPage page, final boolean mapped) {
        return forPage(page, mapped, TitleType.TITLE);
    }

    /**
     * Get a builder instance for a page using the specified title type on the returned builder.
     *
     * @param page page
     * @param mapped if true, link path will be mapped through resource resolver
     * @param titleType type of page title to set on the builder
     * @return builder containing the path and title of the given page
     */
    public static LinkBuilder forPage(final VeneeredPage page, final boolean mapped, final TitleType titleType) {
        final String title = checkNotNull(page).getTitle(titleType).orElse(page.getPage().getTitle());

        final String redirect = page.get(PropertyConstants.CQ_REDIRECT_TARGET, String.class)
            .orElse(page.get(PropertyConstants.REDIRECT_TARGET, ""));

        final String path = redirect.isEmpty() ? page.getPath() : redirect;

        final Resource resource = page.getPage().getContentResource();

        return new DefaultLinkBuilder(path, (mapped && resource != null) ? resource.getResourceResolver() : null)
            .setTitle(title);
    }

    /**
     * Get a builder instance for a path.
     *
     * @param path content or external path
     * @return builder containing the given path
     */
    public static LinkBuilder forPath(final String path) {
        return new DefaultLinkBuilder(checkNotNull(path), null);
    }

    /**
     * Get a builder instance for a path and resource resolver, using the resource resolver to map the given path in the
     * built link.
     *
     * @param path content or external path
     * @param resourceResolver resource resolver for path mapping
     * @return builder containing the given path
     */
    public static LinkBuilder forPath(final String path, final ResourceResolver resourceResolver) {
        return new DefaultLinkBuilder(checkNotNull(path), resourceResolver);
    }

    /**
     * Get a builder instance to build a "mailto:" link for the given email address.
     *
     * @param emailAddress email address
     * @return builder with the given email address link
     */
    public static LinkBuilder forEmail(final String emailAddress) {
        return forPath("mailto:" + emailAddress);
    }

    /**
     * Get a builder instance to build a "tel:" link for the given telephone number.
     *
     * @param telephoneNumber phone number
     * @return builder with the given phone number link
     */
    public static LinkBuilder forTelephoneNumber(final String telephoneNumber) {
        return forPath("tel:" + telephoneNumber);
    }

    /**
     * Get a builder instance for the given Asset, using the asset's metadata title (or name, if not present) as the
     * link title.
     *
     * @param asset asset
     * @param mapped if true, link path will be mapped through resource resolver
     * @return
     */
    public static LinkBuilder forAsset(final Asset asset, final boolean mapped) {
        final Resource assetResource = checkNotNull(asset).adaptTo(Resource.class);

        final String title = Optional.ofNullable(asset.getMetadataValue(DamConstants.DC_TITLE))
            .orElse(asset.getName());

        return forResource(assetResource, mapped).setTitle(title);
    }

    /**
     * Get a builder instance for a resource.
     *
     * @param resource resource
     * @return builder containing the path of the given resource
     */
    public static LinkBuilder forResource(final Resource resource) {
        return forResource(resource, false);
    }

    /**
     * Get a builder instance for a resource using the mapped path on the returned builder.
     *
     * @param resource resource
     * @param mapped if true, link path will be mapped through resource resolver
     * @return builder containing the mapped path of the given resource
     */
    public static LinkBuilder forResource(final Resource resource, final boolean mapped) {
        return new DefaultLinkBuilder(checkNotNull(resource).getPath(), mapped ? resource.getResourceResolver() : null);
    }

    /**
     * Get an empty link builder instance ("#").
     *
     * @return empty path link builder
     */
    public static LinkBuilder forEmpty() {
        return forPath("#");
    }

    private LinkBuilderFactory() {

    }
}
