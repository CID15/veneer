package org.cid15.aem.veneer.injectors.impl;

import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.cid15.aem.veneer.injectors.annotations.InheritInject;
import org.cid15.aem.veneer.injectors.utils.InjectorUtils;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Collectors;

@Component(service = Injector.class)
public final class InheritInjector extends AbstractVeneeredResourceInjector
    implements InjectAnnotationProcessorFactory2 {

    @Override
    public String getName() {
        return InheritInject.NAME;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getValue(final VeneeredResource veneeredResource, final String name, final Type declaredType,
        final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        Object value = null;

        if (element.getAnnotation(InheritInject.class) != null) {
            if (InjectorUtils.isParameterizedListType(declaredType)) {
                final Class<?> typeClass = InjectorUtils.getActualType((ParameterizedType) declaredType);

                value = veneeredResource.getVeneeredResourcesInherited(name)
                    .stream()
                    .map(cr -> cr.getResource().adaptTo(typeClass))
                    .collect(Collectors.toList());
            } else if (declaredType instanceof Class && ((Class) declaredType).isEnum()) {
                value = veneeredResource.getInherited(name, String.class)
                    .map(enumString -> Enum.valueOf((Class) declaredType, enumString))
                    .orElse(null);
            } else {
                value = veneeredResource.getInherited(name, (Class) declaredType).orElse(null);

                if (value == null) {
                    value = veneeredResource.getVeneeredResourceInherited(name)
                        .map(VeneeredResource :: getResource)
                        .map(resource -> resource.adaptTo((Class) declaredType))
                        .orElse(null);
                }
            }
        }

        return value;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final Object adaptable,
        final AnnotatedElement element) {
        final InheritInject annotation = element.getAnnotation(InheritInject.class);

        return annotation != null ? new InheritAnnotationProcessor(annotation) : null;
    }

    static class InheritAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        private final InheritInject annotation;

        InheritAnnotationProcessor(final InheritInject annotation) {
            this.annotation = annotation;
        }

        @Override
        public InjectionStrategy getInjectionStrategy() {
            return annotation.injectionStrategy();
        }
    }
}
