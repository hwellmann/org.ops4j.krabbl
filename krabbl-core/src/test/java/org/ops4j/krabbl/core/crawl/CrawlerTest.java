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

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.krabbl.api.Crawler;
import org.ops4j.krabbl.api.CrawlerBuilder;
import org.ops4j.krabbl.api.CrawlerConfiguration;

/**
 * @author Harald Wellmann
 *
 */
public class CrawlerTest {

    private CrawlerBuilder crawlerBuilder;

    @Before
    public void before() {
        crawlerBuilder = CrawlerBuilder.builder();
    }

    @After
    public void after() {
        crawlerBuilder.close();
    }


    @Test
    public void shouldCrawlOps4j() {
        CrawlerConfiguration config = new CrawlerConfiguration();
        config.setMaxDepthOfCrawling(2);
        Crawler crawlController = crawlerBuilder.newCrawler(config,
            new TestVisitor("ops4j.github.io"));
        crawlController.addSeed("http://ops4j.github.io");
        crawlController.start();
        crawlController.awaitTermination();
    }

    @Test
    public void shouldCrawlGithub() throws InterruptedException {
        CrawlerConfiguration config = new CrawlerConfiguration();
        config.setMaxDepthOfCrawling(2);
        Crawler crawlController = crawlerBuilder.newCrawler(config, new TestVisitor("github.com"));
        crawlController.addSeed("http://github.com");
        crawlController.start();
        TimeUnit.SECONDS.sleep(5);
        crawlController.shutdown();
        crawlController.awaitTermination();
    }

    @Test
    public void shouldHandleMetaRefresh() {
        CrawlerConfiguration config = new CrawlerConfiguration();
        config.setMaxDepthOfCrawling(2);
        Crawler crawlController = crawlerBuilder.newCrawler(config,
            new TestVisitor("ops4j.github.io"));
        crawlController.addSeed("http://ops4j.github.io/dadl/latest/");
        crawlController.start();
        crawlController.awaitTermination();
    }

    @Test
    public void shouldCrawlWikipedia() {
        CrawlerConfiguration config = new CrawlerConfiguration();
        config.setMaxDepthOfCrawling(2);
        config.setMaxPagesToFetch(20);
        Crawler crawlController = crawlerBuilder.newCrawler(config, new TestVisitor("de", "wikipedia.org"));
        crawlController.addSeed("http://de.wikipedia.org");
        crawlController.start();
        crawlController.awaitTermination();
    }
}
