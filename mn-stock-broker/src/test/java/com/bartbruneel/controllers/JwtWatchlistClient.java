package com.bartbruneel.controllers;

import com.bartbruneel.models.WatchList;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;

import java.util.UUID;

@Client("/")
public interface JwtWatchlistClient {

    @Post("/login")
    BearerAccessRefreshToken login(@Body UsernamePasswordCredentials credentials);

    @Get("/account/watchlist-reactive")
    HttpResponse<WatchList> retrieveWatchList(@Header(name = "Authorization") String authorization);

    @Get("/account/watchlist-reactive/flux")
    HttpResponse<String> retrieveWatchListFluxEndpoint(@Header String authorization);

    @Put("/account/watchlist-reactive")
    HttpResponse<Object> updateWatchList(@Header String authorization, @Body WatchList watchList);

    @Delete("/account/watchlist-reactive/{accountId}")
    HttpResponse<Object> deleteWatchList(@Header String authorization, @PathVariable final UUID accountId);


}
