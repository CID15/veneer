package org.cid15.aem.veneer.core.dam.impl

import org.cid15.aem.veneer.core.specs.VeneerSpec

class AbstractVeneeredAssetSpec extends VeneerSpec {

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
                empty("dam:Asset") {

                }
            }
        }
    }
}
