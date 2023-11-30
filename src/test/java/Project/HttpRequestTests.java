package Project;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import Project.Http.HttpRequest;
import Project.Server.BasicServer;

public class HttpRequestTests {
    @BeforeClass
    public static void setUp(){
        BasicServer server = new BasicServer();
        server.addRoute("GET", "/", (req, res) -> {
            
        });
    }

    @Test //Update this one at a later date with all other valid methods.
    public void testMethodValidation() {

        HttpRequest request1 = new HttpRequest("GET / HTTP/1.1");
        assertTrue(request1.getRequestValidity());

        HttpRequest request2 = new HttpRequest("get / HTTP/1.1");
        assertFalse(request2.getRequestValidity());

        HttpRequest request3 = new HttpRequest("GEt / HTTP/1.1");
        assertFalse(request3.getRequestValidity());
    }

    @Test
    public void testUriValidation() {
        HttpRequest request1 = new HttpRequest("GET / HTTP/1.1");
        assertTrue(request1.getRequestValidity());

        // No Uri given
        HttpRequest request2 = new HttpRequest("GET  HTTP/1.1");
        assertFalse(request2.getRequestValidity());

        // Valid Uri, but not defined.
        HttpRequest request3 = new HttpRequest("GET /new HTTP/1.1");
        assertFalse(request3.getRequestValidity());

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

    @Test
    public void testHttpVersionValidation() {

        // Correct version
        HttpRequest request1 = new HttpRequest("GET / HTTP/1.1");
        assertTrue(request1.getRequestValidity());

        // Incorrect version
        HttpRequest request2 = new HttpRequest("GET / HTTP/1.0");
        assertFalse(request2.getRequestValidity());

    }

}
