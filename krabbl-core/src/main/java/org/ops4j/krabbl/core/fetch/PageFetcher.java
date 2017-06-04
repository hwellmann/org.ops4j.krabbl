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

package org.ops4j.krabbl.core.fetch;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.ops4j.krabbl.api.CrawlerConfiguration;
import org.ops4j.krabbl.api.WebTarget;
import org.ops4j.krabbl.core.exc.PageBiggerThanMaxSizeException;
import org.ops4j.krabbl.core.url.UrlCanonicalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yasser Ganjisaffar
 */
public class PageFetcher {

    protected static final Logger logger = LoggerFactory.getLogger(PageFetcher.class);
    protected PoolingHttpClientConnectionManager connectionManager;
    protected CloseableHttpClient httpClient;
    protected long lastFetchTime = 0;
    private CrawlerConfiguration config;

    public PageFetcher(CrawlerConfiguration config) {
        this.config = config;
        RequestConfig requestConfig = RequestConfig.custom().setExpectContinueEnabled(false)
            .setRedirectsEnabled(false).setSocketTimeout(config.getSocketTimeout())
            .setConnectTimeout(config.getConnectionTimeout()).build();

        RegistryBuilder<ConnectionSocketFactory> connRegistryBuilder = RegistryBuilder.create();
        connRegistryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
        if (config.isIncludeHttpsPages()) {
            connRegistryBuilder.register("https",
                SSLConnectionSocketFactory.getSystemSocketFactory());
        }

        Registry<ConnectionSocketFactory> connRegistry = connRegistryBuilder.build();
        connectionManager = new PoolingHttpClientConnectionManager(connRegistry);
        connectionManager.setMaxTotal(config.getMaxTotalConnections());
        connectionManager.setDefaultMaxPerRoute(config.getMaxConnectionsPerHost());

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultRequestConfig(requestConfig);
        clientBuilder.setConnectionManager(connectionManager);
        clientBuilder.setUserAgent(config.getUserAgentString());
        clientBuilder.setDefaultHeaders(config.getDefaultHeaders());

        if (config.getProxyHost() != null) {
            if (config.getProxyUsername() != null) {
                BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(
                    new AuthScope(config.getProxyHost(), config.getProxyPort()),
                    new UsernamePasswordCredentials(config.getProxyUsername(),
                        config.getProxyPassword()));
                clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }

            HttpHost proxy = new HttpHost(config.getProxyHost(), config.getProxyPort());
            clientBuilder.setProxy(proxy);
            logger.debug("Working through Proxy: {}", proxy.getHostName());
        }

        httpClient = clientBuilder.build();
    }

    public PageFetchResult fetchPage(WebTarget webUrl)
        throws InterruptedException, IOException, PageBiggerThanMaxSizeException {
        logger.info("fetching {}", webUrl);
        // Getting URL, setting headers & content
        PageFetchResult fetchResult = new PageFetchResult();
        String toFetchURL = webUrl.getUrl();
        HttpUriRequest request = null;
        try {
            request = newHttpUriRequest(toFetchURL);
            applyPolitenessDelay();

            CloseableHttpResponse response = httpClient.execute(request);
            fetchResult.setEntity(response.getEntity());
            fetchResult.setResponseHeaders(response.getAllHeaders());

            // Setting HttpStatus
            int statusCode = response.getStatusLine().getStatusCode();

            // If Redirect ( 3xx )
            if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
                || statusCode == HttpStatus.SC_MOVED_TEMPORARILY
                || statusCode == HttpStatus.SC_MULTIPLE_CHOICES
                || statusCode == HttpStatus.SC_SEE_OTHER
                || statusCode == HttpStatus.SC_TEMPORARY_REDIRECT || statusCode == 308) { // todo
                                                                                          // follow
                // https://issues.apache.org/jira/browse/HTTPCORE-389

                Header header = response.getFirstHeader("Location");
                if (header != null) {
                    String movedToUrl = UrlCanonicalizer.getCanonicalURL(header.getValue(),
                        toFetchURL);
                    fetchResult.setMovedToUrl(movedToUrl);
                }
            }
            else if (statusCode >= 200 && statusCode <= 299) { // is 2XX, everything looks ok
                fetchResult.setFetchedUrl(toFetchURL);
                String uri = request.getURI().toString();
                if (!uri.equals(toFetchURL)) {
                    if (!UrlCanonicalizer.getCanonicalURL(uri).equals(toFetchURL)) {
                        fetchResult.setFetchedUrl(uri);
                    }
                }

                // Checking maximum size
                if (fetchResult.getEntity() != null) {
                    checkSize(fetchResult, response);
                }
            }

            fetchResult.setStatusCode(statusCode);
            return fetchResult;

        }
        finally {
            if ((fetchResult.getEntity() == null) && (request != null)) {
                request.abort();
            }
        }
    }

    private void checkSize(PageFetchResult fetchResult, CloseableHttpResponse response)
        throws IOException, PageBiggerThanMaxSizeException {
        long size = fetchResult.getEntity().getContentLength();
        if (size == -1) {
            Header length = response.getLastHeader("Content-Length");
            if (length == null) {
                length = response.getLastHeader("Content-length");
            }
            if (length != null) {
                size = Integer.parseInt(length.getValue());
            }
        }
        if (size > config.getMaxDownloadSize()) {
            // fix issue #52 - consume entity
            response.close();
            throw new PageBiggerThanMaxSizeException(size);
        }
    }

    private synchronized void applyPolitenessDelay() throws InterruptedException {
        long now = System.currentTimeMillis();
        long delay = config.getPolitenessDelay() - (now - lastFetchTime);
        if (delay > 0) {
            Thread.sleep(delay);
        }
        lastFetchTime = System.currentTimeMillis();
    }

    /**
     * Creates a new HttpUriRequest for the given url. The default is to create a HttpGet without
     * any further configuration. Subclasses may override this method and provide their own logic.
     *
     * @param url
     *            the url to be fetched
     * @return the HttpUriRequest for the given url
     */
    protected HttpUriRequest newHttpUriRequest(String url) {
        return new HttpGet(url);
    }

}
