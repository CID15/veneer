package org.cid15.aem.veneer.core.dam.impl;

import com.day.cq.dam.api.Asset;
import org.cid15.aem.veneer.api.dam.VeneeredAsset;

public final class DefaultVeneeredAsset extends AbstractVeneeredAsset implements VeneeredAsset {

    private final Asset delegate;

    public DefaultVeneeredAsset(final Asset delegate) {
        super(delegate);

        this.delegate = delegate;
    }

    @Override
    public Asset getAsset() {
        return delegate;
    }
}
