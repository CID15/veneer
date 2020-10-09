package org.cid15.aem.veneer.injectors.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.cid15.aem.veneer.injectors.utils.InjectorUtils;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component(service = Injector.class)
public final class ModelListInjector implements Injector {

    @Override
    public String getName() {
        return "model-list";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getValue(final Object adaptable, final String name, final Type declaredType,
        final AnnotatedElement element, final DisposalCallbackRegistry registry) {
        final Resource resource = InjectorUtils.getResource(adaptable);

        Object value = null;

        if (resource != null && InjectorUtils.isParameterizedListType(declaredType)) {
            final Class typeClass = InjectorUtils.getActualType((ParameterizedType) declaredType);

            if (isTypeAdaptableFromResource(typeClass)) {
                final Resource childResource = resource.getChild(name);

                if (childResource != null) {
                    final List<Object> models = new ArrayList<>();

                    for (final Resource grandChildResource : childResource.getChildren()) {
                        final Object adaptedType = grandChildResource.adaptTo(typeClass);

                        if (adaptedType != null) {
                            models.add(adaptedType);
                        }
                    }

                    value = models;
                }
            }
        }

        return value;
    }

    private boolean isTypeAdaptableFromResource(final Class typeClass) {
        boolean adaptableFromResource = false;

        if (typeClass.isAnnotationPresent(Model.class)) {
            final Model model = (Model) typeClass.getDeclaredAnnotation(Model.class);

            adaptableFromResource = ArrayUtils.contains(model.adaptables(), Resource.class);
        }

        return adaptableFromResource;
    }
}
