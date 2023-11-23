package Project.Server;

import Project.HTTP.HTTPRequest;

interface LambdaInterface {
    void run(HTTPRequest req);
}