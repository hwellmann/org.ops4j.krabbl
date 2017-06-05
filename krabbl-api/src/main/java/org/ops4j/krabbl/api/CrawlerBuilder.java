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
package org.ops4j.krabbl.api;

import java.util.ServiceLoader;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Harald Wellmann
 *
 */
public abstract class CrawlerBuilder {

    public static final int DEFAULT_PRIORITY = 100;

    public abstract void setExecutor(ScheduledExecutorService executor);

    public abstract void setHttpClientConfiguration(HttpClientConfiguration httpClientConfiguration);

    public abstract void setRobotsConfiguration(RobotsConfiguration robotsConfiguration);

    public abstract Crawler newCrawler(CrawlerConfiguration config, PageVisitor pageVisitor);

    public abstract int getPriority();

    public static CrawlerBuilder builder() {
        int maxPriority = 0;
        CrawlerBuilder preferred = null;
        for (CrawlerBuilder builder : ServiceLoader.load(CrawlerBuilder.class)) {
            if (builder.getPriority() > maxPriority) {
                maxPriority = builder.getPriority();
                preferred = builder;
            }
        }
        if (preferred == null) {
            throw new IllegalStateException("CrawlerBuilder service lookup failed");
        }
        return preferred;
    }
}
