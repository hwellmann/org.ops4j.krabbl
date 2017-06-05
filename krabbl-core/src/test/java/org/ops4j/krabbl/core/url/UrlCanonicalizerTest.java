package org.ops4j.krabbl.core.url;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UrlCanonicalizerTest {

    @Test
    public void testCanonizalier() {

        assertEquals("http://www.example.com/display?category=foo%2Fbar%2Bbaz",
                     UrlCanonicalizer.getCanonicalURL(
                         "http://www.example.com/display?category=foo/bar+baz"));

        assertEquals("http://www.example.com/?q=a%2Bb",
                     UrlCanonicalizer.getCanonicalURL("http://www.example.com/?q=a+b"));

        assertEquals("http://www.example.com/display?category=foo%2Fbar%2Bbaz",
                     UrlCanonicalizer.getCanonicalURL(
                         "http://www.example.com/display?category=foo%2Fbar%2Bbaz"));

        assertEquals("http://somedomain.com/uploads/1/0/2/5/10259653/6199347.jpg?1325154037",
                     UrlCanonicalizer.getCanonicalURL(
                         "http://somedomain.com/uploads/1/0/2/5/10259653/6199347.jpg?1325154037"));

        assertEquals("http://hostname.com/",
                     UrlCanonicalizer.getCanonicalURL("http://hostname.com"));

        assertEquals("http://hostname.com/",
                     UrlCanonicalizer.getCanonicalURL("http://HOSTNAME.com"));

        assertEquals("http://www.example.com/index.html",
                     UrlCanonicalizer.getCanonicalURL("http://www.example.com/index.html?&"));

        assertEquals("http://www.example.com/index.html",
                     UrlCanonicalizer.getCanonicalURL("http://www.example.com/index.html?"));

        assertEquals("http://www.example.com/",
                     UrlCanonicalizer.getCanonicalURL("http://www.example.com"));

        assertEquals("http://www.example.com/bar.html",
                     UrlCanonicalizer.getCanonicalURL("http://www.example.com:80/bar.html"));

        assertEquals("http://www.example.com/index.html?name=test&rame=base",
                     UrlCanonicalizer.getCanonicalURL(
                         "http://www.example.com/index.html?name=test&rame=base#123"));

        assertEquals("http://www.example.com/~username/",
                     UrlCanonicalizer.getCanonicalURL("http://www.example.com/%7Eusername/"));

        assertEquals("http://www.example.com/A/B/index.html",
                     UrlCanonicalizer.getCanonicalURL("http://www.example.com//A//B/index.html"));

        assertEquals("http://www.example.com/index.html?x=y",
                     UrlCanonicalizer.getCanonicalURL("http://www.example.com/index.html?&x=y"));

        assertEquals("http://www.example.com/a.html",
                     UrlCanonicalizer.getCanonicalURL("http://www.example.com/../../a.html"));

        assertEquals("http://www.example.com/a/c/d.html", UrlCanonicalizer.getCanonicalURL(
            "http://www.example.com/../a/b/../c/./d.html"));

        assertEquals("http://foo.bar.com/?baz=1",
                     UrlCanonicalizer.getCanonicalURL("http://foo.bar.com?baz=1"));

        assertEquals("http://www.example.com/index.html?c=d&e=f&a=b",
                     UrlCanonicalizer.getCanonicalURL(
                         "http://www.example.com/index.html?&c=d&e=f&a=b"));

        assertEquals("http://www.example.com/index.html?q=a%20b",
                     UrlCanonicalizer.getCanonicalURL("http://www.example.com/index.html?q=a b"));

        assertEquals("http://www.example.com/search?width=100%&height=100%",
                     UrlCanonicalizer.getCanonicalURL(
                         "http://www.example.com/search?width=100%&height=100%"));

        assertEquals("http://foo.bar/mydir/myfile?page=2",
                     UrlCanonicalizer.getCanonicalURL("?page=2", "http://foo.bar/mydir/myfile"));

    }
}