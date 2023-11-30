package Project.Server;

import Project.Http.HttpRequest;
import Project.Http.HttpResponse;

public interface RequestHandler {
    void handleHttpRequest(HttpRequest req, HttpResponse res);
}
