package uk.gov.di.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class HelloWorldResource {

    @GET
    public String helloWorld() {
        return "Hello, world!";
    }
}
