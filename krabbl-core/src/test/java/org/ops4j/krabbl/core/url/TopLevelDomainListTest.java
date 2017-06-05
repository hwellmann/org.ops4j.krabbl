package org.ops4j.krabbl.core.url;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.ops4j.krabbl.api.WebTarget;


public class TopLevelDomainListTest {

    private WebTarget webUrl;

    private void setUrl(String url) {
        webUrl = new WebTargetBuilder(url).build();
    }

    @Test
    public void testTLD() {

        setUrl("http://example.com");
        assertEquals("example.com", webUrl.getDomain());
        assertEquals("", webUrl.getSubDomain());

        setUrl("http://test.example.com");
        assertEquals("example.com", webUrl.getDomain());
        assertEquals("test", webUrl.getSubDomain());

        setUrl("http://test2.test.example.com");
        assertEquals("example.com", webUrl.getDomain());
        assertEquals("test2.test", webUrl.getSubDomain());

        setUrl("http://test3.test2.test.example.com");
        assertEquals("example.com", webUrl.getDomain());
        assertEquals("test3.test2.test", webUrl.getSubDomain());

        setUrl("http://www.example.ac.jp");
        assertEquals("example.ac.jp", webUrl.getDomain());
        assertEquals("www", webUrl.getSubDomain());

        setUrl("http://example.ac.jp");
        assertEquals("example.ac.jp", webUrl.getDomain());
        assertEquals("", webUrl.getSubDomain());
    }
}