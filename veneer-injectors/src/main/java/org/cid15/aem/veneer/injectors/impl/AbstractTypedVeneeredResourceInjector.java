package org.cid15.aem.veneer.injectors.impl;

import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.cid15.aem.veneer.injectors.utils.InjectorUtils;

import javax.inject.Named;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public abstract class AbstractTypedVeneeredResourceInjector<T> implements Injector {

    @Override
    @SuppressWarnings("unchecked")
    public Object getValue(final Object adaptable, final String name, final Type declaredType,
        final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        final Type clazz = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        Object value = null;

        if (declaredType == clazz) {
            value = Optional.ofNullable(InjectorUtils.getResource(adaptable))
                .map(resource -> resource.adaptTo(VeneeredResource.class))
                .map(veneeredResource -> {
                    final Named namedAnnotation = element.getAnnotation(Named.class);

                    return getVeneeredResourceValue(
                        veneeredResource,
                        Optional.ofNullable(namedAnnotation).map(Named :: value).orElse(name),
                        (Class<T>) declaredType,
                        element,
                        callbackRegistry
                    );
                })
                .orElse(null);
        }

        return value;
    }

    protected abstract Object getVeneeredResourceValue(final VeneeredResource veneeredResource, final String name,
        final Class<T> declaredType, final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry);
}
