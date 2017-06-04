/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.krabbl.core.spi;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.ops4j.krabbl.api.Page;
import org.ops4j.krabbl.api.WebTarget;

/**
 * @author Yasser Ganjisaffar
 */

public interface Frontier {

    void monitor(WebTarget url, CompletableFuture<Page> page);
    void monitor(List<WebTarget> url, List<CompletableFuture<Page>> page);

    void setProcessed(WebTarget webURL);

    long getNumberOfAssignedPages();
    long getNumberOfProcessedPages();
    long getNumberOfScheduledPages();
    boolean isFinished();
    boolean isSeenBefore(String url);
    CompletableFuture<Page> consume();

    default void close() {

    }

    default void finish() {

    }
}
