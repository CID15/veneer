package org.cid15.aem.veneer.core.resource.predicates;

import org.apache.sling.api.resource.ValueMap;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

import static veneer.com.google.common.base.Preconditions.checkNotNull;

public final class VeneeredResourcePropertyValuePredicate<T> implements Predicate<VeneeredResource> {

    private static final Logger LOG = LoggerFactory.getLogger(VeneeredResourcePropertyValuePredicate.class);

    private final String propertyName;

    private final T propertyValue;

    public VeneeredResourcePropertyValuePredicate(final String propertyName, final T propertyValue) {
        this.propertyName = checkNotNull(propertyName);
        this.propertyValue = checkNotNull(propertyValue);
    }

    @Override
    public boolean test(final VeneeredResource veneeredResource) {
        final ValueMap properties = checkNotNull(veneeredResource).getProperties();

        boolean result = false;

        if (properties.containsKey(propertyName)) {
            result = properties.get(propertyName, propertyValue.getClass()).equals(propertyValue);

            LOG.debug("property name : {}, value : {}, result : {} for component node : {}", propertyName,
                propertyValue, result, veneeredResource);
        } else {
            LOG.debug("property name : {}, does not exist for component node : {}", propertyName, veneeredResource);
        }

        return result;
    }
}
