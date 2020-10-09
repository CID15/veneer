package org.cid15.aem.veneer.core.servlets.datasource;

import com.day.cq.commons.Filter;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.cid15.aem.veneer.core.servlets.optionsprovider.Option;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Extends the AbstractOptionsDataSourceServlet and solely focuses on building options from tags within the repository.
 * A basic extension is to provide just the namespace of the tags and the servlet will build a list of options from all
 * direct descendants of that namespace tag. Extending classes may optionally provide a more granular tag path and a
 * custom filter.
 */
abstract class AbstractTagDataSourceServlet extends AbstractOptionsDataSourceServlet {

    private static final Filter<Tag> TAG_FILTER_INCLUDE_ALL = tag -> true;

    @Override
    protected List<Option> getOptions(final SlingHttpServletRequest request) {
        final TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
        final Tag containerTag = tagManager.resolve(getNamespace() + getContainerTagRelativePath());

        final List<Option> options = new ArrayList<>();

        if (containerTag != null) {
            final Iterator<Tag> iterator = containerTag.listChildren(getTagFilter());

            while (iterator.hasNext()) {
                final Tag tag = iterator.next();

                options.add(new Option(tag.getTagID(), tag.getTitle()));
            }
        }

        return options;
    }

    /**
     * The string value of the tag namespace with a colon at the end, e.g. "colors:". A namespace tag is any tag that is
     * a direct child of the taxonomy root node (/etc/tags).
     *
     * @return the node name of the namespace tag.
     */
    protected abstract String getNamespace();

    /**
     * Override this method to provide the path of the parent tag containing all child tags to be returned by the
     * servlet. Defaults to an empty string if not overridden, resulting in all child tags directly under the namespace
     * tag being returned.
     *
     * @return the path of the containing tag
     */
    protected String getContainerTagRelativePath() {
        return "";
    }

    /**
     * Override this method to provide a filter for the list of tags returned by the servlet. Defaults to an all
     * inclusive filter if not overridden.
     *
     * @return the tag filter to use when building the list of tags returned by the servlet.
     */
    protected Filter<Tag> getTagFilter() {
        return TAG_FILTER_INCLUDE_ALL;
    }
}
