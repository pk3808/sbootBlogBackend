package com.pensieri.blogbackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);
    
    @GetMapping("/hello")
    public String helloWorld() {
        logger.info("Hello World endpoint accessed");
        return "Hello World!";
    }
    
    @GetMapping("/")
    public String home() {
        logger.info("Home endpoint accessed");
        return "Welcome to the Pensieri Blog Backend!";
    }
}
