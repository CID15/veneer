package org.cid15.aem.veneer.injectors.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.scripting.WCMBindingsConstants;
import com.google.common.collect.ImmutableList;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.cid15.aem.veneer.api.Accessible;
import org.cid15.aem.veneer.api.page.VeneeredPage;
import org.cid15.aem.veneer.api.page.VeneeredPageManager;
import org.cid15.aem.veneer.api.resource.VeneeredResource;
import org.cid15.aem.veneer.injectors.utils.InjectorUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

/**
 * Injector for objects derived from the current component context.
 */
@Component(service = Injector.class)
@ServiceRanking(Integer.MIN_VALUE)
public final class ComponentInjector implements Injector {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentInjector.class);

    private static final List<Class> REQUEST_INJECTABLES = ImmutableList.of(
        Page.class,
        VeneeredPage.class,
        VeneeredResource.class,
        ValueMap.class,
        WCMMode.class
    );

    private static final List<Class> RESOURCE_INJECTABLES = ImmutableList.of(
        ResourceResolver.class,
        ValueMap.class,
        VeneeredResource.class,
        Page.class,
        VeneeredPage.class
    );

    @Override
    public String getName() {
        return "component";
    }

    @Override
    public Object getValue(final Object adaptable, final String name, final Type type, final AnnotatedElement element,
        final DisposalCallbackRegistry registry) {
        Object value = null;

        if (type instanceof Class) {
            final Class clazz = (Class) type;

            if (adaptable instanceof SlingHttpServletRequest) {
                // get request adaptable
                if (REQUEST_INJECTABLES.contains(clazz)) {
                    value = getValueForRequest(clazz, adaptable);
                } else if (RESOURCE_INJECTABLES.contains(clazz)) {
                    value = getValueForResource(clazz, adaptable);
                } else {
                    LOG.debug("class : {} is not supported by this injector for request", clazz.getName());
                }
            } else if (adaptable instanceof Resource) {
                // get resource adaptable
                if (RESOURCE_INJECTABLES.contains(clazz)) {
                    value = getValueForResource(clazz, adaptable);
                } else {
                    LOG.debug("class : {} is not supported by this injector for resource", clazz.getName());
                }
            }
        }

        return value;
    }

    private Object getValueForRequest(final Class clazz, final Object adaptable) {
        final SlingHttpServletRequest request = InjectorUtils.getRequest(adaptable);

        Object value = null;

        if (clazz == WCMMode.class) {
            value = WCMMode.fromRequest(request);
        } else if (clazz == VeneeredPage.class || clazz == Page.class) {
            // get currentPage from sling bindings to ensure that the resource of the rendering page is used for
            // structural components on editable templates
            final Page currentPage = Optional.ofNullable(
                (SlingBindings) request.getAttribute(SlingBindings.class.getName()))
                .map(bindings -> (Page) bindings.get(WCMBindingsConstants.NAME_CURRENT_PAGE))
                .orElse(null);

            if (clazz == VeneeredPage.class) {
                value = Optional.ofNullable(currentPage)
                    .map(page -> page.adaptTo(VeneeredPage.class))
                    .orElse(null);
            } else {
                value = currentPage;
            }
        } else if (clazz == VeneeredResource.class || clazz == ValueMap.class) {
            // get resource from sling bindings to ensure that the resource of the rendering page is used for
            // structural components on editable templates
            final VeneeredResource veneeredResource = Optional.ofNullable(
                (SlingBindings) request.getAttribute(SlingBindings.class.getName()))
                .map(bindings -> (Resource) bindings.get(SlingBindings.RESOURCE))
                .map(resource -> resource.adaptTo(VeneeredResource.class))
                .orElse(null);

            if (clazz == ValueMap.class) {
                value = Optional.ofNullable(veneeredResource)
                    .map(Accessible :: getProperties)
                    .orElse(null);
            } else {
                value = veneeredResource;
            }
        }

        LOG.debug("injecting class : {} with instance : {}", clazz.getName(), value);

        return value;
    }

    private Object getValueForResource(final Class clazz, final Object adaptable) {
        final Resource resource = InjectorUtils.getResource(adaptable);

        Object value = null;

        if (clazz == ResourceResolver.class) {
            value = resource.getResourceResolver();
        } else if (clazz == ValueMap.class) {
            value = resource.getValueMap();
        } else if (clazz == VeneeredResource.class) {
            value = resource.adaptTo(VeneeredResource.class);
        } else if (clazz == VeneeredPage.class) {
            value = resource.getResourceResolver().adaptTo(VeneeredPageManager.class).getContainingVeneeredPage(
                resource);
        } else if (clazz == Page.class) {
            value = resource.getResourceResolver().adaptTo(PageManager.class).getContainingPage(resource);
        }

        LOG.debug("injecting class : {} with instance : {}", clazz.getName(), value);

        return value;
    }
}
