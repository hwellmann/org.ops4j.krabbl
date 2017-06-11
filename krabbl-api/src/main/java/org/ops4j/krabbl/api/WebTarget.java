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

import java.io.Serializable;

/**
 * @author Yasser Ganjisaffar
 */

public class WebTarget implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;

    private String referringUrl;
    private int depth;
    private String domain;
    private String subdomain;
    private String path;

    /**
     * Gets the URL of the first encountered page referring to this target.
     *
     * @return referring URL, or null if this target is a seed
     */
    public String getReferringUrl() {
        return referringUrl;
    }

    /**
     * Sets the referring URL.
     *
     * @param referringUrl
     */
    public void setReferringUrl(String referringUrl) {
        this.referringUrl = referringUrl;
    }

    /**
     * Gets the depth at which this target is first encountered. Seeds have depth 0, URLs extracted
     * from a seed have depth 1 etc.
     *
     * @return depth of this target
     *
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Sets the depth of this target.
     *
     * @param depth
     *            depth
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Gets the registered domain part of the host name of this URL. This part is computed with
     * using the <a href="https://publicsuffix.org/">Public Suffix List</a>. The domain is composed
     * of the public suffix and the name part preceding the suffix.
     *
     * @return domain of this URL, e.g. for {@code http://de.wikipedia.org}, the domain is
     *         {@code wikipedia.org}.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Gets the subdomain part of the host name of this URL. This is the leading part of the host
     * name of this target up to the domain part and not including the dot separator. The result can
     * be empty, but never null.
     *
     * @return subdomain of this URL, e.g. for {@code http://de.wikipedia.org}, the domain is
     *         {@code de}.
     */
    public String getSubdomain() {
        return subdomain;
    }

    /**
     * Gets the normalized URL of this target. The
     * <a href="https://en.wikipedia.org/wiki/URL_normalization">normalized form</a> is used for
     * deciding whether two given targets are equal.
     *
     * @return normalized URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the normalized URL of this target.
     *
     * @param url
     *            normalized URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Sets the domain name of this URL.
     *
     * @param domain
     *            domain name
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Sets the subdomain name of this URL.
     *
     * @param subdomain
     */
    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    /**
     * Gets the path of the normalized URL of this target.
     *
     * @return path of this target. E.g. for {@code https://en.wikipedia.org/wiki/Crawler}, the path
     *         will be {@code wiki/Crawler}.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path of this target.
     *
     * @param path
     *            path
     */
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        WebTarget otherUrl = (WebTarget) o;
        return (url != null) && url.equals(otherUrl.getUrl());

    }

    @Override
    public String toString() {
        return url;
    }
}
