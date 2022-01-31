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
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static com.bartbruneel.data.InMemoryAccountStore.ACCOUNT_ID;
import static io.micronaut.http.HttpRequest.GET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID TEST_ACCOUNT_ID = ACCOUNT_ID;
    public static final String ACCOUNT_WATCHLIST = "/account/watchlist";

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    InMemoryAccountStore inMemoryAccountStore;

    @BeforeEach
    void setUp() {
        inMemoryAccountStore.deleteWatchList(TEST_ACCOUNT_ID);
    }

    @Test
    void test_unauthorized() {
        HttpClientResponseException httpClientResponseException = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().retrieve(ACCOUNT_WATCHLIST);
        });
        assertEquals(httpClientResponseException.getStatus(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void test_returnsEmptyWatchList() {
        String accessToken = loginUser();
        MutableHttpRequest<Object> get = GET(ACCOUNT_WATCHLIST)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(accessToken);
        final WatchList result = client.toBlocking().retrieve(get, WatchList.class);
        assertNull(result.symbols());
        assertTrue(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
    }

    @Test
    void test_returnsWatchListForTestAccount() {
        addWatchListForTestAccount();
        String accessToken = loginUser();
        MutableHttpRequest<Object> get = GET(ACCOUNT_WATCHLIST)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(accessToken);
        var response = client.toBlocking().exchange(get, JsonNode.class);
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
        String accessToken = loginUser();
        var symbols = Stream.of("AAPL", "GOOGL", "MSFT").map(Symbol::new).toList();
        MutableHttpRequest<WatchList> request = HttpRequest.PUT(ACCOUNT_WATCHLIST, new WatchList(symbols))
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(accessToken);
        HttpResponse<Object> response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(symbols, inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols());
    }

    @Test
    void canDeleteWatchListForTestAccount() {
        addWatchListForTestAccount();
        String accessToken = loginUser();
        assertFalse(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
        MutableHttpRequest<Object> request = HttpRequest.DELETE(ACCOUNT_WATCHLIST).bearerAuth(accessToken);
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

    private String loginUser() {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("my-user", "secret");
        var login = HttpRequest.POST("/login", credentials);
        HttpResponse<BearerAccessRefreshToken> response = client.toBlocking().exchange(login, BearerAccessRefreshToken.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        BearerAccessRefreshToken body = response.body();
        assertNotNull(body);
        assertEquals("my-user", body.getUsername());
        LOG.debug("Login Bearer Token: {} expires in {}", body.getAccessToken(), body.getExpiresIn());
        return body.getAccessToken();
    }

    private String replaceLineSeparators(String input) {
        return input.replaceAll("\n", "").replaceAll("\r", "");
    }



}
