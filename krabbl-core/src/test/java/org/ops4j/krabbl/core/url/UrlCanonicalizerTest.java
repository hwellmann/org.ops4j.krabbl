package org.ops4j.krabbl.core.url;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UrlCanonicalizerTest {

    @Test
    public void testCanonizalier() {

        assertEquals("http://www.example.com/display?category=foo%2Fbar%2Bbaz",
                     UrlNormalizer.normalize(
                         "http://www.example.com/display?category=foo/bar+baz"));

        assertEquals("http://www.example.com/?q=a%2Bb",
                     UrlNormalizer.normalize("http://www.example.com/?q=a+b"));

        assertEquals("http://www.example.com/display?category=foo%2Fbar%2Bbaz",
                     UrlNormalizer.normalize(
                         "http://www.example.com/display?category=foo%2Fbar%2Bbaz"));

        assertEquals("http://somedomain.com/uploads/1/0/2/5/10259653/6199347.jpg?1325154037",
                     UrlNormalizer.normalize(
                         "http://somedomain.com/uploads/1/0/2/5/10259653/6199347.jpg?1325154037"));

        assertEquals("http://hostname.com/",
                     UrlNormalizer.normalize("http://hostname.com"));

        assertEquals("http://hostname.com/",
                     UrlNormalizer.normalize("http://HOSTNAME.com"));

        assertEquals("http://www.example.com/index.html",
                     UrlNormalizer.normalize("http://www.example.com/index.html?&"));

        assertEquals("http://www.example.com/index.html",
                     UrlNormalizer.normalize("http://www.example.com/index.html?"));

        assertEquals("http://www.example.com/",
                     UrlNormalizer.normalize("http://www.example.com"));

        assertEquals("http://www.example.com/bar.html",
                     UrlNormalizer.normalize("http://www.example.com:80/bar.html"));

        assertEquals("http://www.example.com/index.html?name=test&rame=base",
                     UrlNormalizer.normalize(
                         "http://www.example.com/index.html?name=test&rame=base#123"));

        assertEquals("http://www.example.com/~username/",
                     UrlNormalizer.normalize("http://www.example.com/%7Eusername/"));

        assertEquals("http://www.example.com/A/B/index.html",
                     UrlNormalizer.normalize("http://www.example.com//A//B/index.html"));

        assertEquals("http://www.example.com/index.html?x=y",
                     UrlNormalizer.normalize("http://www.example.com/index.html?&x=y"));

        assertEquals("http://www.example.com/a.html",
                     UrlNormalizer.normalize("http://www.example.com/../../a.html"));

        assertEquals("http://www.example.com/a/c/d.html", UrlNormalizer.normalize(
            "http://www.example.com/../a/b/../c/./d.html"));

        assertEquals("http://foo.bar.com/?baz=1",
                     UrlNormalizer.normalize("http://foo.bar.com?baz=1"));

        assertEquals("http://www.example.com/index.html?c=d&e=f&a=b",
                     UrlNormalizer.normalize(
                         "http://www.example.com/index.html?&c=d&e=f&a=b"));

        assertEquals("http://www.example.com/index.html?q=a%20b",
                     UrlNormalizer.normalize("http://www.example.com/index.html?q=a b"));

        assertEquals("http://www.example.com/search?width=100%&height=100%",
                     UrlNormalizer.normalize(
                         "http://www.example.com/search?width=100%&height=100%"));

        assertEquals("http://foo.bar/mydir/myfile?page=2",
                     UrlNormalizer.normalize("?page=2", "http://foo.bar/mydir/myfile"));

    }
}