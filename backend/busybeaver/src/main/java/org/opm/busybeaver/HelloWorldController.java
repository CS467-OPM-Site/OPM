package org.opm.busybeaver;

import org.opm.busybeaver.controller.ApiPrefixController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiPrefixController
public class HelloWorldController {

    private static final String template = "Welcome, to Busy Beaver Project Management!";

    @GetMapping("/greeting")
    public HelloWorld helloWorld() {
        return new HelloWorld(template);
    }
}
