package org.cid15.aem.veneer.core.servlets.datasource;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.google.common.collect.Maps;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.cid15.aem.veneer.core.servlets.optionsprovider.Option;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;

/**
 * Base class for supplying a data source to component dialogs using the Touch UI.  Implementing classes will provide a
 * list of options that will be made available as text/value pairs to selection dialog elements.  Servlets must be
 * annotated with the <code>@SlingServlet(resourceTypes = "projectname/datasources/colors")</code> annotation.  The
 * resource type attribute is an arbitrary relative path that can be referenced by dialog elements using the data
 * source.  The implementing class determines how these options are retrieved from the repository (or external provider,
 * such as a web service).
 */
public abstract class AbstractOptionsDataSourceServlet extends SlingSafeMethodsServlet {

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

    @Override
    protected final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        final ResourceResolver resourceResolver = request.getResourceResolver();

        final List<Option> options = getOptions(request);

        // transform the list of options into a list of synthetic resources
        final List<Resource> resources = options
            .stream()
            .map(option -> {
                final Map<String, Object> map = Maps.newHashMapWithExpectedSize(options.size());

                map.put("value", option.getValue());
                map.put("text", option.getText());

                final ValueMap valueMap = new ValueMapDecorator(map);

                return new ValueMapResource(resourceResolver, new ResourceMetadata(), NT_UNSTRUCTURED, valueMap);
            })
            .collect(Collectors.toList());

        final DataSource dataSource = new SimpleDataSource(resources.iterator());

        request.setAttribute(DataSource.class.getName(), dataSource);
    }
}
