package com.devJava.client_app.service;

import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {
    public String sayHello(String name) {
        return "Hello World " + name;
    }
}
