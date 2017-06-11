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

/**
 * Builds a web target from a given URL. The URL of the resulting target will be normalized.
 *
 * @author Harald Wellmann
 *
 */
public class WebTargetBuilder {

    private String url;

    /**
     * Creates a web target for the given URL.
     *
     * @param url
     *            URL to be normalized
     */
    public WebTargetBuilder(String url) {
        this.url = url;
    }

    public WebTargetImpl build() {
        url = UrlNormalizer.normalize(url);
        int domainStartIdx = url.indexOf("//") + 2;
        int domainEndIdx = url.indexOf('/', domainStartIdx);
        domainEndIdx = (domainEndIdx > domainStartIdx) ? domainEndIdx : url.length();
        String domain = url.substring(domainStartIdx, domainEndIdx);
        String subDomain = "";
        String[] parts = domain.split("\\.");
        if (parts.length > 2) {
            domain = parts[parts.length - 2] + "." + parts[parts.length - 1];
            int limit = 2;
            if (TopLevelDomainList.getInstance().contains(domain)) {
                domain = parts[parts.length - 3] + "." + domain;
                limit = 3;
            }
            for (int i = 0; i < (parts.length - limit); i++) {
                if (!subDomain.isEmpty()) {
                    subDomain += ".";
                }
                subDomain += parts[i];
            }
        }
        String path = url.substring(domainEndIdx);
        int pathEndIdx = path.indexOf('?');
        if (pathEndIdx >= 0) {
            path = path.substring(0, pathEndIdx);
        }

        WebTargetImpl target = new WebTargetImpl();
        target.setUrl(url);
        target.setDomain(domain);
        target.setSubdomain(subDomain);
        target.setPath(path);
        return target;
    }

}
