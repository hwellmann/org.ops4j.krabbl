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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpStatus;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.ops4j.krabbl.api.CrawlerConfiguration;
import org.ops4j.krabbl.api.Page;
import org.ops4j.krabbl.api.PageVisitor;
import org.ops4j.krabbl.api.ParseData;
import org.ops4j.krabbl.api.WebTarget;
import org.ops4j.krabbl.core.exc.ContentFetchException;
import org.ops4j.krabbl.core.exc.PageBiggerThanMaxSizeException;
import org.ops4j.krabbl.core.fetch.PageFetchResult;
import org.ops4j.krabbl.core.fetch.PageFetcher;
import org.ops4j.krabbl.core.parse.HtmlParseData;
import org.ops4j.krabbl.core.parse.JsoupHtmlParser;
import org.ops4j.krabbl.core.spi.Frontier;
import org.ops4j.krabbl.core.spi.Parser;
import org.ops4j.krabbl.core.url.WebTargetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 *
 */
public class PageProcessor {

    private static Logger logger = LoggerFactory.getLogger(PageProcessor.class);

    private PageVisitor visitor;

    private Frontier frontier;

    private CrawlerConfiguration config;

    private PageFetcher pageFetcher;

    private Parser parser;

    public PageProcessor(CrawlerConfiguration config, PageVisitor visitor, Frontier frontier) {
        this.config = config;
        this.visitor = visitor;
        this.frontier = frontier;
        this.parser = new JsoupHtmlParser();
        this.pageFetcher = new PageFetcher(config);
    }

    public List<WebTarget> handleOutgoingLinks(Page page) {
        if (page == null) {
            return Collections.emptyList();
        }
        WebTarget redirectedTo = handleRedirects(page);
        if (redirectedTo != null) {
            return Collections.singletonList(redirectedTo);
        }

        if (page.getParseData() == null) {
            return Collections.emptyList();
        }
        return processParsedLinks(page);
    }

    public Page processPage(WebTarget target) {
        if (target == null) {
            return null;
        }
        frontier.setProcessing(target);
        Page page = new Page(target);

        PageFetchResult fetchResult = null;
        try {
            fetchResult = pageFetcher.fetchPage(target);
            int statusCode = fetchResult.getStatusCode();
            visitor.handlePageStatusCode(target, statusCode,
                EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, Locale.ENGLISH));

            page.setFetchResponseHeaders(fetchResult.getResponseHeaders());
            page.setStatusCode(statusCode);
            if (statusCode < 200 || statusCode > 299) {
                if (isRedirect(statusCode)) {
                    handleRedirect(page, fetchResult, target);
                }
                else {
                    handleUnexpectedStatus(fetchResult, target);
                }
            }
            else { // if status code is 200
                handleSuccess(page, fetchResult, target);
            }
        }
        catch (PageBiggerThanMaxSizeException e) {
            visitor.onPageBiggerThanMaxSize(target.getUrl(), e.getPageSize());
        }
        catch (ContentFetchException cfe) {
            visitor.onContentFetchError(target);
        }
        catch (Exception e) {
            visitor.onUnhandledException(target, e);
        }
        finally {
            if (fetchResult != null) {
                fetchResult.discardContentIfNotConsumed();
            }
        }
        frontier.setProcessed(target);

