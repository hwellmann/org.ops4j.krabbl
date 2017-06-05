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

package org.ops4j.krabbl.api;

import java.util.Collection;
import java.util.HashSet;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class HttpClientConfiguration {

    /**
     * user-agent string that is used for representing your crawler to web
     * servers. See http://en.wikipedia.org/wiki/User_agent for more details
     */
    private String userAgentString = "krabbl (https://github.com/hwellmann/org.ops4j.krabbl/)";

    /**
     * Default request header values.
     */
    private Collection<BasicHeader> defaultHeaders = new HashSet<BasicHeader>();

    /**
     * Politeness delay in milliseconds (delay between sending two requests to
     * the same host).
     */
    private int politenessDelay = 200;

    /**
     * Maximum Connections per host
     */
    private int maxConnectionsPerHost = 100;

    /**
     * Maximum total connections
     */
    private int maxTotalConnections = 100;

    /**
     * Socket timeout in milliseconds
     */
    private int socketTimeout = 20000;

    /**
     * Connection timeout in milliseconds
     */
    private int connectionTimeout = 30000;

    /**
     * Max allowed size of a page. Pages larger than this size will not be
     * fetched.
     */
    private int maxDownloadSize = 1048576;

    /**
     * If crawler should run behind a proxy, this parameter can be used for
     * specifying the proxy host.
     */
    private String proxyHost = null;

    /**
     * If crawler should run behind a proxy, this parameter can be used for
     * specifying the proxy port.
     */
    private int proxyPort = 80;

    /**
     * If crawler should run behind a proxy and user/pass is needed for
     * authentication in proxy, this parameter can be used for specifying the
     * username.
     */
    private String proxyUsername = null;

    /**
     * If crawler should run behind a proxy and user/pass is needed for
     * authentication in proxy, this parameter can be used for specifying the
     * password.
     */
    private String proxyPassword = null;

    /**
     * Validates the configs specified by this instance.
     *
     * @throws Exception on Validation fail
     */
    public void validate() throws Exception {
        if (politenessDelay < 0) {
            throw new Exception("Invalid value for politeness delay: " + politenessDelay);
        }
    }

    /**
     *
     * @return userAgentString
     */
    public String getUserAgentString() {
        return userAgentString;
    }

    /**
     * user-agent string that is used for representing your crawler to web
     * servers. See http://en.wikipedia.org/wiki/User_agent for more details
     *
     * @param userAgentString Custom userAgent string to use as your crawler's identifier
     */
    public void setUserAgentString(String userAgentString) {
        this.userAgentString = userAgentString;
    }

    /**
     * Return a copy of the default header collection.
     */
    public Collection<BasicHeader> getDefaultHeaders() {
        return new HashSet<>(defaultHeaders);
    }

    /**
     * Set the default header collection (creating copies of the provided headers).
     */
    public void setDefaultHeaders(Collection<? extends Header> defaultHeaders) {
        Collection<BasicHeader> copiedHeaders = new HashSet<>();
        for (Header header : defaultHeaders) {
            copiedHeaders.add(new BasicHeader(header.getName(), header.getValue()));
        }
        this.defaultHeaders = copiedHeaders;
    }

    public int getPolitenessDelay() {
        return politenessDelay;
    }

    /**
     * Politeness delay in milliseconds (delay between sending two requests to
     * the same host).
     *
     * @param politenessDelay
     *            the delay in milliseconds.
     */
    public void setPolitenessDelay(int politenessDelay) {
        this.politenessDelay = politenessDelay;
    }

    public int getMaxConnectionsPerHost() {
        return maxConnectionsPerHost;
    }

    /**
     * @param maxConnectionsPerHost Maximum Connections per host
     */
    public void setMaxConnectionsPerHost(int maxConnectionsPerHost) {
        this.maxConnectionsPerHost = maxConnectionsPerHost;
    }

    public int getMaxTotalConnections() {
        return maxTotalConnections;
    }

    /**
     * @param maxTotalConnections Maximum total connections
     */
    public void setMaxTotalConnections(int maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * @param socketTimeout Socket timeout in milliseconds
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @param connectionTimeout Connection timeout in milliseconds
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getMaxDownloadSize() {
        return maxDownloadSize;
    }

    /**
     * @param maxDownloadSize Max allowed size of a page. Pages larger than this size will not be
     * fetched.
     */
    public void setMaxDownloadSize(int maxDownloadSize) {
        this.maxDownloadSize = maxDownloadSize;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * @param proxyHost If crawler should run behind a proxy, this parameter can be used for
     * specifying the proxy host.
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * @param proxyPort If crawler should run behind a proxy, this parameter can be used for
     * specifying the proxy port.
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    /**
     * @param proxyUsername
     *        If crawler should run behind a proxy and user/pass is needed for
     *        authentication in proxy, this parameter can be used for specifying the username.
     */
    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * If crawler should run behind a proxy and user/pass is needed for
     * authentication in proxy, this parameter can be used for specifying the password.
     *
     * @param proxyPassword String Password
     */
    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User agent string: " + getUserAgentString() + "\n");
        sb.append("Max connections per host: " + getMaxConnectionsPerHost() + "\n");
        sb.append("Max total connections: " + getMaxTotalConnections() + "\n");
        sb.append("Socket timeout: " + getSocketTimeout() + "\n");
        sb.append("Max total connections: " + getMaxTotalConnections() + "\n");
        sb.append("Max download size: " + getMaxDownloadSize() + "\n");
        sb.append("Proxy host: " + getProxyHost() + "\n");
        sb.append("Proxy port: " + getProxyPort() + "\n");
        sb.append("Proxy username: " + getProxyUsername() + "\n");
        sb.append("Proxy password: " + getProxyPassword() + "\n");
        return sb.toString();
    }
}
