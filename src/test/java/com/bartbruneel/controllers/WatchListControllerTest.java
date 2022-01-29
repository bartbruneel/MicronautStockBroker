package com.bartbruneel.controllers;

import com.bartbruneel.data.InMemoryAccountStore;
import com.bartbruneel.models.Symbol;
import com.bartbruneel.models.WatchList;
import com.fasterxml.jackson.databind.JsonNode;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static com.bartbruneel.data.InMemoryAccountStore.ACCOUNT_ID;
import static io.micronaut.http.HttpRequest.GET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID TEST_ACCOUNT_ID = ACCOUNT_ID;

    @Inject
    @Client("/account/watchlist")
    HttpClient client;

    @Inject
    InMemoryAccountStore inMemoryAccountStore;

    @BeforeEach
    void setUp() {
        inMemoryAccountStore.deleteWatchList(TEST_ACCOUNT_ID);
    }

    @Test
    void test_returnsEmptyWatchList() {
        final WatchList result = client.toBlocking().retrieve(GET("/"), WatchList.class);
        assertNull(result.symbols());
        assertTrue(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
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



    @Test
    void test_canUpdateWatchListForTestAccount() {
        var symbols = Stream.of("AAPL", "GOOGL", "MSFT").map(Symbol::new).toList();
        MutableHttpRequest<WatchList> request = HttpRequest.PUT("/", new WatchList(symbols))
                .accept(MediaType.APPLICATION_JSON);
        HttpResponse<Object> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(symbols, inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols());
    }

    @Test
    void canDeleteWatchListForTestAccount() {
        addWatchListForTestAccount();
        assertFalse(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
        MutableHttpRequest<Object> request = HttpRequest.DELETE("/");
        HttpResponse<Object> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        assertTrue(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
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
