package org.cid15.aem.veneer.core.page.impl;

import org.apache.sling.api.resource.Resource;
import org.cid15.aem.veneer.api.page.VeneeredPage;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public final class VeneeredPageIterator implements Iterator<VeneeredPage> {

    private VeneeredPage nextPage;

    private final Iterator<Resource> base;

    private final Predicate<VeneeredPage> predicate;

    VeneeredPageIterator(final Iterator<Resource> base, final Predicate<VeneeredPage> predicate) {
        this.base = base;
        this.predicate = predicate;

        seek();
    }

    @Override
    public boolean hasNext() {
        return nextPage != null;
    }

    @Override
    public VeneeredPage next() {
        if (nextPage != null) {
            return seek();
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private VeneeredPage seek() {
        VeneeredPage prev = nextPage;

        nextPage = null;

        while (base.hasNext() && nextPage == null) {
            final Resource resource = base.next();

            nextPage = resource.adaptTo(VeneeredPage.class);

            if (nextPage != null && predicate != null && !predicate.test(nextPage)) {
                nextPage = null;
            }
        }

        return prev;
    }
}
