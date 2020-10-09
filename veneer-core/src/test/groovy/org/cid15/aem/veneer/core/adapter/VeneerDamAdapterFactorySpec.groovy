package org.cid15.aem.veneer.core.adapter

import com.adobe.cq.dam.cfm.ContentFragment
import com.day.cq.dam.api.Asset
import org.cid15.aem.veneer.api.dam.VeneeredAsset
import org.cid15.aem.veneer.api.dam.VeneeredContentFragment
import org.cid15.aem.veneer.core.specs.VeneerSpec
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
class VeneerDamAdapterFactorySpec extends VeneerSpec {

    @Shared
    VeneerDamAdapterFactory adapterFactory = new VeneerDamAdapterFactory()

    def setupSpec() {
        nodeBuilder.content {
            dam("sling:Folder") {
                asset("dam:Asset") {
                    "jcr:content" {
                        metadata()
                        renditions("nt:folder") {
                            original("nt:file") {
                                "jcr:content"("nt:resource", "jcr:data": "data")
                            }
                        }
                    }
                }
            }
        }
    }

    def "get veneered asset"() {
        expect:
        adapterFactory.getAdapter(adaptable, VeneeredAsset)

        where:
        adaptable << [getResource("/content/dam/asset"), getResource("/content/dam/asset").adaptTo(Asset)]
    }

    def "get veneered content fragment"() {
        expect:
        adapterFactory.getAdapter(adaptable, VeneeredContentFragment)

        where:
        adaptable << [getResource("/content/dam/asset"), getResource("/content/dam/asset").adaptTo(ContentFragment)]
    }

    def "get adapter returns null for non-asset node"() {
        setup:
        def resource = resourceResolver.getResource("/")

        expect:
        !adapterFactory.getAdapter(resource, type)

        where:
        type << [VeneeredAsset, VeneeredContentFragment]
    }

    def "get adapter for resource with invalid type returns null"() {
        setup:
        def resource = resourceResolver.getResource("/")

        expect:
        !adapterFactory.getAdapter(resource, String)
    }

    def "get invalid adapter returns null"() {
        expect:
        !adapterFactory.getAdapter("", String)
    }
}
