package HTTPServer.Http;

import java.util.Map;
import java.util.HashMap;

public class HttpResponse {
    private String httpVersion;
    private int statusCode;
    private Map<String, String> responseHeaders = new HashMap<>();
    private String responseBody;

    
        // this.httpVersion = version;
        // this.statusCode = code;

        // statusMessage = httpVersion + " " + statusCode + " " + StatusCodes.statusCodeMap.get(statusCode);
    public void setStatusCode(int code) {
        statusCode = code;
    }

    public void setHttpVersion(String version) {
        httpVersion = version;
    }

    public void addHeader(String key, String value) {
        responseHeaders.put(key, value);
    }

    public void addBody(String body) {
        responseBody = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        if (httpVersion == null || statusCode == 0) {
            return "HTTP/1.1 500 Internal Server Error";
        }
        return httpVersion + " " + statusCode + " " + StatusCodes.statusCodeMap.get(statusCode);
    }

    public String[] getHeaderArray() {
        String[] headerArray = new String[responseHeaders.size()];
        int i = 0;
        for (String headerType : responseHeaders.keySet()) {
            headerArray[i] = headerType + ":" + responseHeaders.get(headerType) + "\r";
            i++;
        }
        return headerArray;
    }

    public String getResponseBody() {
        return responseBody;
    }


}
