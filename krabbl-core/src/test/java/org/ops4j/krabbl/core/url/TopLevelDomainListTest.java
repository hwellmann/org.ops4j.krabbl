package org.ops4j.krabbl.core.url;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.ops4j.krabbl.api.WebTarget;

@RunWith(Parameterized.class)
public class TopLevelDomainListTest {

    @Parameter(0)
    public String url;

    @Parameter(1)
    public String domain;

    @Parameter(2)
    public String subdomain;

    private WebTarget webUrl;

    @Parameters(name = "{index}: {0}")
    public static Object[][] data() {
        return new Object[][] {
            { "http://example.com", "example.com", ""},
            { "http://test.example.com", "example.com", "test"},
            { "http://test2.test.example.com", "example.com", "test2.test"},
            { "http://test3.test2.test.example.com", "example.com", "test3.test2.test"},
            { "http://www.example.ac.jp", "example.ac.jp", "www"},
            { "http://example.ac.jp", "example.ac.jp", ""},
            { "http://myhost.mycompany.local", "mycompany.local", "myhost"},
            { "http://jenkins.ci.sabio.de", "sabio.de", "jenkins.ci"},
            { "http://www.sabio.de", "sabio.de", "www"},
            { "http://ops4j.github.io", "ops4j.github.io", ""},
            { "http://test.kobe.jp", "kobe.jp", "test"},
            { "http://test2.test.kobe.jp", "kobe.jp", "test2.test"},
        };
    }


    @Test
    public void testTLD() {
        webUrl = new WebTargetBuilder(url).build();

        assertThat(webUrl.getDomain()).isEqualTo(domain);
        assertThat(webUrl.getSubdomain()).isEqualTo(subdomain);
        assertThat(webUrl.getDepth()).isEqualTo(0);
        assertThat(webUrl.getReferringUrl()).isNull();
    }
}
