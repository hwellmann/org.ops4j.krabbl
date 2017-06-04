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

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.ops4j.krabbl.api.Page;
import org.ops4j.krabbl.api.WebTarget;
import org.ops4j.krabbl.core.spi.Frontier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 *
 */
public class InMemoryFrontier implements Frontier {

    private static Logger logger = LoggerFactory.getLogger(InMemoryFrontier.class);

    private Map<WebTarget, PageStatus> pageMap = new ConcurrentHashMap<>();

    private Map<WebTarget, CompletableFuture<Page>> processingMap = new ConcurrentHashMap<>();

    private BlockingQueue<CompletableFuture<Page>> queue = new LinkedBlockingQueue<>();

    private AtomicLong numProcessed = new AtomicLong();

    @Override
    public void setProcessed(WebTarget url) {
        pageMap.put(url, PageStatus.DONE);
        processingMap.remove(url);
        numProcessed.incrementAndGet();
    }

    @Override
    public long getNumberOfAssignedPages() {
        return processingMap.size();
    }

    @Override
    public long getNumberOfProcessedPages() {
        return numProcessed.get();
    }

    @Override
    public long getNumberOfScheduledPages() {
        return queue.size();
    }

    @Override
    public boolean isFinished() {
        return queue.isEmpty() && processingMap.isEmpty();
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub

    }

    @Override
    public CompletableFuture<Page> consume() {
        logger.info("processing {} pages of {} total", processingMap.size(), pageMap.size());
        return queue.poll();
    }

    @Override
    public boolean isSeenBefore(String url) {
        WebTarget target = new WebTarget();
        target.setUrl(url);
        return pageMap.containsKey(target);
    }

    @Override
    public void monitor(WebTarget url, CompletableFuture<Page> page) {
        processingMap.put(url, page);
        pageMap.put(url, PageStatus.PROCESSING);
        queue.offer(page);
    }

    @Override
    public void monitor(List<WebTarget> urls, List<CompletableFuture<Page>> pages) {
        if (urls.size() != pages.size()) {
            throw new IllegalArgumentException("url and page lists must have same size");
        }

        for (int i = 0; i < urls.size(); i++) {
            monitor(urls.get(i), pages.get(i));
        }
    }

}
