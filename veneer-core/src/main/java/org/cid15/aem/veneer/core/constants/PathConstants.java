package org.cid15.aem.veneer.core.constants;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;

/**
 * JCR path and extension constants.
 */
public final class PathConstants {

    public static final String EXTENSION_HTML = "html";

    public static final String EXTENSION_JSON = "json";

    public static final String PATH_CONTENT = "/content";

    public static final String PATH_CONTENT_DAM = "/content/dam";

    public static final String PATH_JCR_CONTENT = "/" + JcrConstants.JCR_CONTENT;

    public static final String RELATIVE_PATH_METADATA = JcrConstants.JCR_CONTENT + "/" + DamConstants.METADATA_FOLDER;

    public static final String REGEX_CONTENT = PATH_CONTENT + "/([^/]+)/?(.+)?";

    public static final String SELECTOR = ".";

    private PathConstants() {

    }
}
