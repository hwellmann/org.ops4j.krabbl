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
package org.ops4j.krabbl.core;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.ops4j.krabbl.api.CrawlerConfiguration;
import org.ops4j.krabbl.api.Page;
import org.ops4j.krabbl.api.PageVisitor;
import org.ops4j.krabbl.api.WebTarget;
import org.ops4j.krabbl.core.crawl.DefaultCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 *
 */
public class CrawlerTest {

    private static Logger log = LoggerFactory.getLogger(CrawlerTest.class);

    public static class TestVisitor implements PageVisitor {

        private String domain;

        public TestVisitor(String domain) {
            this.domain = domain;
        }

        @Override
        public boolean shouldVisit(Page referringPage, WebTarget url) {
            return url.getDomain().equals(domain);
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

    @Test
    public void shouldCrawlOps4j() {
        CrawlerConfiguration config = new CrawlerConfiguration();
        config.setMaxDepthOfCrawling(2);
        DefaultCrawler crawlController = new DefaultCrawler(config,
            new TestVisitor("ops4j.github.io"));
        crawlController.addSeed("http://ops4j.github.io");
        crawlController.start();
        crawlController.waitUntilFinish();
    }

    @Test
    public void shouldCrawlGithub() throws InterruptedException {
        CrawlerConfiguration config = new CrawlerConfiguration();
        config.setMaxDepthOfCrawling(2);
        DefaultCrawler crawlController = new DefaultCrawler(config, new TestVisitor("github.com"));
        crawlController.addSeed("http://github.com");
        crawlController.start();
        TimeUnit.SECONDS.sleep(5);
        crawlController.shutdown();
        crawlController.waitUntilFinish();
    }

    @Test
    public void shouldHandleMetaRefresh() {
        CrawlerConfiguration config = new CrawlerConfiguration();
        config.setMaxDepthOfCrawling(2);
        DefaultCrawler crawlController = new DefaultCrawler(config,
            new TestVisitor("ops4j.github.io"));
        crawlController.addSeed("http://ops4j.github.io/dadl/latest/");
        crawlController.start();
        crawlController.waitUntilFinish();
    }

}
