package org.cid15.aem.veneer.core.page.impl;

import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import veneer.com.google.common.base.Stopwatch;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.cid15.aem.veneer.api.page.VeneeredPage;
import org.cid15.aem.veneer.api.page.VeneeredPageManager;
import org.cid15.aem.veneer.core.page.predicates.TemplatePredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.RowIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static veneer.com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class DefaultVeneeredPageManager implements VeneeredPageManager {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultVeneeredPageManager.class);

    private final ResourceResolver resourceResolver;

    private final PageManager pageManager;

    public DefaultVeneeredPageManager(final ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;

        pageManager = resourceResolver.adaptTo(PageManager.class);
    }

    @Override
    public PageManager getPageManager() {
        return pageManager;
    }

    @Override
    public List<VeneeredPage> findPages(final String rootPath, final Collection<String> tagIds,
        final boolean matchOne) {
        checkNotNull(rootPath);
        checkNotNull(tagIds);

        LOG.debug("path : {}, tag IDs : {}", rootPath, tagIds);

        final Stopwatch stopwatch = Stopwatch.createStarted();

        final RangeIterator<Resource> iterator = resourceResolver.adaptTo(TagManager.class).find(rootPath,
            tagIds.toArray(new String[0]), matchOne);

        final List<VeneeredPage> pages = new ArrayList<>();

        while (iterator.hasNext()) {
            final Resource resource = iterator.next();

            if (JcrConstants.JCR_CONTENT.equals(resource.getName())) {
                final VeneeredPage page = getPage(resource.getParent().getPath());

                if (page != null) {
                    pages.add(page);
                }
            }
        }

        LOG.debug("found {} result(s) in {}ms", pages.size(), stopwatch.elapsed(MILLISECONDS));

        return pages;
    }

    @Override
    public List<VeneeredPage> findPages(final String rootPath, final String templatePath) {
        return findPages(rootPath, new TemplatePredicate(templatePath));
    }

    @Override
    public List<VeneeredPage> findPages(final String rootPath, final Predicate<VeneeredPage> predicate) {
        final Stopwatch stopwatch = Stopwatch.createStarted();

        final List<VeneeredPage> pages = Optional.ofNullable(getPage(checkNotNull(rootPath)))
            .map(page -> page.findDescendants(predicate))
            .orElse(Collections.emptyList());

        stopwatch.stop();

        LOG.debug("found {} result(s) in {}ms", pages.size(), stopwatch.elapsed(MILLISECONDS));

        return pages;
    }

    @Override
    public VeneeredPage getContainingPage(final Resource resource) {
        return getPage(pageManager.getContainingPage(resource));
    }

    @Override
    public VeneeredPage getContainingPage(final String path) {
        return getPage(pageManager.getContainingPage(path));
    }

    @Override
    public VeneeredPage getPage(final Page page) {
        return Optional.ofNullable(page)
            .map(p -> p.adaptTo(VeneeredPage.class))
            .orElse(null);
    }

    @Override
    public VeneeredPage getPage(final String path) {
        return getPage(pageManager.getPage(path));
    }

    @Override
    public List<VeneeredPage> search(final Query query) {
        return search(query, -1);
    }

    @Override
    public List<VeneeredPage> search(final Query query, final int limit) {
        checkNotNull(query);

        LOG.debug("query statement : {}", query.getStatement());

        final Stopwatch stopwatch = Stopwatch.createStarted();

        final List<VeneeredPage> pages = new ArrayList<>();

        int count = 0;

        try {
            final Set<String> paths = new HashSet<>();

            final RowIterator rows = query.execute().getRows();

            while (rows.hasNext()) {
                final String path = rows.nextRow().getPath();

                if (limit == -1 || count < limit) {
                    LOG.debug("result path : {}", path);

                    final VeneeredPage page = getContainingPage(path);

                    if (page != null) {
                        // ensure no duplicate pages are added
                        if (!paths.contains(page.getPath())) {
                            paths.add(page.getPath());
                            pages.add(page);
                            count++;
                        }
                    } else {
                        LOG.error("result is null for path : {}", path);
                    }
                }
            }

            stopwatch.stop();

            LOG.debug("found {} result(s) in {}ms", pages.size(), stopwatch.elapsed(MILLISECONDS));
        } catch (RepositoryException re) {
            LOG.error("error finding pages for query : {}", query.getStatement(), re);
        }

        return pages;
    }
}
