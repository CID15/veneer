package org.cid15.aem.veneer.core.servlets.optionsprovider;

import veneer.com.google.common.collect.ImmutableMap;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.cid15.aem.veneer.core.servlets.AbstractJsonResponseServlet;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static veneer.com.google.common.base.Preconditions.checkNotNull;

/**
 * Base class for providing a list of "options" to a component dialog widget.  An option is simply a text/value pair to
 * be rendered in a selection box.  The implementing class determines how these options are retrieved from the
 * repository (or external provider, such as a web service).
 */
public abstract class AbstractOptionsProviderServlet extends AbstractJsonResponseServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Get a list of "options" (text/value pairs) for rendering in an authoring dialog.  Building the list of options is
     * handled by the implementing class and will vary depending on the requirements for the component dialog calling
     * this servlet.
     *
     * @param request Sling request
     * @return list of options as determined by the implementing class
     */
    protected abstract List<Option> getOptions(final SlingHttpServletRequest request);

    /**
     * @param request Sling request
     * @return Optional name of root JSON object containing options
     */
    protected abstract Optional<String> getOptionsRoot(final SlingHttpServletRequest request);

    @Override
    protected final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws IOException {
        final List<Option> options = getOptions(request);

        checkNotNull(options, "option list must not be null");

        final Optional<String> optionsRoot = getOptionsRoot(request);

        if (optionsRoot.isPresent()) {
            writeJsonResponse(response, ImmutableMap.of(optionsRoot.get(), options));
        } else {
            writeJsonResponse(response, options);
        }
    }
}
