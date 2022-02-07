package com.bartbruneel.udemy.service;

import io.micronaut.context.annotation.Primary;
import jakarta.inject.Singleton;

@Primary
@Singleton
public class HelloWorldService implements MyService {

    @Override
    public String helloFromService() {
        return "Hello from Service!";
    }

}
