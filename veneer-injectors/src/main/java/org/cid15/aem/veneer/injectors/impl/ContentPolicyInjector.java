package org.cid15.aem.veneer.injectors.impl;

import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.cid15.aem.veneer.injectors.utils.InjectorUtils;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Injects the content policy for the current resource.
 */
@Component(service = Injector.class)
public final class ContentPolicyInjector implements Injector {

    @Override
    public String getName() {
        return "content-policy";
    }

    @Override
    public Object getValue(final Object adaptable, final String name, final Type type, final AnnotatedElement element,
        final DisposalCallbackRegistry registry) {
        Object value = null;

        if (type instanceof Class) {
            final Class clazz = (Class) type;

            if (clazz == ContentPolicy.class) {
                value = getContentPolicy(adaptable);
            }
        }

        return value;
    }

    private ContentPolicy getContentPolicy(final Object adaptable) {
        return Optional.ofNullable(InjectorUtils.getResource(adaptable))
            .map(resource -> resource.getResourceResolver().adaptTo(ContentPolicyManager.class).getPolicy(resource))
            .orElse(null);
    }
}
