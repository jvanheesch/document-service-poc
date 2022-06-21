package com.github.jvanheesch.document;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello/1")
    public String hello1() {
        return "hello1";
    }

    @GetMapping("/hello/2")
    public String hello2() {
        return "hello2";
    }

    @GetMapping("/hello/3")
    public String hello3() {
        return "hello3";
    }
}