        return page;
    }

    private void handleUnexpectedStatus(PageFetchResult fetchResult, WebTarget curUrl) {
        String description = EnglishReasonPhraseCatalog.INSTANCE
            .getReason(fetchResult.getStatusCode(), Locale.ENGLISH); // Finds
        // the status reason for all known statuses
        String contentType = fetchResult.getEntity() == null ? ""
            : fetchResult.getEntity().getContentType() == null ? ""
                : fetchResult.getEntity().getContentType().getValue();
        visitor.onUnexpectedStatusCode(curUrl.getUrl(), fetchResult.getStatusCode(), contentType,
            description);
    }

    private void handleRedirect(Page page, PageFetchResult fetchResult, WebTarget curUrl) {
        page.setRedirect(true);

        String movedToUrl = fetchResult.getMovedToUrl();
        if (movedToUrl == null) {
            logger.warn("Unexpected error, Url: {} is redirected to NOTHING", curUrl);
            return;
        }
        WebTarget webUrl = new WebTargetBuilder(movedToUrl).build();
        webUrl.setParentDocid(curUrl.getParentDocid());
        webUrl.setParentUrl(curUrl.getParentUrl());
        webUrl.setDepth(curUrl.getDepth());
        webUrl.setDocid(-1);
        webUrl.setAnchor(curUrl.getAnchor());

        page.setRedirectedToUrl(webUrl);
        visitor.onRedirectedStatusCode(page);
    }

    private void handleSuccess(Page page, PageFetchResult fetchResult, WebTarget curUrl)
        throws ContentFetchException {
        if (!curUrl.getUrl().equals(fetchResult.getFetchedUrl())) {
            if (frontier.isSeenBefore(fetchResult.getFetchedUrl())) {
                logger.debug("Redirect page: {} has already been seen", curUrl);
                return;
            }
            curUrl.setUrl(fetchResult.getFetchedUrl());
            // curUrl.setDocid(docIdServer.getNewDocID(fetchResult.getFetchedUrl()));
        }

        if (!fetchResult.fetchContent(page, config.getMaxDownloadSize())) {
            throw new ContentFetchException();
        }

        if (page.isTruncated()) {
            logger.warn(
                "Warning: unknown page size exceeded max-download-size, truncated to: ({}), at Url: {}",
                config.getMaxDownloadSize(), curUrl.getUrl());
        }

        parser.parse(page, curUrl.getUrl());

        if (!noIndex(page)) {
            visitor.visit(page);
        }
    }

    private WebTarget handleRedirects(Page page) {
        if (page.getRedirectedToUrl() != null && config.isFollowRedirects()) {
            if (visitor.shouldVisit(page, page.getRedirectedToUrl())) {
                return page.getRedirectedToUrl();
            }
            else {
                logger.debug("Not visiting: {} as per your \"shouldVisit\" policy",
                    page.getRedirectedToUrl().getUrl());
            }
        }
        return null;
    }

    private List<WebTarget> processParsedLinks(Page page) {
        WebTarget curUrl = page.getWebTarget();
        if (!visitor.shouldFollowLinksIn(page.getWebTarget())) {
            logger.debug("Not looking for links in page {}, "
                + "as per your \"shouldFollowLinksInPage\" policy", page.getWebTarget().getUrl());
            return Collections.emptyList();
        }

        ParseData parseData = page.getParseData();
        List<WebTarget> toSchedule = new ArrayList<>();
        int maxCrawlDepth = config.getMaxDepthOfCrawling();
        for (WebTarget webUrl : parseData.getOutgoingUrls()) {
            webUrl.setParentDocid(curUrl.getDocid());
            webUrl.setParentUrl(curUrl.getUrl());
            // int newdocid = docIdServer.getDocId(webUrl.getUrl());
            if (frontier.isSeenBefore(webUrl.getUrl())) {
                // This is not the first time that this Url is visited. So, we set the
                // depth to a negative number.
                webUrl.setDepth((short) -1);
                // webUrl.setDocid(newdocid);
            }
            else {
                webUrl.setDocid(-1);
                webUrl.setDepth(curUrl.getDepth() + 1);
                if ((maxCrawlDepth == -1) || (curUrl.getDepth() < maxCrawlDepth)) {
                    if (visitor.shouldVisit(page, webUrl)) {
                        // webUrl.setDocid(docIdServer.getNewDocID(webUrl.getUrl()));
                        toSchedule.add(webUrl);
                    }
                    else {
                        logger.debug("Not visiting: {} as per your \"shouldVisit\" policy",
                            webUrl.getUrl());
                    }
                }
            }
        }
        return toSchedule;
    }

    private boolean isRedirect(int statusCode) {
        return statusCode == HttpStatus.SC_MOVED_PERMANENTLY
            || statusCode == HttpStatus.SC_MOVED_TEMPORARILY
            || statusCode == HttpStatus.SC_MULTIPLE_CHOICES || statusCode == HttpStatus.SC_SEE_OTHER
            || statusCode == HttpStatus.SC_TEMPORARY_REDIRECT || statusCode == 308;
    }

    /**
     * @param page
     * @return
     */
    private boolean noIndex(Page page) {
        return config.isRespectNoIndex() && page.getContentType() != null
            && page.getContentType().contains("html")
            && ((HtmlParseData) page.getParseData()).getMetaTagValue("robots").contains("noindex");
    }
}
