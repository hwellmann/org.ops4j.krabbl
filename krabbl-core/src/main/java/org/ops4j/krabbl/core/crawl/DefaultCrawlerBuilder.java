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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.ops4j.krabbl.api.Crawler;
import org.ops4j.krabbl.api.CrawlerBuilder;
import org.ops4j.krabbl.api.CrawlerConfiguration;
import org.ops4j.krabbl.api.HttpClientConfiguration;
import org.ops4j.krabbl.api.PageVisitor;
import org.ops4j.krabbl.core.fetch.PageFetcher;

/**
 * @author Harald Wellmann
 *
 */
public class DefaultCrawlerBuilder extends CrawlerBuilder  {

    private HttpClientConfiguration httpClientConfiguration;

    private ScheduledExecutorService executor;

    private PageFetcher pageFetcher;

    private synchronized ScheduledExecutorService getExecutor() {
        if (executor == null) {
            executor = Executors.newScheduledThreadPool(2);
        }
        return executor;
    }

    @Override
    public void setExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    private synchronized HttpClientConfiguration getHttpClientConfiguration() {
        if (httpClientConfiguration == null) {
            httpClientConfiguration = new HttpClientConfiguration();
        }
        return httpClientConfiguration;
    }

    @Override
    public void setHttpClientConfiguration(HttpClientConfiguration httpClientConfiguration) {
        this.httpClientConfiguration = httpClientConfiguration;
    }

    private synchronized PageFetcher pageFetcher() {
        if (pageFetcher == null) {
            pageFetcher = new PageFetcher(getHttpClientConfiguration());
        }
        return pageFetcher;
    }

    @Override
    public Crawler newCrawler(CrawlerConfiguration config, PageVisitor pageVisitor) {
        InMemoryFrontier frontier = new InMemoryFrontier();
        PageProcessor pageProcessor = new PageProcessor(config, pageVisitor, frontier,
            pageFetcher());
        return new DefaultCrawler(config, pageVisitor, getExecutor(), frontier, pageProcessor);
    }

    @Override
    public int getPriority() {
        return DEFAULT_PRIORITY;
    }
}
