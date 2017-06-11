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

import java.io.Serializable;

/**
 * @author Yasser Ganjisaffar
 */

public interface WebTarget extends Serializable {

    /**
     * Gets the URL of the first encountered page referring to this target.
     *
     * @return referring URL, or null if this target is a seed
     */
    String getReferringUrl();

    /**
     * Gets the depth at which this target is first encountered. Seeds have depth 0, URLs extracted
     * from a seed have depth 1 etc.
     * <p>
     * A value of -1 indicates that the target has been visited before.
     *
     * @return depth of this target
     */
    int getDepth();

    /**
     * Gets the registered domain part of the host name of this URL. This part is computed with
     * using the <a href="https://publicsuffix.org/">Public Suffix List</a>. The domain is composed
     * of the public suffix and the name part preceding the suffix.
     *
     * @return domain of this URL, e.g. for {@code http://de.wikipedia.org}, the domain is
     *         {@code wikipedia.org}.
     */
    String getDomain();

    /**
     * Gets the subdomain part of the host name of this URL. This is the leading part of the host
     * name of this target up to the domain part and not including the dot separator. The result can
     * be empty, but never null.
     *
     * @return subdomain of this URL, e.g. for {@code http://de.wikipedia.org}, the domain is
     *         {@code de}.
     */
    String getSubdomain();

    /**
     * Gets the normalized URL of this target. The
     * <a href="https://en.wikipedia.org/wiki/URL_normalization">normalized form</a> is used for
     * deciding whether two given targets are equal.
     *
     * @return normalized URL
     */
    String getUrl();

    /**
     * Gets the path of the normalized URL of this target.
     *
     * @return path of this target. E.g. for {@code https://en.wikipedia.org/wiki/Crawler}, the path
     *         will be {@code wiki/Crawler}.
     */
    String getPath();
}
