package HTTPServer;

import org.junit.Test;

import HTTPServer.Http.HttpRequest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class HttpRequestTests {

    @Test
    public void testUriValidation() {
        HttpRequest request1 = new HttpRequest("GET / HTTP/1.1");
        assertTrue(request1.getRequestValidity());

        // No Uri given
        HttpRequest request2 = new HttpRequest("GET  HTTP/1.1");
        assertFalse(request2.getRequestValidity());

        // Defined path, invalid Uri
        HttpRequest request4 = new HttpRequest("GET /? HTTP/1.1");
        assertFalse(request4.getRequestValidity());

        // Defined path, invalid Uri
        HttpRequest request5 = new HttpRequest("GET /?param1 HTTP/1.1");
        assertFalse(request5.getRequestValidity());

        // Defined path, invalid Uri
        HttpRequest request6 = new HttpRequest("GET /?param1= HTTP/1.1");
        assertFalse(request6.getRequestValidity());

        // Defined path, Valid Uri
        HttpRequest request7 = new HttpRequest("GET /?param1=value1 HTTP/1.1");
        assertTrue(request7.getRequestValidity());

        // Defined path, Valid Uri
        HttpRequest request8 = new HttpRequest("GET /?param1=value1&param2=value2 HTTP/1.1");
        assertTrue(request8.getRequestValidity());

        // Defined path, Invalid Uri
        HttpRequest request9 = new HttpRequest("GET /?param1=value1,param2=value2 HTTP/1.1");
        assertFalse(request9.getRequestValidity());
    }

}
