package org.cid15.aem.veneer.injectors.impl;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.cid15.aem.veneer.injectors.annotations.TagInject;
import org.cid15.aem.veneer.injectors.utils.InjectorUtils;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component(service = Injector.class)
public final class TagInjector extends AbstractVeneeredResourceInjector implements InjectAnnotationProcessorFactory2,
    AcceptsNullName {

    @Override
    public Object getValue(final VeneeredResource veneeredResource, final String name, final Type declaredType,
        final AnnotatedElement element, final DisposalCallbackRegistry callbackRegistry) {
        final TagInject annotation = element.getAnnotation(TagInject.class);

        final Class declaredClass = InjectorUtils.getDeclaredClassForDeclaredType(declaredType);

        Object value = null;

        if (declaredClass == Tag.class) {
            final TagManager tagManager = veneeredResource.getResource().getResourceResolver().adaptTo(
                TagManager.class);

            final List<String> tagIds = annotation != null && annotation.inherit() ?
                veneeredResource.getAsListInherited(name, String.class) :
                veneeredResource.getAsList(name, String.class);

            final List<Tag> tags = tagIds
                .stream()
                .map(tagManager :: resolve)
                .filter(Objects :: nonNull)
                .collect(Collectors.toList());

            if (!tags.isEmpty()) {
                value = InjectorUtils.isDeclaredTypeCollection(declaredType) ? tags : tags.get(0);
            }
        }

        return value;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final Object adaptable,
        final AnnotatedElement element) {
        final TagInject annotation = element.getAnnotation(TagInject.class);

        return annotation != null ? new TagAnnotationProcessor(annotation) : null;
    }

    @Override
    public String getName() {
        return TagInject.NAME;
    }

    static class TagAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        private final TagInject annotation;

        TagAnnotationProcessor(final TagInject annotation) {
            this.annotation = annotation;
        }

        @Override
        public InjectionStrategy getInjectionStrategy() {
            return annotation.injectionStrategy();
        }
    }
}
