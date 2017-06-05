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
import java.util.Map;

/**
 * @author Yasser Ganjisaffar
 */

public class WebTarget implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;

    private String parentUrl;
    private int depth;
    private String domain;
    private String subDomain;
    private String path;
    private String anchor;
    private int priority;
    private String tag;
    private Map<String, String> attributes;

    /**
     * @return
     *      url of the parent page. The parent page is the page in which
     *      the Url of this page is first observed.
     */
    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    /**
     * @return
     *      crawl depth at which this Url is first observed. Seed Urls
     *      are at depth 0. Urls that are extracted from seed Urls are at depth 1, etc.
     */
    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * @return
     *      domain of this Url. For 'http://www.example.com/sample.htm', domain will be 'example
     *      .com'
     */
    public String getDomain() {
        return domain;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public void setDomain(String domain) {
        this.domain = domain;
    }


    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    /**
     * @return
     *      path of this Url. For 'http://www.example.com/sample.htm', domain will be 'sample.htm'
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return
     *      anchor string. For example, in <a href="example.com">A sample anchor</a>
     *      the anchor string is 'A sample anchor'
     */
    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    /**
     * @return priority for crawling this URL. A lower number results in higher priority.
     */
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return tag in which this URL is found
     * */
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getAttribute(String name) {
        if (attributes == null) {
            return "";
        }
        return attributes.getOrDefault(name, "");
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