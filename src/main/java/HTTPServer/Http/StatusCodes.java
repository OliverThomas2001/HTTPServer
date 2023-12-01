package HTTPServer.Http;

import java.util.Map;
import java.util.HashMap;


public class StatusCodes {
    public static final Map<Integer, String> statusCodeMap = new HashMap<>();
    // Theses are the error codes I intend to implement for now.
    static {
        
        statusCodeMap.put(200, "OK");
        statusCodeMap.put(201, "Created");
        statusCodeMap.put(204, "No Content");

        statusCodeMap.put(400, "Bad Request");
        statusCodeMap.put(401, "Unauthorised");
        statusCodeMap.put(402, "Payment Required");
        statusCodeMap.put(403, "Forbidden");
        statusCodeMap.put(404, "Not Found");
        statusCodeMap.put(406, "Not Acceptable");
        statusCodeMap.put(408, "Request Timeout");
        statusCodeMap.put(413, "Request Too Large");
        statusCodeMap.put(414, "Request-URI Too Long");
        statusCodeMap.put(415, "Unsupported Media Type");

        statusCodeMap.put(500, "Internal Server Error");
        statusCodeMap.put(501, "Not Implemented");
        statusCodeMap.put(503, "Service Unavailable");
        statusCodeMap.put(505, "HTTP Version Not Supported");
    }
}
