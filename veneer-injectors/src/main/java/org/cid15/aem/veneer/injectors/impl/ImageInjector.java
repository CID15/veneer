package org.cid15.aem.veneer.injectors.impl;

import com.day.cq.wcm.foundation.Image;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory2;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.cid15.aem.veneer.injectors.annotations.ImageInject;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedElement;

@Component(service = Injector.class)
public final class ImageInjector extends AbstractTypedVeneeredResourceInjector<Image> implements Injector,
    InjectAnnotationProcessorFactory2, AcceptsNullName {

    @Override
    public String getName() {
        return ImageInject.NAME;
    }

    @Override
    public Object getVeneeredResourceValue(final VeneeredResource veneeredResource, final String name,
        final Class<Image> declaredType, final AnnotatedElement element,
        final DisposalCallbackRegistry callbackRegistry) {
        final ImageInject annotation = element.getAnnotation(ImageInject.class);

        final boolean self = annotation != null && annotation.isSelf();

        final Resource resource;

        if (annotation != null && annotation.inherit()) {
            resource = veneeredResource.findAncestor(cn -> self ? cn.isHasImage() : cn.isHasImage(name))
                .map(VeneeredResource :: getResource)
                .orElse(null);
        } else {
            resource = veneeredResource.getResource();
        }

        Object value = null;

        if (resource != null) {
            final Image image;

            if (self) {
                image = new Image(resource);
            } else {
                image = new Image(resource, name);
            }

            if (image.hasContent()) {
                if (annotation != null) {
                    if (annotation.selectors().length > 0) {
                        image.setSelector("." + String.join(".", annotation.selectors()));
                    }
                } else {
                    image.setSelector(ImageInject.IMG_SELECTOR);
                }

                value = image;
            }
        }

        return value;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(final Object adaptable,
        final AnnotatedElement element) {
        // check if the element has the expected annotation
        final ImageInject annotation = element.getAnnotation(ImageInject.class);

        return annotation != null ? new ImageAnnotationProcessor(annotation) : null;
    }

    static class ImageAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

        private final ImageInject annotation;

        ImageAnnotationProcessor(final ImageInject annotation) {
            this.annotation = annotation;
        }

        @Override
        public InjectionStrategy getInjectionStrategy() {
            return annotation.injectionStrategy();
        }
    }
}
