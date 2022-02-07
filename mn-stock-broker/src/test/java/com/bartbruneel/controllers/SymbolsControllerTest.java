package com.bartbruneel.controllers;

import com.bartbruneel.data.InMemoryStore;
import com.bartbruneel.models.Symbol;
import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.micronaut.http.server.netty.types.stream.NettyStreamedCustomizableResponseType.LOG;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class SymbolsControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(SymbolsControllerTest.class);

    @Inject
    @Client("/symbols")
    HttpClient client;

    @Inject
    InMemoryStore inMemoryStore;

    @BeforeEach
    void setUp() {
        inMemoryStore.initializeWith(10);
    }

    @Test
    void test_symbolsEndpoint_returnsListOfSymbol() {
        var response = client.toBlocking().exchange("/", JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(10, response.getBody().get().size());
    }

    @Test
    void test_symbolsEndpoint_returnsTheCorrectSymbol() {
        var testSymbol = new Symbol("TEST");
        inMemoryStore.getSymbols().put(testSymbol.value(), testSymbol);
        var response = client.toBlocking().exchange("/" + testSymbol.value(), Symbol.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(testSymbol, response.getBody().get());
    }

    @Test
    void test_symbolsEndpoint_filter_returnMaxSymbols() {
        var response = client.toBlocking().exchange("/filter?max=10", JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        LOG.debug("Max: 10: {}", response.getBody().get().toPrettyString());
        assertEquals(10, response.getBody().get().size());
    }

    @Test
    void test_symbolsEndpoint_filter_withOffset() {
        var response = client.toBlocking().exchange("/filter?offset=7", JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        LOG.debug("Offset: 7: {}", response.getBody().get().toPrettyString());
        assertEquals(3, response.getBody().get().size());
    }

    @Test
    void test_symbolsEndpoint_filter_withOffsetAndMax() {
        var response = client.toBlocking().exchange("/filter?max=2&offset=7", JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        LOG.debug("Max2 & Offset: 7: {}", response.getBody().get().toPrettyString());
        assertEquals(2, response.getBody().get().size());
    }

}
