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
package org.ops4j.krabbl.core.parse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ops4j.krabbl.api.Page;
import org.ops4j.krabbl.api.WebTarget;
import org.ops4j.krabbl.core.exc.ParseException;
import org.ops4j.krabbl.core.spi.Parser;
import org.ops4j.krabbl.core.url.UrlCanonicalizer;
import org.ops4j.krabbl.core.url.WebTargetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Harald Wellmann
 *
 */
public class JsoupHtmlParser implements Parser {

    private Logger logger = LoggerFactory.getLogger(JsoupHtmlParser.class);

    @Override
    public void parse(Page page, String contextUrl) {
        HtmlParseData parseData = new HtmlParseData();
        page.setParseData(parseData);
        try (InputStream is = new ByteArrayInputStream(page.getContentData())) {
            Document document = Jsoup.parse(is, page.getContentCharset(), contextUrl);
            parseDocument(page, contextUrl, document, parseData);
        }
        catch (IOException exc) {
            logger.error("Error parsing content of {}", page, exc);
            throw new ParseException();
        }
    }

    private void parseDocument(Page page, String contextUrl, Document document, HtmlParseData parseData) {
        parseData.setHtml(document.html());
        parseData.setTitle(document.title());
        parseData.setText(document.text());

        findMetaTags(page, contextUrl, document);
        findHrefLinks(page, contextUrl, document);
        findSrcLinks(page, contextUrl, document);
    }

    private void findHrefLinks(Page page, String contextUrl, Document document) {
        Elements links = document.select("a[href], area[href], link[href]");
        for (Element link : links) {
            addToOutgoingUrls(page, contextUrl, link.attr("href"));
        }
    }

    private void findSrcLinks(Page page, String contextUrl, Document document) {
        Elements links = document.select("img[src], embed[src], frame[src], iframe[src], script[src]");
        for (Element link : links) {
            addToOutgoingUrls(page, contextUrl, link.attr("src"));
        }
    }

    private void addToOutgoingUrls(Page page, String contextUrl, String relativeUrl) {
        String outgoing = UrlCanonicalizer.getCanonicalURL(relativeUrl, contextUrl);
        if (outgoing != null) {
            WebTarget target = buildWebTarget(outgoing, page.getWebTarget());
            page.getParseData().getOutgoingUrls().add(target);
        }
    }

    private void findMetaTags(Page page, String contextUrl, Document document) {
        Elements metas = document.select("meta");
        for (Element meta : metas) {
            parseMetaTag(meta, page, contextUrl);
        }
    }

    private void parseMetaTag(Element meta, Page page, String contextUrl) {
        String equiv = meta.attr("http-equiv");
        if (equiv == null) {
            equiv = meta.attr("name");
        }

        String content = meta.attr("content");
        if (equiv != null && content != null) {

            // http-equiv="refresh" content="0; URL=http://foo.bar/..."
            if ("refresh".equals(equiv)) {
                int pos = content.toLowerCase().indexOf("url=");
                if (pos != -1) {
                    String metaRefresh = content.substring(pos + 4);
                    addToOutgoingUrls(page, contextUrl, metaRefresh);
                }
            }

            // http-equiv="location" content="http://foo.bar/..."
            if ("location".equals(equiv)) {
                addToOutgoingUrls(page, contextUrl, content);
            }
        }
    }

    private WebTarget buildWebTarget(String outgoing, WebTarget parent) {
        WebTarget webTarget = new WebTargetBuilder(outgoing).build();
        webTarget.setUrl(outgoing);
        webTarget.setParentUrl(parent.getUrl());
        webTarget.setDepth(parent.getDepth() + 1);
        return webTarget;
    }
}
