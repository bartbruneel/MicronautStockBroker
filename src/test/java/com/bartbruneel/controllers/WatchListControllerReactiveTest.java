package com.bartbruneel.controllers;

import com.bartbruneel.data.InMemoryAccountStore;
import com.bartbruneel.models.Symbol;
import com.bartbruneel.models.WatchList;
import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Stream;

import static com.bartbruneel.data.InMemoryAccountStore.ACCOUNT_ID;
import static io.micronaut.http.HttpRequest.GET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class WatchListControllerReactiveTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID TEST_ACCOUNT_ID = ACCOUNT_ID;

    @Inject
    @Client("/account/watchlist-reactive")
    HttpClient client;

    @Inject
    InMemoryAccountStore inMemoryAccountStore;

    @BeforeEach
    void setUp() {
        inMemoryAccountStore.deleteWatchList(TEST_ACCOUNT_ID);
    }

    @Test
    void test_returnsEmptyWatchList() {
        Publisher<WatchList> result = client.retrieve(GET("/"), WatchList.class);
        assertTrue(Mono.from(result).blockOptional().map(WatchList::symbols).isEmpty());
        assertTrue(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
    }

    @Test
    void test_returnsWatchListForTestAccountFlux() {
        addWatchListForTestAccount();
        var response = client.toBlocking().exchange("/flux", String.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("[AAPL,GOOGL,MFST]", response.getBody().get());
    }

    @Test
    void test_returnsWatchListForTestAccount() {
        addWatchListForTestAccount();
        var response = client.toBlocking().exchange("/", JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(replaceLineSeparators(""" 
                        {
                          "symbols" : [ {
                            "value" : "AAPL"
                          }, {
                            "value" : "GOOGL"
                          }, {
                            "value" : "MFST"
                          } ]
                        }"""),
                replaceLineSeparators(response.getBody().get().toPrettyString()));
    }

    private void addWatchListForTestAccount() {
        inMemoryAccountStore.updateWatchList(TEST_ACCOUNT_ID, new WatchList(
                Stream.of("AAPL", "GOOGL", "MFST")
                        .map(Symbol::new)
                        .toList()
        ));
    }

    private String replaceLineSeparators(String input) {
        return input.replaceAll("\n", "").replaceAll("\r", "");
    }



}
