/*
 * Copyright 2017 OPS4J Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.krabbl.core.crawl;

import org.ops4j.krabbl.api.Page;
import org.ops4j.krabbl.api.PageVisitor;
import org.ops4j.krabbl.api.WebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestVisitor implements PageVisitor {

    private static Logger log = LoggerFactory.getLogger(TestVisitor.class);


    private String domain;
    private String subdomain;

    public TestVisitor(String subdomain, String domain) {
        this.subdomain = subdomain;
        this.domain = domain;
    }

    public TestVisitor(String domain) {
        this.domain = domain;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebTarget url) {
        if (url.getPath().contains(":") && !url.getPath().contains("Hauptseite")) {
            return false;
        }
        boolean subdomainMatches = subdomain == null || subdomain.equals(url.getSubdomain());
        return subdomainMatches && domain.equals(url.getDomain());
    }

    @Override
    public void visit(Page page) {
        int numLinks = 0;
        if (page.getParseData() != null) {
            numLinks = page.getParseData().getOutgoingUrls().size();
        }
        log.info("Visiting {} at depth {} with {} outgoing links", page.getWebTarget().getUrl(),
            page.getWebTarget().getDepth(), numLinks);
    }
}