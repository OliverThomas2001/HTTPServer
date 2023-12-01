package HTTPServer.Server;

import HTTPServer.Http.HttpRequest;
import HTTPServer.Http.HttpResponse;

public interface RequestHandler {
    void handleHttpRequest(HttpRequest req, HttpResponse res);
}
