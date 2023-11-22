package Project.HTTP;

import java.util.HashMap;

public class HTTPRequest {
    private String reqLine;
    private HashMap<String, String> headers = new  HashMap<String, String>();

    
    // Will eventually need to be amended to handle request body
    public HTTPRequest(String method, String uri) {
        reqLine = method + " " + uri + " " + "HTTP/1.1";
    }

    public void addHeader(String headerType, String headerValue) {
        headers.put(headerType, headerValue);
    }

    public void addBody() {
        // For future development.
    }

    // Method intended to be used ny server to validate incoming requests.
    public static Boolean validateRequestLine(String requestLine) {
        String[] reqStrings = requestLine.split(" ");

         if (validateMethod(reqStrings[0]) && validateURI(reqStrings[1]) && validateHTTPVersion(reqStrings[2]) && reqStrings.length == 3) return true;
         return false;
    }

    public static Boolean validateMethod(String method) {
        switch (method){
            case "GET":
                return true;
            case "HEAD":
                return true;
            case "POST":
                return true;
            case "PUT":
                return true;
            case "DELETE":
                return true;
            case "CONNECT":
                return true;
            case "OPTIONS":
                return true;
            case "TRACE":
                return true;
            default:
                return false;
        }
    }
    
    public static Boolean validateURI(String uri) {
        // want to only allow alphanumeric characters and '/', uri must start with '/'.
        return uri.matches("^/[a-zA-Z0-9/]*$");
    }

    public static Boolean validateHTTPVersion(String version) {
        // At present, I only plan to use version 1.1 but could extend to other versions at a later date.
        return version.equals("HTTP/1.1");
    }

    public String getRequestLine() {
        return this.reqLine;
    }

    // Note to self, this could likely be optimised by just storing the headers in a dynamic array to begin with.
    public String[] getHeaderArray() {
        String[] headerArray = new String[headers.size()];
        int i = 0;
        for (String headerType : headers.keySet()) {
            headerArray[i] = headerType + ": " + headers.get(headerType);
            i++;
        }
        return headerArray;
    }
}
