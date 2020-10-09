package org.cid15.aem.veneer.injectors.impl;

import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

@Component(service = Injector.class)
public final class EnumInjector extends AbstractVeneeredResourceInjector {

    @Override
    public String getName() {
        return "enum";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getValue(final VeneeredResource veneeredResource, final String name, final Type declaredType,
        final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        Object value = null;

        if (declaredType instanceof Class && ((Class) declaredType).isEnum()) {
            value = veneeredResource.get(name, String.class)
                .map(enumString -> Enum.valueOf((Class) declaredType, enumString))
                .orElse(null);
        }

        return value;
    }
}
