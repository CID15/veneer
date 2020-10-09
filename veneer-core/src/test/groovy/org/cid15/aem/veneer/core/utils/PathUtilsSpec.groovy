package org.cid15.aem.veneer.core.utils

import org.cid15.aem.veneer.core.specs.VeneerSpec
import spock.lang.Unroll

@Unroll
class PathUtilsSpec extends VeneerSpec {

    def "is content"() {
        expect:
        PathUtils.isContent(path) == result

        where:
        path                      | result
        "http://www.google.com"   | false
        "notcontent"              | false
        "/content"                | false
        "/content/"               | false
        "/content/global"         | true
        "/content/global/en"      | true
        "/content/global/en/test" | true
    }

    def "is external"() {
        expect:
        PathUtils.isExternal(path) == result

        where:
        path                      | result
        "http://www.google.com"   | true
        "http://"                 | true
        "notcontent"              | true
        "/content/global"         | false
        "/content/global/en"      | false
        "/content/global/en/test" | false
    }

    def "is external strict"() {
        setup:
        nodeBuilder.etc {
            designs()
        }

        expect:
        PathUtils.isExternal(path, resourceResolver) == result

        where:
        path                    | result
        "http://www.google.com" | true
        "notcontent"            | true
        "/etc/foo"              | true
        "/etc/designs"          | false
    }
}