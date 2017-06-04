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
import org.ops4j.krabbl.core.spi.Parser;
import org.ops4j.krabbl.core.url.UrlCanonicalizer;
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
            parseData.setHtml(document.html());
            parseData.setTitle(document.title());
            parseData.setText(document.text());

            Elements links = document.select("a[href]");
            for (Element link : links) {
                String href = link.attr("href");
                String outgoing = UrlCanonicalizer.getCanonicalURL(href, contextUrl);
                if (outgoing != null) {
                    WebTarget webURL = new WebTarget();
                    webURL.setUrl(outgoing);
                    webURL.setParentUrl(page.getWebTarget().getUrl());
                    webURL.setDepth(page.getWebTarget().getDepth() + 1);
                    parseData.getOutgoingUrls().add(webURL);
                }
            }
        }
        catch (IOException exc) {
            logger.error("Error parsing content of {}", page, exc);
        }
    }

}
