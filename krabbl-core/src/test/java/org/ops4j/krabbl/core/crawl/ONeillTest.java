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

import java.io.File;

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
public class ONeillTest {

    private CrawlerBuilder crawlerBuilder;
    private File outputDir;

    @Before
    public void before() {
        crawlerBuilder = CrawlerBuilder.builder();

        outputDir = new File("target", "oneill1001");
        outputDir.mkdirs();
    }

    @After
    public void after() {
        crawlerBuilder.close();
    }


    @Test
    public void shouldCrawlFolkies() {
        CrawlerConfiguration config = new CrawlerConfiguration();
        config.setMaxDepthOfCrawling(1);
        Crawler crawlController = crawlerBuilder.newCrawler(config, new ONeillVisitor(outputDir));
        crawlController.addSeed("http://ecf-guest.mit.edu/~jc/music/book/oneills/1001/X/");
        crawlController.start();
        crawlController.awaitTermination();
    }
}
