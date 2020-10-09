package org.cid15.aem.veneer.core.adapter;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.dam.api.Asset;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.cid15.aem.veneer.api.dam.VeneeredAsset;
import org.cid15.aem.veneer.api.dam.VeneeredContentFragment;
import org.cid15.aem.veneer.core.dam.impl.DefaultVeneeredAsset;
import org.cid15.aem.veneer.core.dam.impl.DefaultVeneeredContentFragment;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

import java.util.Optional;

@Component(service = AdapterFactory.class, property = {
    SlingConstants.PROPERTY_ADAPTABLE_CLASSES + "=org.apache.sling.api.resource.Resource",
    SlingConstants.PROPERTY_ADAPTABLE_CLASSES + "=com.day.cq.dam.api.Asset",
    SlingConstants.PROPERTY_ADAPTABLE_CLASSES + "=com.adobe.cq.dam.cfm.ContentFragment",
    SlingConstants.PROPERTY_ADAPTER_CLASSES + "=org.cid15.aem.veneer.api.dam.VeneeredAsset",
    SlingConstants.PROPERTY_ADAPTER_CLASSES + "=org.cid15.aem.veneer.api.dam.VeneeredContentFragment"
})
@ServiceDescription("Veneer DAM Adapter Factory")
@SuppressWarnings("unchecked")
public final class VeneerDamAdapterFactory implements AdapterFactory {

    @Override
    public <AdapterType> AdapterType getAdapter(final Object adaptable, final Class<AdapterType> type) {
        AdapterType result = null;

        if (type == VeneeredAsset.class) {
            result = getVeneeredAsset(adaptable);
        } else if (type == VeneeredContentFragment.class) {
            result = getVeneeredContentFragment(adaptable);
        }

        return result;
    }

    private <AdapterType> AdapterType getVeneeredAsset(final Object adaptable) {
        Asset asset = null;

        if (adaptable instanceof Resource) {
            asset = ((Resource) adaptable).adaptTo(Asset.class);
        } else if (adaptable instanceof Asset) {
            asset = (Asset) adaptable;
        }

        return (AdapterType) Optional.ofNullable(asset)
            .map(DefaultVeneeredAsset :: new)
            .orElse(null);
    }

    private <AdapterType> AdapterType getVeneeredContentFragment(final Object adaptable) {
        ContentFragment contentFragment = null;

        if (adaptable instanceof Resource) {
            contentFragment = ((Resource) adaptable).adaptTo(ContentFragment.class);
        } else if (adaptable instanceof ContentFragment) {
            contentFragment = (ContentFragment) adaptable;
        }

        return (AdapterType) Optional.ofNullable(contentFragment)
            .map(DefaultVeneeredContentFragment :: new)
            .orElse(null);
    }
}
