package org.opm.busybeaver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    private static final String template = "Welcome, to Busy Beaver Project Management!";

    @GetMapping("/greeting")
    public HelloWorld helloWorld() {
        return new HelloWorld(template);
    }
}
