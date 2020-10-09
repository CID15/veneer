package org.cid15.aem.veneer.injectors.impl;

import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.cid15.aem.veneer.injectors.utils.InjectorUtils;

import javax.inject.Named;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Optional;

public abstract class AbstractVeneeredResourceInjector implements Injector {

    @Override
    public Object getValue(final Object adaptable, final String name, final Type declaredType,
        final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        return Optional.ofNullable(InjectorUtils.getResource(adaptable))
            .map(resource -> resource.adaptTo(VeneeredResource.class))
            .map(veneeredResource -> {
                final Named namedAnnotation = element.getAnnotation(Named.class);

                return getValue(
                    veneeredResource,
                    Optional.ofNullable(namedAnnotation).map(Named :: value).orElse(name),
                    declaredType,
                    element,
                    callbackRegistry
                );
            })
            .orElse(null);
    }

    protected abstract Object getValue(final VeneeredResource veneeredResource, final String name,
        final Type declaredType, final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry);
}
