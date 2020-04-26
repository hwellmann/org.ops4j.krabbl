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
import java.util.Arrays;

import org.apache.http.message.BasicHeader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.krabbl.api.Crawler;
import org.ops4j.krabbl.api.CrawlerBuilder;
import org.ops4j.krabbl.api.CrawlerConfiguration;
import org.ops4j.krabbl.api.HttpClientConfiguration;

/**
 * @author Harald Wellmann
 *
 */
public class FolkiesTest {

    private CrawlerBuilder crawlerBuilder;
    private File outputDir;

    @Before
    public void before() {
        HttpClientConfiguration httpConfig = new HttpClientConfiguration();
        httpConfig.setDefaultHeaders(Arrays.asList(new BasicHeader("Cookie", "cookieconsent_status=dismiss; groupsio=MTU2ODQ4MDAyMnxEdi1CQkFFQ180SUFBUkFCRUFBQVFmLUNBQUlHYzNSeWFXNW5EQWdBQm1OdmIydHBaUVZwYm5RMk5BUUtBUGpDU2tMN2lUUUhhZ1p6ZEhKcGJtY01CQUFDYVdRRmFXNTBOalFFQlFEOU5zbDJ8yLFYL3l3Kot8fg75cOUTsfp0EcPY73EGq-FQ0nKgU7I=; haslogin=y")));
        crawlerBuilder = CrawlerBuilder.builder();
        crawlerBuilder.setHttpClientConfiguration(httpConfig);

        outputDir = new File("target", "tunes");
        outputDir.mkdirs();
    }

    @After
    public void after() {
        crawlerBuilder.close();
    }


    @Test
    public void shouldCrawlFolkies() {
        CrawlerConfiguration config = new CrawlerConfiguration();
        config.setMaxDepthOfCrawling(3);
        Crawler crawlController = crawlerBuilder.newCrawler(config, new FolkiesVisitor(outputDir));
        crawlController.addSeed("https://groups.io/g/folkies/files/");
        crawlController.start();
        crawlController.awaitTermination();
    }
}
