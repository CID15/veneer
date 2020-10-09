package org.cid15.aem.veneer.core.dam.impl

import com.adobe.cq.dam.cfm.ContentFragment
import org.apache.sling.api.resource.Resource
import spock.lang.Unroll

@Unroll
class DefaultVeneeredContentFragmentSpec extends AbstractVeneeredAssetSpec {

    def setupSpec() {
        nodeBuilder.content {
            dam("sling:Folder") {
                empty("dam:Asset") {
                    "jcr:content"()
                }
            }
        }
    }

    def "get content fragment"() {
        setup:
        def delegate = getResource("/content/dam/asset").adaptTo(ContentFragment)
        def veneeredContentFragment = new DefaultVeneeredContentFragment(delegate)

        expect:
        veneeredContentFragment.contentFragment.adaptTo(Resource).path == "/content/dam/asset"
    }

    def "get content resource"() {
        setup:
        def delegate = getResource("/content/dam/asset").adaptTo(ContentFragment)
        def veneeredContentFragment = new DefaultVeneeredContentFragment(delegate)

        expect:
        veneeredContentFragment.contentResource.present
    }

    def "get metadata resource"() {
        setup:
        def delegate = getResource("/content/dam/asset").adaptTo(ContentFragment)
        def veneeredContentFragment = new DefaultVeneeredContentFragment(delegate)

        expect:
        veneeredContentFragment.metadataResource.present
    }
}
