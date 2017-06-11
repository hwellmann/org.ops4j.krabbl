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
package org.ops4j.krabbl.api;

import org.apache.http.Header;

/**
 * This class contains the data for a fetched and parsed page.
 *
 * @author Yasser Ganjisaffar
 */
public class Page {

    /**
     * The URL of this page.
     */
    private WebTarget webTarget;

    /**
     * Redirection flag
     */
    private boolean redirect;

    /**
     * The URL to which this page will be redirected to
     */
    private WebTarget redirectedToUrl;

    /**
     * Status of the page
     */
    private int statusCode;

    /**
     * The content of this page in binary format.
     */
    private byte[] contentData;

    /**
     * The ContentType of this page.
     * For example: "text/html; charset=UTF-8"
     */
    private String contentType;

    /**
     * The encoding of the content.
     * For example: "gzip"
     */
    private String contentEncoding;

    /**
     * The charset of the content.
     * For example: "UTF-8"
     */
    private String contentCharset;

    /**
     * Language of the Content.
     */
    private String language;

    /**
     * Headers which were present in the response of the fetch request
     */
    private Header[] fetchResponseHeaders;

    /**
     * Whether the content was truncated because the received data exceeded the imposed maximum
     */
    private boolean truncated = false;

    private org.ops4j.krabbl.api.ParseData parseData;

    public Page(WebTarget url) {
        this.webTarget = url;
    }


    public WebTarget getWebTarget() {
        return webTarget;
    }

    public void setWebTarget(WebTarget url) {
        this.webTarget = url;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }

    public WebTarget getRedirectedToUrl() {
        return redirectedToUrl;
    }

    public void setRedirectedToUrl(WebTarget redirectedToUrl) {
        this.redirectedToUrl = redirectedToUrl;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Returns headers which were present in the response of the fetch request
     *
     * @return Header Array, the response headers
     */
    public Header[] getFetchResponseHeaders() {
        return fetchResponseHeaders;
    }

    public void setFetchResponseHeaders(Header[] headers) {
        fetchResponseHeaders = headers;
    }

    /**
     * @return content of this page in binary format.
     */
    public byte[] getContentData() {
        return contentData;
    }

    public void setContentData(byte[] contentData) {
        this.contentData = contentData;
    }

    /**
     * @return ContentType of this page.
     * For example: "text/html; charset=UTF-8"
     */
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return encoding of the content.
     * For example: "gzip"
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    /**
     * @return charset of the content.
     * For example: "UTF-8"
     */
    public String getContentCharset() {
        return contentCharset;
    }

    public void setContentCharset(String contentCharset) {
        this.contentCharset = contentCharset;
    }

    /**
     * @return Language
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTrunctated(boolean truncated) {
        this.truncated = truncated;
    }

    /**
     * @return parsed data generated for this page by parsers
     */
    public ParseData getParseData() {
        return parseData;
    }

    public void setParseData(ParseData parseData) {
        this.parseData = parseData;
    }
}
