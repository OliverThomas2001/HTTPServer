# HTTPServer
Implementing a basic HTTP server using socket connections and as few external libraries as possible.

## Goals for this Project
- Learn in greater detail about how HTTP requests are processed and how a client and server interact via sockets.
- Continue to learn the Java language.
- Learn to write unit and integration tests in Java.

## Project Status: Still In Progress
- Users can create a multi-threaded server (number of threads is determined by user) capable of making simultaneous HTTP responses.
- GET requests are functional - users can add GET routes to server.
- HTTP requests can contain query parameters, headers (although not yet functional) but not a body.
- Users can add status codes, headers and a body to HTTP responses.

### In Development
- Ability to add a body to HTTP requests enabling POST, PUT & PATCH methods to be used.
- Selected header functionality for HTTP requests (currently they are parsed and saved, but have no functionality).

## Usage Instructions
Create the BasicServer Object.
`BasicServer server = new BasicServer()`
