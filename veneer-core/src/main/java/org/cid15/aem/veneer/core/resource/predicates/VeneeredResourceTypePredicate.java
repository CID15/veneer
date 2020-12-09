package org.cid15.aem.veneer.core.resource.predicates;

import org.cid15.aem.veneer.api.resource.VeneeredResource;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Predicate for the resource type of the Veneered Resource.
 */
public final class VeneeredResourceTypePredicate implements Predicate<VeneeredResource> {

    private final String resourceType;

    public VeneeredResourceTypePredicate(final String resourceType) {
        this.resourceType = checkNotNull(resourceType);
    }

    @Override
    public boolean test(final VeneeredResource veneeredResource) {
        return veneeredResource.getResource().isResourceType(resourceType);
    }
}
