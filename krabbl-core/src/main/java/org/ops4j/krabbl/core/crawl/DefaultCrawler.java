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
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

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

    private Frontier frontier;

    private ScheduledExecutorService executor;

    private CompletableFuture<Void> future;

    private PageProcessor pageProcessor;

    private List<WebTarget> seeds;

    private boolean shuttingDown;

    public DefaultCrawler(CrawlerConfiguration config, PageVisitor visitor) {
        this.executor = Executors.newScheduledThreadPool(2);
        this.frontier = new InMemoryFrontier();
        this.seeds = new ArrayList<>();
        this.pageProcessor = new PageProcessor(config, visitor, frontier, this::asyncLoad);
    }

    @Override
    public void waitUntilFinish() {
        future.join();
    }

    @Override
    public void addSeed(String pageUrl) {
        WebTarget target = new WebTargetBuilder(pageUrl).build();
        target.setDepth(0);
        seeds.add(target);
    }

    @Override
    public boolean isFinished() {
        return future.isDone();
    }

    @Override
    public boolean isShuttingDown() {
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
        pageProcessor.schedule(seeds);
        CompletableFuture<Page> futurePage = null;
        while ((futurePage = frontier.consume()) != null) {
            if (shuttingDown) {
                futurePage.complete(null);
                futurePage.join();
            }
            else {
                futurePage.thenApply(pageProcessor::handleOutgoingLinks).thenAccept(this::schedule).join();
                logger.info("processed {} pages of {} total", frontier.getNumberOfProcessedPages(), frontier.getNumberOfAssignedPages());
            }
        }
    }

    public void schedule(List<WebTarget> targets) {
        List<CompletableFuture<Page>> pages = targets.stream().map(this::asyncLoad)
            .collect(Collectors.toList());
        frontier.monitor(targets, pages);
    }



    private CompletableFuture<Page> asyncLoad(WebTarget target) {
        return CompletableFuture.supplyAsync(() -> pageProcessor.processPage(target), executor);
    }
}
