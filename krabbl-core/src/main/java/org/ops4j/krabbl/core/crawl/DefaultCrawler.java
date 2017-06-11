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

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import org.ops4j.krabbl.api.Crawler;
import org.ops4j.krabbl.api.CrawlerConfiguration;
import org.ops4j.krabbl.api.Page;
import org.ops4j.krabbl.api.PageVisitor;
import org.ops4j.krabbl.api.WebTarget;
import org.ops4j.krabbl.core.spi.Frontier;
import org.ops4j.krabbl.core.url.WebTargetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 *
 */
public class DefaultCrawler implements Crawler {

    private static Logger logger = LoggerFactory.getLogger(DefaultCrawler.class);

    private CrawlerConfiguration config;

    private ScheduledExecutorService executor;

    private Frontier frontier;

    private PageProcessor pageProcessor;

    private CompletableFuture<Void> future;

    private List<WebTarget> seeds;

    private boolean shuttingDown;

    private BlockingQueue<CompletableFuture<Page>> queue = new LinkedBlockingQueue<>();

    private PageVisitor visitor;

    public DefaultCrawler(CrawlerConfiguration config,
        ScheduledExecutorService executor, Frontier frontier, PageProcessor pageProcessor, PageVisitor visitor) {
        this.config = config;
        this.executor = executor;
        this.frontier = frontier;
        this.pageProcessor = pageProcessor;
        this.visitor = visitor;
        this.seeds = new ArrayList<>();
    }

    @Override
    public void awaitTermination() {
        if (future != null) {
            future.join();
        }
    }

    @Override
    public void addSeed(String pageUrl) {
        if (future != null) {
            throw new IllegalStateException("Cannot add seeds after crawling has started");
        }
        WebTarget target = new WebTargetBuilder(pageUrl).build();
        target.setDepth(0);
        seeds.add(target);
    }

    @Override
    public boolean isTerminated() {
        return future != null && future.isDone();
    }

    @Override
    public boolean isShutdown() {
        return shuttingDown;
    }

    @Override
    public void shutdown() {
        this.shuttingDown = true;
    }

    @Override
    public void start() {
        future = CompletableFuture.runAsync(this::execute, executor);
    }

    private void execute() {
        visitor.onStart();
        schedule(seeds);
        CompletableFuture<Page> futurePage = null;
        while ((futurePage = queue.poll()) != null) {
            completeOnePage(futurePage);
        }
        visitor.onBeforeExit();
        assert frontier.isFinished() || shuttingDown;
    }

    private void completeOnePage(CompletableFuture<Page> futurePage) {
        if (shuttingDown) {
            cancelOrWaitForPage(futurePage);
        }
        else {
            processAndWaitForPage(futurePage);
        }
    }

    private void cancelOrWaitForPage(CompletableFuture<Page> futurePage) {
        futurePage.complete(null);
        futurePage.join();
    }

    private void processAndWaitForPage(CompletableFuture<Page> futurePage) {
        futurePage.thenApply(pageProcessor::handleOutgoingLinks).thenAccept(this::schedule).join();
        logger.info("processed {} pages of {} total", frontier.getNumberOfProcessedPages(),
            frontier.getNumberOfScheduledPages());
    }

    public void schedule(List<WebTarget> targets) {
        List<WebTarget> newTargets = truncateToMax(targets);
        if (!newTargets.isEmpty()) {
            List<CompletableFuture<Page>> pages = newTargets.stream().map(this::asyncLoad)
                .collect(toList());
            frontier.schedule(newTargets);
            queue.addAll(pages);
        }
    }

    private List<WebTarget> truncateToMax(List<WebTarget> targets) {
        List<WebTarget> newTargets = targets;
        int max = config.getMaxPagesToFetch();
        if (max >= 0) {
            max = config.getMaxPagesToFetch() - (int) frontier.getNumberOfScheduledPages();
            if (newTargets.size() > max) {
                newTargets = newTargets.subList(0, max);
            }
        }
        return newTargets;
    }

    private CompletableFuture<Page> asyncLoad(WebTarget target) {
        return CompletableFuture.supplyAsync(() -> pageProcessor.processPage(target), executor);
    }
}
