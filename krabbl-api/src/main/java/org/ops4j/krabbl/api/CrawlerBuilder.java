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
 * Creates crawlers and manages global resources.
 * <p>
 * Applications shall use this builder class to create new crawlers. This builder requires
 * an executor service, an HTTP client configuration and a robots configuration which are
 * defined at a global level to permit resource management for crawlers running within the
 * the same environment, e.g. a servlet container.
 * <p>
 * These dependencies must be set before {@link #newCrawler(CrawlerConfiguration, PageVisitor)}
 * is invoked for the first time and may not be changed afterwards.
 * <p>
 * On shutdown, applications must take care to shutdown all running crawlers and then
 * to {@link #close()} this builder.
 * <p>
 * Krabbl provides a default implementation of this abstract class which is loaded via the Java #link {@link ServiceLoader}.
 * To extend Krabbl, e.g. by providing alternative frontier or parser implementations, users may provide
 * and register alternative {@code CrawlerBuilder} implementations. If more than one service is found, the
 * one with the highest priority will be used.
 *
 * @author Harald Wellmann
 *
 */
public abstract class CrawlerBuilder {

    /**
     * Priority for the built-in default crawler builder service.
     */
    public static final int DEFAULT_PRIORITY = 100;

    /**
     * Sets an executor service to be used by all crawlers for all tasks.
     * @param executor shared executor service
     */
    public abstract void setExecutor(ScheduledExecutorService executor);

    /**
     * Sets the configuration for the shared HTTP client to be used by all crawlers.
     * @param httpClientConfiguration shared HTTP client configuaration
     */
    public abstract void setHttpClientConfiguration(HttpClientConfiguration httpClientConfiguration);

    /**
     * Sets the shared robots configuration to be used by all crawlers.
     * @param robotsConfiguration robots configuration
     */
    public abstract void setRobotsConfiguration(RobotsConfiguration robotsConfiguration);

    /**
     * Gets the priority of this crawler builder. If multiple builder services are found
     * on the classpath, the one with the highest priority will take effect.
     * <p>
     * A between builders with the same priority is broken by sorting the builders by
     * fully qualified class names and selecting the first one.
     *
     * @return service priority
     */
    public abstract int getPriority();

    /**
     * Creates a new crawler with the given configuration and the given page visitor.
     * The visitor will be invoked for each crawled page and for some other events. The shared
     * resources managed by this builder must be defined before this method is invoked for the
     * first time.
     * @param config crawler configuration
     * @param pageVisitor page visitor defined by the application
     * @return crawler with given configuration
     * @throws IllegalStateException when the builder is closed or when shared resources are undefined
     */
    public abstract Crawler newCrawler(CrawlerConfiguration config, PageVisitor pageVisitor);

    /**
     * Closes this builder and releases any shared resources. Invoking this method on a closed
     * builder has no effect.
     */
    public abstract void close();

    /**
     * Entry point for Krabbl. Finds and returns the crawler builder implementation to be used
     * in the current environment.
     * <p>
     * Since crawlers created by the same builder share some resources, the result of this
     * method should be treated as singleton or application scoped, if the runtime environment
     * supports such concepts.
     *
     * @return crawler builder with highest priority
     */
    public static CrawlerBuilder builder() {
        int maxPriority = 0;
        CrawlerBuilder preferred = null;
        for (CrawlerBuilder builder : ServiceLoader.load(CrawlerBuilder.class)) {
            int prio = builder.getPriority();
            if (prio <= 0) {
                throw new IllegalStateException(String.format("Priority of %s must be positive", builder.getClass().getName()));
            }
            if (prio > maxPriority) {
                maxPriority = prio;
                preferred = builder;
            }
            else if (prio == maxPriority) {
                if (builder.getClass().getName().compareTo(preferred.getClass().getName()) < 0) {
                    preferred = builder;
                }
            }
        }
        if (preferred == null) {
            throw new IllegalStateException("CrawlerBuilder service lookup failed");
        }
        return preferred;
    }
}
