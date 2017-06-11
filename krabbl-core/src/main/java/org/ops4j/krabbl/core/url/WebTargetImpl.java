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
package org.ops4j.krabbl.core.url;

import java.util.Objects;

import org.ops4j.krabbl.api.WebTarget;

/**
 * Default implementation of {@link WebTarget}. Not for direct use by client
 * applications.
 *
 * @author Harald Wellmann
 */

public class WebTargetImpl implements WebTarget {

    private static final long serialVersionUID = 1L;

    private String url;

    private String referringUrl;
    private int depth;
    private String domain;
    private String subdomain;
    private String path;


    /**
     * Hidden constructor. Consumers shall use {@code WebTargetBuilder} to create instances
     * of this class.
     */
    WebTargetImpl() {
        // empty
    }

    @Override
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

    @Override
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

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getSubdomain() {
        return subdomain;
    }

    @Override
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

    @Override
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

        WebTargetImpl other = (WebTargetImpl) o;
        return Objects.equals(url, other.url);
    }

    @Override
    public String toString() {
        return url;
    }
}
