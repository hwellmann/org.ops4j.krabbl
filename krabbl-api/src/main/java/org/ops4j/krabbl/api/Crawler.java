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

/**
 * A crawler takes a number of seed URLs to be fetched and parsed. Any outgoing
 * URLs found in the parsed pages are candidates to be fetched, depending on
 * the crawler configuration and the results of page visitor callbacks.
 * <p>
 * Crawlers created by the same builder share some resources, including an executor
 * used for all background tasks like fetching pages.
 *
 * @author Harald Wellmann
 */
public interface Crawler {

    /**
     * Waits until this crawler is terminated.
     */
    void awaitTermination();

    /**
     * Adds a seed URL. Seed URLs are starting points for the crawling process.
     * @param url seed URL
     */
    void addSeed(String url);

    /**
     * Starts crawling from the given seed URLs.
     */
    void start();

    /**
     * Checks if this crawler is terminated.
     * @return true if terminated
     */
    boolean isTerminated();

    /**
     * Checks if this crawler has been shut down. After shutdown, the crawler
     * may take some time to actually terminate.
     * @return
     */
    boolean isShutdown();

    /**
     * Gracefully shuts down this crawler. No new pages will be fetched, but any running tasks
     * including visitor callbacks will be finished.
     */
    void shutdown();
}
