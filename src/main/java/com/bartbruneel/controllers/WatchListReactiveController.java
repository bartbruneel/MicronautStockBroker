package com.bartbruneel.controllers;

import com.bartbruneel.data.InMemoryAccountStore;
import com.bartbruneel.models.Symbol;
import com.bartbruneel.models.WatchList;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static com.bartbruneel.data.InMemoryAccountStore.ACCOUNT_ID;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/account/watchlist-reactive")
public class WatchListReactiveController {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListReactiveController.class);
    private final InMemoryAccountStore store;
    private final Scheduler scheduler;

    public WatchListReactiveController(@Named(TaskExecutors.IO) ExecutorService executorService,
                                       InMemoryAccountStore store) {
        this.store = store;
        this.scheduler = Schedulers.fromExecutor(executorService);
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    @ExecuteOn(TaskExecutors.IO)
    public Mono<WatchList> get() {
        LOG.debug("getWatchList - {}", Thread.currentThread().getName());
        return Mono.fromCallable(() -> store.getWatchList(ACCOUNT_ID));
    };

    @Get(
            value="/flux",
            produces = MediaType.APPLICATION_JSON)
    public Flux<String> getFlux() {
        LOG.debug("getMonoWatchList - {}", Thread.currentThread().getName());
        return Mono
                .fromCallable(() -> store.getWatchList(ACCOUNT_ID))
                .subscribeOn(scheduler)
                .map(WatchList::symbols)
                .flatMapMany(Flux::fromIterable)
                .map(Symbol::value);
    };

    @Put(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public Mono<WatchList> update(@Body WatchList watchList) {
        return Mono.fromCallable(() -> store.updateWatchList(ACCOUNT_ID, watchList));
    }

    @Delete(value = "/{uuid}",
            produces = MediaType.APPLICATION_JSON

    )
    public Mono<MutableHttpResponse<Object>> delete(final UUID uuid) {
        store.deleteWatchListReactive(uuid);
        return Mono.fromCallable(HttpResponse::noContent);
    }


}