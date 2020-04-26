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
package org.ops4j.krabbl.core.crawl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.ops4j.krabbl.api.Page;
import org.ops4j.krabbl.api.PageVisitor;
import org.ops4j.krabbl.api.WebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolkiesVisitor implements PageVisitor {

    private static Logger log = LoggerFactory.getLogger(FolkiesVisitor.class);
    private File outputDir;


    public FolkiesVisitor(File outputDir) {
        this.outputDir = outputDir;
    }


    @Override
    public boolean shouldVisit(Page referringPage, WebTarget url) {
        return referringPage == null || url.getPath().contains("folkies/files/Tunes");
    }

    @Override
    public void visit(Page page) {
        int numLinks = 0;
        if (page.getParseData() != null) {
            numLinks = page.getParseData().getOutgoingUrls().size();
        }
        log.info("Visiting {} at depth {} with {} outgoing links", page.getWebTarget().getUrl(),
            page.getWebTarget().getDepth(), numLinks);

        if (page.getContentType().equals("text/plain")) {
            saveTune(page);
        }
    }

    private synchronized void saveTune(Page page) {
        String fileName = extractFileName(page);
        File outputFile = new File(outputDir, fileName);

        try (OutputStream os = new FileOutputStream(outputFile)) {
            os.write(page.getContentData());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private String extractFileName(Page page) {
        String path = page.getWebTarget().getPath();
        int slash = path.lastIndexOf('/');
        String fileNameEncoded = path.substring(slash + 1);
        try {
            return URLDecoder.decode(fileNameEncoded, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}