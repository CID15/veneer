package org.cid15.aem.veneer.core.page.predicates;

import org.cid15.aem.veneer.api.page.VeneeredPage;

import java.util.function.Predicate;

import static veneer.com.google.common.base.Preconditions.checkNotNull;

/**
 * Predicate that filters on the value of the page's cq:template property.
 */
public final class TemplatePredicate implements Predicate<VeneeredPage> {

    private final String templatePath;

    public TemplatePredicate(final VeneeredPage page) {
        checkNotNull(page);

        templatePath = page.getTemplatePath();
    }

    public TemplatePredicate(final String templatePath) {
        checkNotNull(templatePath);

        this.templatePath = templatePath;
    }

    @Override
    public boolean test(final VeneeredPage page) {
        return templatePath.equals(page.getTemplatePath());
    }
}
