package Project.Http;

import java.util.HashMap;
import java.util.Arrays;

import Project.Server.BasicServer;

public class HttpRequest {
    private Boolean isRequestValid;
    private String requestMethod;
    private String requestUri;
    private String requestPath;
    private String requestHttpVersion;

    private HashMap<String, Object> requestParameters = new HashMap<>(); // this may need to be changed to determine the type of the value.
    private HashMap<String, String> requestHeaders = new HashMap<>();

    
    // Needs to be amended to handle request body.
    public HttpRequest(String requestLine) {
        String[] requestLineComponents = requestLine.split(" ");

        System.out.println(Arrays.toString(requestLineComponents));

        requestMethod = requestLineComponents[0];
        requestUri = requestLineComponents[1];
        requestPath = requestUri.split("\\?")[0];
        requestHttpVersion = requestLineComponents[2];
        

        isRequestValid = validateRequestLine(requestLine);
        System.out.println("URI: "+ requestUri);
        System.out.println("URIValidity Test: "+ validateUri());

        if (requestUri.contains("?") && isRequestValid){ // if parameters exist in the request line ...
            //Need to split the uri into the path and parameters.
            String[] requestUriComponents = requestLineComponents[1].split("\\?");
            requestPath = requestUriComponents[0];

            System.out.println(Arrays.toString(requestUriComponents));

            String[] requestParameterArray = requestUriComponents[1].split("\\&");

            for (String parameter : requestParameterArray) {
                String[] parameterNameValue = parameter.split("\\=");
                if (parameterNameValue.length == 2){
                    requestParameters.put(parameterNameValue[0], parameterNameValue[1]);
                } else {
                    isRequestValid = false;
                }
                
            }
        } else {
            requestPath = requestUri;
        }
    }

    public Boolean getRequestValidity() {
        return isRequestValid;
    }

    public String getRequestMethod(){
        return requestMethod;
    }

    public String getRequestPath(){
        return requestPath;
    }

    // Note to self, this could likely be optimised by just storing the headers in a dynamic array to begin with.
    public String[] getHeaderArray() {
        String[] headerArray = new String[requestHeaders.size()];
        int i = 0;
        for (String headerType : requestHeaders.keySet()) {
            headerArray[i] = headerType + ": " + requestHeaders.get(headerType);
            i++;
        }
        return headerArray;
    }

    public void addHeader(String headerType, String headerValue) {
        requestHeaders.put(headerType, headerValue);
    }

    public void addBody() {
        // For future development.
    }

    // Method intended to be used ny server to validate incoming requests.
    private Boolean validateRequestLine(String requestLine) {

         if (validateMethod() && validateUri() && validateHttpVersion() && requestLine.split(" ").length == 3) return true;
         return false;
    }

    public Boolean validateMethod() {
        switch (requestMethod){
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
    
    public Boolean validateUri() {
        // want to only allow alphanumeric characters and '/', uri must start with '/'.
        return requestUri.matches("^/[a-zA-Z0-9/]*(\\?[A-Za-z0-9]+=[A-Za-z0-9]+(&[A-Za-z0-9]+=[A-Za-z0-9]+)*)?$"); //&& BasicServer.isPermittedRoute(requestMethod, requestPath)
    }

    public Boolean validateHttpVersion() {
        // At present, I only plan to use version 1.1 but could extend to other versions at a later date.
        return requestHttpVersion.equals("HTTP/1.1");
    }
}
