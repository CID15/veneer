package org.cid15.aem.veneer.injectors.impl;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.cid15.aem.veneer.injectors.annotations.ReferenceInject;
import org.cid15.aem.veneer.injectors.utils.InjectorUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component(service = Injector.class)
public final class ReferenceInjector extends AbstractVeneeredResourceInjector
    implements InjectAnnotationProcessorFactory2 {

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceInjector.class);

    @Override
    public Object getValue(final VeneeredResource veneeredResource, final String name, final Type declaredType,
        final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        final ReferenceInject annotation = element.getAnnotation(ReferenceInject.class);
        Object value = null;

        if (annotation != null) {
            final List<String> references = annotation.inherit() ? veneeredResource.getAsListInherited(name,
                String.class) : veneeredResource.getAsList(name, String.class);

            final List<Object> referencedObjects = getReferencedObjects(veneeredResource, declaredType, references);

            if (!referencedObjects.isEmpty()) {
                if (InjectorUtils.isDeclaredTypeCollection(declaredType)) {
                    value = referencedObjects;
                } else {
                    value = referencedObjects.get(0);
                }
            }
        }

        return value;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final Object adaptable,
        final AnnotatedElement element) {
        final ReferenceInject annotation = element.getAnnotation(ReferenceInject.class);

        return annotation != null ? new ReferenceAnnotationProcessor(annotation) : null;
    }

    @Override
    public String getName() {
        return ReferenceInject.NAME;
    }

    private List<Object> getReferencedObjects(final VeneeredResource veneeredResource, final Type declaredType,
        final List<String> references) {
        final Class<?> declaredClass = InjectorUtils.getDeclaredClassForDeclaredType(declaredType);

        final List<Object> referencedObjects = new ArrayList<>();

        final ResourceResolver resourceResolver = veneeredResource.getResource().getResourceResolver();

        for (final String reference : references) {
            final Resource referencedResource = reference.startsWith("/") ? resourceResolver.getResource(
                reference) : resourceResolver.getResource(veneeredResource.getResource(), reference);

            if (referencedResource == null) {
                LOG.warn("reference {} did not resolve to an accessible resource", reference);
            } else {
                if (declaredClass != Resource.class) {
                    final Object adaptedObject = referencedResource.adaptTo(declaredClass);

                    if (adaptedObject == null) {
                        LOG.warn("resource at {} could not be adapted to an instance of {}",
                            referencedResource.getPath(), declaredClass.getName());
                    } else {
                        referencedObjects.add(adaptedObject);
                    }
                } else {
                    referencedObjects.add(referencedResource);
                }
            }
        }

        return referencedObjects;
    }

    static class ReferenceAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        private final ReferenceInject annotation;

        ReferenceAnnotationProcessor(final ReferenceInject annotation) {
            this.annotation = annotation;
        }

        @Override
        public InjectionStrategy getInjectionStrategy() {
            return annotation.injectionStrategy();
        }
    }
}
