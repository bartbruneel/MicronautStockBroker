package com.bartbruneel.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.json.tree.JsonNode;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class HelloWorldControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void helloWorldEndpointRespondsWithProperContent() {
        var response = client.toBlocking().retrieve("/hello");
        assertEquals("Hello from Service!", response);
    }

    @Test
    void helloWorldEndpointRespondsWithProperStatusCodeAndContent() {
        var response = client.toBlocking().exchange("/hello", String.class);
        assertEquals("Hello from Service!", response.getBody().get());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void helloFromConfigEndpointReturnsMessageFromConfigFile() {
        var response = client.toBlocking().exchange("/hello/config", String.class);
        assertEquals("Hello from application-test.yml", response.getBody().get());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void helloFromTranslationEndpointEndpointReturnsContentFromConfigFile() {
        var response = client.toBlocking().exchange("/hello/translation", JsonNode.class);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJsonString = gson.toJson(response.getBody().get());
        assertEquals("{\n" +
                "  \"values\": {\n" +
                "    \"de\": {\n" +
                "      \"value\": \"Hallo Welt\"\n" +
                "    },\n" +
                "    \"en\": {\n" +
                "      \"value\": \"Hello World\"\n" +
                "    }\n" +
                "  }\n" +
                "}", prettyJsonString);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

}
