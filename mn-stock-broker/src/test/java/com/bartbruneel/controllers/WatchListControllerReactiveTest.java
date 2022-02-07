package com.bartbruneel.controllers;

import com.bartbruneel.data.InMemoryAccountStore;
import com.bartbruneel.models.Symbol;
import com.bartbruneel.models.WatchList;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bartbruneel.data.InMemoryAccountStore.ACCOUNT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class WatchListControllerReactiveTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID TEST_ACCOUNT_ID = ACCOUNT_ID;
    public static final String BEARER = "Bearer ";

    @Inject
    @Client("/")
    JwtWatchlistClient client;

    @Inject
    InMemoryAccountStore store;

    @BeforeEach
    void setUp() {
        store.deleteWatchList(TEST_ACCOUNT_ID);
    }

    @Test
    void test_returnsEmptyWatchList() {
        BearerAccessRefreshToken login = loginUser();
        HttpResponse<WatchList> result = client.retrieveWatchList(getAuthorization(login));
        assertNull(result.body().symbols());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
    }

    private BearerAccessRefreshToken loginUser() {
        return client.login(new UsernamePasswordCredentials("my-user@bart.com", "secret"));
    }

    @Test
    void test_returnsWatchListForTestAccountFlux() {
        addWatchListForTestAccount();
        BearerAccessRefreshToken bearerAccessRefreshToken = loginUser();
        var response = client.retrieveWatchListFluxEndpoint(getAuthorization(bearerAccessRefreshToken));
        assertEquals("[AAPL,GOOGL,MFST]", response.getBody().get());
    }

    @Test
    void test_returnsWatchListForTestAccount() {
        addWatchListForTestAccount();
        var response = client.retrieveWatchList(getAuthorization(loginUser()));
        assertEquals(replaceLineSeparators("""
                        WatchList[symbols=[Symbol[value=AAPL], Symbol[value=GOOGL], Symbol[value=MFST]]]
                        """),
                replaceLineSeparators(response.body().toString()));
    }

    @Test
    void test_canUpdateWatchListForAccount() {
        final WatchList watchList = getWatchList();
        HttpResponse<Object> response = client.updateWatchList(getAuthorization(loginUser()), watchList);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(watchList, store.getWatchList(TEST_ACCOUNT_ID));
    }

    @Test
    void test_canDeleteWatchListForAccount() {
        final WatchList watchList = getWatchList();
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);
        assertFalse(store.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
        HttpResponse<Object> response = client.deleteWatchList(getAuthorization(loginUser()), ACCOUNT_ID);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
    }

    private String getAuthorization(BearerAccessRefreshToken bearerAccessRefreshToken) {
        return BEARER + bearerAccessRefreshToken.getAccessToken();
    }

    private WatchList getWatchList() {
        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        return new WatchList(symbols);
    }

    private void addWatchListForTestAccount() {
        store.updateWatchList(TEST_ACCOUNT_ID, new WatchList(
                Stream.of("AAPL", "GOOGL", "MFST")
                        .map(Symbol::new)
                        .toList()
        ));
    }

    private String replaceLineSeparators(String input) {
        return input.replaceAll("\n", "").replaceAll("\r", "");
    }



}
