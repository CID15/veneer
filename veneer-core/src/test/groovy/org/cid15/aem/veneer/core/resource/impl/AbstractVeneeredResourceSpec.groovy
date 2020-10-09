package org.cid15.aem.veneer.core.resource.impl

import com.day.cq.tagging.TagConstants
import org.apache.sling.jcr.resource.api.JcrResourceConstants
import org.cid15.aem.veneer.core.specs.VeneerSpec

abstract class AbstractVeneeredResourceSpec extends VeneerSpec {

    def setupSpec() {
        pageBuilder.content {
            cid15("CID15") {
                "jcr:content"(otherPagePath: "/content/ales/esb", nonExistentPagePath: "/content/home",
                    externalPath: "http://www.reddit.com", multiValue: ["one", "two"],
                    fileReference: "/content/dam/image",
                    "cq:tags": ["cid15:experience"] as String[],
                    tags: ["cid15:experience", "cid15:commerce"] as String[]) {
                    image(fileReference: "/content/dam/image")
                    secondimage(fileReference: "/content/dam/image")
                    thirdimage()
                    nsfwImage(fileReference: "omg.png")
                    imageWithRenditions(fileReference: "/content/dam/image-renditions")
                    beer(label: "orval", abv: "9.0", oz: "12")
                    whiskey("sling:resourceType": "rye")
                    malort {
                        one("sling:resourceType": "won")
                        two("sling:resourceType": "tew")
                    }
                }
                about()
            }
            ales {
                esb("ESB") {
                    "jcr:content"(otherPagePath: "/content/cid15", externalPath: "http://www.reddit.com",
                        pagePaths: ["/content/cid15", "/content/ales"] as String[],
                        tags: ["cid15:experience"] as String[]) {
                        secondimage(fileReference: "/content/dam/image")
                        fullers("sling:resourceType": "bitter")
                        morland("sling:resourceType": "bitter")
                        greeneking("sling:resourceType": "bitter") {
                            image(fileReference: "/content/dam/image")
                        }
                    }
                    suds {
                        "jcr:content"(otherPagePath: "") {
                            container {
                                child1("jcr:title": "Zeus")
                                child2()
                            }
                        }
                        pint {
                            keg {
                                "jcr:content" {
                                    container()
                                }
                            }
                            barrel {
                                "jcr:content" {
                                    container {
                                        child1()
                                    }
                                }
                            }
                        }
                    }
                    bar {
                        "jcr:content" {
                            wood {
                                container {
                                    pine()
                                    spruce()
                                    maple()
                                }
                            }
                        }
                        tree {
                            "jcr:content" {
                                wood()
                            }
                        }
                    }
                    lace {
                        "jcr:content"() {
                            parent {
                                child1("sling:resourceType": "unknown")
                                child2("sling:resourceType": "unknown")
                                child3("sling:resourceType": "known")
                            }
                        }
                    }
                }
            }
            lagers {
                "jcr:content"(otherPagePath: "/content/cid15") {
                    dynamo("sling:resourceType": "us", related: "/content/lagers/jcr:content/spaten")
                    stiegl("sling:resourceType": "de",
                        related: ["/content/lagers/jcr:content/spaten", "/content/lagers/jcr:content/dynamo"] as
                            String[])
                    spaten("sling:resourceType": "de")
                }
            }
            inheritance {
                "jcr:content"("jcr:title": "Inheritance") {
                    component("jcr:title": "Component", "number": 5, "boolean": false) {
                        image(fileReference: "/content/dam/image")
                        secondimage(fileReference: "/content/dam/image")
                        insidecomponent(fileReference: "/content/dam/image")
                    }
                }
                child {
                    "jcr:content" {
                        component() {
                            insidecomponent()
                        }
                        other()
                    }
                }
            }
        }

        nodeBuilder.content {
            "cq:tags"(JcrResourceConstants.NT_SLING_FOLDER) {
                cid15(TagConstants.NT_TAG) {
                    experience(TagConstants.NT_TAG)
                    commerce(TagConstants.NT_TAG)
                }
            }
        }

        nodeBuilder.content {
            dam("sling:Folder") {
                image("dam:Asset") {
                    "jcr:content"("jcr:data": "data") {
                        renditions("nt:folder") {
                            original("nt:file") {
                                "jcr:content"("nt:resource", "jcr:data": "data")
                            }
                        }
                    }
                }
                "image-renditions"("dam:Asset") {
                    "jcr:content"("jcr:data": "data") {
                        renditions("nt:folder") {
                            original("nt:file") {
                                "jcr:content"("nt:resource", "jcr:data": "data")
                            }
                            one("nt:file") {
                                "jcr:content"("nt:resource", "jcr:data": "data")
                            }
                        }
                    }
                }
            }
        }
    }
}