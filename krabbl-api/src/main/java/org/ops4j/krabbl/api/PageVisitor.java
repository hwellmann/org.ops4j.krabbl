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
 * @author Harald Wellmann
 *
 */
public interface PageVisitor {

    default void onStart() {

    }

    default void onBeforeExit() {

    }

    default boolean shouldVisit(Page referringPage, WebTarget url) {
        return true;
    }

    default void visit(Page page) {

    }

    /**
     * This function is called if the content of a url is bigger than allowed size.
     *
     * @param urlStr
     *            - The URL which it's content is bigger than allowed size
     */
    default void onPageBiggerThanMaxSize(String urlStr, long pageSize) {
    }

    /**
     * This function is called if the crawler encounters a page with a 3xx status code
     *
     * @param page
     *            Partial page object
     */
    default void onRedirectedStatusCode(Page page) {
        // Subclasses can override this to add their custom functionality
    }

    /**
     * This function is called if the crawler encountered an unexpected http status code ( a status
     * code other than 3xx)
     *
     * @param urlStr
     *            URL in which an unexpected error was encountered while crawling
     * @param statusCode
     *            Html StatusCode
     * @param contentType
     *            Type of Content
     * @param description
     *            Error Description
     */
    default void onUnexpectedStatusCode(String urlStr, int statusCode, String contentType,
        String description) {
    }

    /**
     * This function is called if the content of a url could not be fetched.
     *
     * @param webUrl
     *            URL which content failed to be fetched
     */
    default void onContentFetchError(WebTarget webUrl) {
    }

    /**
     * This function is called when a unhandled exception was encountered during fetching
     *
     * @param webUrl
     *            URL where a unhandled exception occured
     */
    default void onUnhandledException(WebTarget webUrl, Throwable e) {
    }

    /**
     * This function is called if there has been an error in parsing the content.
     *
     * @param webUrl
     *            URL which failed on parsing
     */
    default void onParseError(WebTarget webUrl) {
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
        // Do nothing by default
        // Sub-classed can override this to add their custom functionality
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
