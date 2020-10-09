package org.cid15.aem.veneer.injectors.impl;

import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2;
import org.cid15.aem.veneer.api.link.Link;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.cid15.aem.veneer.core.link.builders.factory.LinkBuilderFactory;
import org.cid15.aem.veneer.injectors.annotations.LinkInject;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedElement;

@Component(service = Injector.class)
public final class LinkInjector extends AbstractTypedVeneeredResourceInjector<Link> implements Injector,
    InjectAnnotationProcessorFactory2, AcceptsNullName {

    @Override
    public String getName() {
        return LinkInject.NAME;
    }

    @Override
    public Object getVeneeredResourceValue(final VeneeredResource veneeredResource, final String name,
        final Class<Link> declaredType, final AnnotatedElement element,
        final DisposalCallbackRegistry callbackRegistry) {
        final LinkInject annotation = element.getAnnotation(LinkInject.class);

        final Link link;

        if (annotation != null) {
            final String title = getTitle(veneeredResource, annotation);

            link = (annotation.inherit() ? veneeredResource.getInherited(name, String.class) : veneeredResource.get(
                name, String.class))
                .map(path -> LinkBuilderFactory.forPath(path).setTitle(title).build())
                .orElse(null);
        } else {
            link = veneeredResource.get(name, String.class).map(path -> LinkBuilderFactory.forPath(path).build())
                .orElse(null);
        }

        return link;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final Object adaptable,
        final AnnotatedElement element) {
        // check if the element has the expected annotation
        final LinkInject annotation = element.getAnnotation(LinkInject.class);

        return annotation != null ? new LinkAnnotationProcessor(annotation) : null;
    }

    private String getTitle(final VeneeredResource veneeredResource, final LinkInject annotation) {
        return annotation.inherit() ? veneeredResource.getInherited(annotation.titleProperty(), "") :
            veneeredResource.get(annotation.titleProperty(), "");
    }

    static class LinkAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        private final LinkInject annotation;

        LinkAnnotationProcessor(final LinkInject annotation) {
            this.annotation = annotation;
        }

        @Override
        public InjectionStrategy getInjectionStrategy() {
            return annotation.injectionStrategy();
        }
    }
}
