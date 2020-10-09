package org.cid15.aem.veneer.core.resource.predicates;

import org.cid15.aem.veneer.api.resource.VeneeredResource;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

public final class VeneeredResourcePropertyExistsPredicate implements Predicate<VeneeredResource> {

    private final String propertyName;

    public VeneeredResourcePropertyExistsPredicate(final String propertyName) {
        this.propertyName = checkNotNull(propertyName);
    }

    @Override
    public boolean test(final VeneeredResource veneeredResource) {
        return veneeredResource.getResource().getValueMap().containsKey(propertyName);
    }
}
