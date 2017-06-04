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
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.ops4j.krabbl.api.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yasser Ganjisaffar
 */
public class PageFetchResult {

    protected static final Logger logger = LoggerFactory.getLogger(PageFetchResult.class);

    protected int statusCode;
    protected HttpEntity entity = null;
    protected Header[] responseHeaders = null;
    protected String fetchedUrl = null;
    protected String movedToUrl = null;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public HttpEntity getEntity() {
        return entity;
    }

    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Header[] responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getFetchedUrl() {
        return fetchedUrl;
    }

    public void setFetchedUrl(String fetchedUrl) {
        this.fetchedUrl = fetchedUrl;
    }

    public boolean fetchContent(Page page, int maxBytes) {
        try {
            load(page, entity, maxBytes);
            page.setFetchResponseHeaders(responseHeaders);
            return true;
        } catch (Exception e) {
            logger.info("Exception while fetching content for: {} [{}]", page.getWebTarget().getUrl(),
                        e.getMessage());
        }
        return false;
    }

    /**
     * Loads the content of this page from a fetched HttpEntity.
     *
     * @param entity HttpEntity
     * @param maxBytes The maximum number of bytes to read
     * @throws Exception when load fails
     */
    public void load(Page page, HttpEntity entity, int maxBytes) throws Exception {

        Header type = entity.getContentType();
        if (type != null) {
            page.setContentType(type.getValue());
        }

        Header encoding = entity.getContentEncoding();
        if (encoding != null) {
            page.setContentEncoding(encoding.getValue());
        }

        Charset charset = ContentType.getOrDefault(entity).getCharset();
        if (charset != null) {
            page.setContentCharset(charset.displayName());
        }

        toByteArray(page, entity, maxBytes);
    }

    /**
     * Read contents from an entity, with a specified maximum. This is a replacement of
     * EntityUtils.toByteArray because that function does not impose a maximum size.
     *
     * @param entity The entity from which to read
     * @param maxBytes The maximum number of bytes to read
     * @return A byte array containing maxBytes or fewer bytes read from the entity
     *
     * @throws IOException Thrown when reading fails for any reason
     */
    protected void toByteArray(Page page, HttpEntity entity, int maxBytes) throws IOException {
        if (entity == null) {
            page.setContentData(new byte[0]);
        }
        try (InputStream is = entity.getContent()) {
            int size = (int) entity.getContentLength();
            int readBufferLength = size;

            if (readBufferLength <= 0) {
                readBufferLength = 4096;
            }
            // in case when the maxBytes is less than the actual page size
            readBufferLength = Math.min(readBufferLength, maxBytes);

            // We allocate the buffer with either the actual size of the entity (if available)
            // or with the default 4KiB if the server did not return a value to avoid allocating
            // the full maxBytes (for the cases when the actual size will be smaller than maxBytes).
            ByteArrayBuffer buffer = new ByteArrayBuffer(readBufferLength);

            byte[] tmpBuff = new byte[4096];
            int dataLength;
            boolean truncated = false;
            while ((dataLength = is.read(tmpBuff)) != -1) {
                if (maxBytes > 0 && (buffer.length() + dataLength) > maxBytes) {
                    truncated = true;
                    dataLength = maxBytes - buffer.length();
                }
                buffer.append(tmpBuff, 0, dataLength);
                if (truncated) {
                    break;
                }
            }
            page.setTrunctated(truncated);
            page.setContentData(buffer.toByteArray());
        }
    }



    public void discardContentIfNotConsumed() {
        try {
            if (entity != null) {
                EntityUtils.consume(entity);
            }
        } catch (IOException ignored) {
            // We can EOFException (extends IOException) exception. It can happen on compressed
            // streams which are not
            // repeatable
            // We can ignore this exception. It can happen if the stream is closed.
        } catch (Exception e) {
            logger.warn("Unexpected error occurred while trying to discard content", e);
        }
    }

    public String getMovedToUrl() {
        return movedToUrl;
    }

    public void setMovedToUrl(String movedToUrl) {
        this.movedToUrl = movedToUrl;
    }
}
