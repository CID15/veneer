package org.cid15.aem.veneer.core.adapter;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.cid15.aem.veneer.api.page.VeneeredPage;
import org.cid15.aem.veneer.api.page.VeneeredPageManager;
import org.cid15.aem.veneer.core.page.impl.DefaultVeneeredPage;
import org.cid15.aem.veneer.core.page.impl.DefaultVeneeredPageManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import java.util.Optional;

@Component(service = AdapterFactory.class, property = {
    SlingConstants.PROPERTY_ADAPTABLE_CLASSES + "=org.apache.sling.api.resource.Resource",
    SlingConstants.PROPERTY_ADAPTABLE_CLASSES + "=org.apache.sling.api.resource.ResourceResolver",
    SlingConstants.PROPERTY_ADAPTABLE_CLASSES + "=com.day.cq.wcm.api.Page",
    SlingConstants.PROPERTY_ADAPTER_CLASSES + "=org.cid15.aem.veneer.api.page.VeneeredPageManager",
    SlingConstants.PROPERTY_ADAPTER_CLASSES + "=org.cid15.aem.veneer.api.page.VeneeredPage"
})
@ServiceDescription("Veneer Page Adapter Factory")
@SuppressWarnings("unchecked")
public final class VeneerPageAdapterFactory implements AdapterFactory {

    @Override
    public <AdapterType> AdapterType getAdapter(final Object adaptable, final Class<AdapterType> type) {
        AdapterType result = null;

        if (type == VeneeredPageManager.class && adaptable instanceof ResourceResolver) {
            result = (AdapterType) new DefaultVeneeredPageManager((ResourceResolver) adaptable);
        } else if (type == VeneeredPage.class) {
            if (adaptable instanceof Resource) {
                result = getVeneeredPageForResource((Resource) adaptable);
            } else if (adaptable instanceof Page) {
                result = (AdapterType) new DefaultVeneeredPage((Page) adaptable);
            }
        }

        return result;
    }

    private <AdapterType> AdapterType getVeneeredPageForResource(final Resource resource) {
        return (AdapterType) Optional.ofNullable(resource.adaptTo(Page.class))
            .map(DefaultVeneeredPage :: new)
            .orElse(null);
    }
}
