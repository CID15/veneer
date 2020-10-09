package org.cid15.aem.veneer.injectors.impl;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.cid15.aem.veneer.injectors.utils.InjectorUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * Injector for objects that are adaptable from a Sling resource resolver.
 */
@Component(service = Injector.class)
@ServiceRanking(Integer.MAX_VALUE)
public final class ResourceResolverAdaptableInjector implements Injector, AcceptsNullName {

    @Override
    public String getName() {
        return "adaptable";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getValue(final Object adaptable, final String name, final Type type, final AnnotatedElement element,
        final DisposalCallbackRegistry registry) {
        Object value = null;

        if (type instanceof Class) {
            final Class clazz = (Class) type;

            final Resource resource = InjectorUtils.getResource(adaptable);

            if (resource != null) {
                final ResourceResolver resourceResolver = resource.getResourceResolver();

                value = resourceResolver.adaptTo(clazz);
            }
        }

        return value;
    }
}
