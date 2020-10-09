package org.cid15.aem.veneer.core.dam.impl

import com.day.cq.dam.api.Asset
import spock.lang.Unroll

@Unroll
class DefaultVeneeredAssetSpec extends AbstractVeneeredAssetSpec {

    def "get asset"() {
        setup:
        def delegate = getResource("/content/dam/asset").adaptTo(Asset)
        def veneeredAsset = new DefaultVeneeredAsset(delegate)

        expect:
        veneeredAsset.asset.path == "/content/dam/asset"
    }

    def "get content resource"() {
        setup:
        def delegate = getResource(path).adaptTo(Asset)
        def veneeredAsset = new DefaultVeneeredAsset(delegate)

        expect:
        veneeredAsset.contentResource.present == hasContentResource

        where:
        path                 | hasContentResource
        "/content/dam/asset" | true
        "/content/dam/empty" | false
    }

    def "get metadata resource"() {
        setup:
        def delegate = getResource(path).adaptTo(Asset)
        def veneeredAsset = new DefaultVeneeredAsset(delegate)

        expect:
        veneeredAsset.metadataResource.present == hasMetadataResource

        where:
        path                 | hasMetadataResource
        "/content/dam/asset" | true
        "/content/dam/empty" | false
    }
}
