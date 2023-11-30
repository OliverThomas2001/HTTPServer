package Project.Server;

import Project.Http.HttpRequest;

public interface RequestHandler {
    void handleHttpRequest(HttpRequest req);
}
