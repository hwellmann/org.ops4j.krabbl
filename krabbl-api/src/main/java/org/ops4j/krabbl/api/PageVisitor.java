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
 * A page visitor receives information about pages visited by a crawler. Clients creating a crawler
 * need to provide a page visitor to get any feedback from the crawling session.
 * <p>
 * Implementations of this interface are required to be thread-safe.
 *
 * @author Harald Wellmann
 *
 */
public interface PageVisitor {

    /**
     * Called after the crawler has started, just before visiting the first seed.
     */
    default void onStart() {
        // empty
    }

    /**
     * Called just before the crawler terminates. No more pages will be visited after this point.
     */
    default void onBeforeExit() {
        // empty
    }

    /**
     * Called just before visiting a given web target. The implementation may return false to skip
     * loading and visiting the given target. The default implementation always returns true.
     *
     * @param referringPage
     *            Referring page on which the given target was found. This is null for seed targets.
     * @param target
     *            target to be visited
     * @return true if the target should be visited.
     */
    default boolean shouldVisit(Page referringPage, WebTarget target) {
        return true;
    }

    /**
     * Called when visiting a given web target. The target has been loaded and parsed.
     *
     * @param page
     */
    default void visit(Page page) {
        // empty
    }

    /**
     * Called when the content length of the given URL exceeds the allowed maximum.
     *
     * @param url
     *            url with partial content
     */
    default void onPageBiggerThanMaxSize(String url, long pageSize) {
        // empty
    }

    /**
     * Called after loading a page with a 3xx HTTP status code.
     *
     * @param page partial page object with detail information
     */
    default void onRedirectedStatusCode(Page page) {
        // empty
    }

    /**
     * Called after loading a page with an unexpected HTTP status code of 400 or higher.
     *
     * @param url
     *            URL that responded with error code
     * @param statusCode
     *            HTTP status code
     * @param contentType
     *            content type from response header
     */
    default void onUnexpectedStatusCode(String url, int statusCode, String contentType) {
        // empty
    }

    /**
     * Called when the content of the given target could not be fetched.
     *
     * @param target
     *            affected target
     */
    default void onContentFetchError(WebTarget target) {
        // empty
    }

    /**
     * This function is called when a unhandled exception was encountered during fetching
     *
     * @param webUrl
     *            URL where a unhandled exception occured
     */
    default void onUnhandledException(WebTarget webUrl, Throwable e) {
        // empty
    }

    /**
     * This function is called if there has been an error in parsing the content.
     *
     * @param webUrl
     *            URL which failed on parsing
     */
    default void onParseError(WebTarget webUrl) {
        // empty
    }

    /**
     * This function is called once the header of a page is fetched. It can be overridden by
     * sub-classes to perform custom logic for different status codes. For example, 404 pages can be
     * logged, etc.
     *
     * @param webUrl
     *            WebUrl containing the statusCode
     * @param statusCode
     *            Html Status Code number
     * @param statusDescription
     *            Html Status COde description
     */
    default void handlePageStatusCode(WebTarget webUrl, int statusCode, String statusDescription) {
        // empty
    }

    /**
     * Determine whether links found at the given URL should be added to the queue for crawling. By
     * default this method returns true always, but classes that extend WebCrawler can override it
     * in order to implement particular policies about which pages should be mined for outgoing
     * links and which should not.
     *
     * If links from the URL are not being followed, then we are not operating as a web crawler and
     * need not check robots.txt before fetching the single URL. (see definition at
     * http://www.robotstxt.org/faq/what.html). Thus URLs that return false from this method will
     * not be subject to robots.txt filtering.
     *
     * @param url
     *            the URL of the page under consideration
     * @return true if outgoing links from this page should be added to the queue.
     */
    default boolean shouldFollowLinksIn(WebTarget url) {
        return true;
    }
}
