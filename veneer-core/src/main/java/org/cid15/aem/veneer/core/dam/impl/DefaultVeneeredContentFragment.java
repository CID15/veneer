package org.cid15.aem.veneer.core.dam.impl;

import com.adobe.cq.dam.cfm.ContentFragment;
import org.cid15.aem.veneer.api.dam.VeneeredContentFragment;

public final class DefaultVeneeredContentFragment extends AbstractVeneeredAsset implements VeneeredContentFragment {

    private final ContentFragment delegate;

    public DefaultVeneeredContentFragment(final ContentFragment delegate) {
        super(delegate);

        this.delegate = delegate;
    }

    @Override
    public ContentFragment getContentFragment() {
        return delegate;
    }
}
