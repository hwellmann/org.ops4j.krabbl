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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.ops4j.krabbl.api.WebTarget;
import org.ops4j.krabbl.core.spi.Frontier;
import org.ops4j.krabbl.core.url.WebTargetBuilder;

/**
 * @author Harald Wellmann
 *
 */
public class InMemoryFrontier implements Frontier {

    private Map<WebTarget, PageStatus> pageMap = new ConcurrentHashMap<>();

    private AtomicLong numProcessed = new AtomicLong();
    private AtomicLong numProcessing = new AtomicLong();

    @Override
    public void setProcessed(WebTarget url) {
        pageMap.put(url, PageStatus.PROCESSED);
        numProcessing.decrementAndGet();
        numProcessed.incrementAndGet();

    }

    @Override
    public long getNumberOfProcessingPages() {
        return numProcessing.get();
    }

    @Override
    public long getNumberOfProcessedPages() {
        return numProcessed.get();
    }

    @Override
    public long getNumberOfScheduledPages() {
        return pageMap.size();
    }

    @Override
    public boolean isFinished() {
        return (numProcessing.get() == 0) && (numProcessed.get() == pageMap.size());
    }

    @Override
    public boolean isSeenBefore(String url) {
        WebTarget target = new WebTargetBuilder(url).build();
        return pageMap.containsKey(target);
    }

    @Override
    public void schedule(WebTarget url) {
        pageMap.put(url, PageStatus.SCHEDULED);
    }

    @Override
    public void schedule(List<WebTarget> urls) {
        urls.forEach(this::schedule);
    }

    @Override
    public void setProcessing(WebTarget target) {
        pageMap.put(target, PageStatus.PROCESSING);
        numProcessing.incrementAndGet();
    }
}
