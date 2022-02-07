package com.bartbruneel.controllers;

import com.bartbruneel.data.InMemoryStore;
import com.bartbruneel.entities.SymbolEntity;
import com.bartbruneel.models.Symbol;
import com.bartbruneel.repositories.SymbolsRepository;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/symbols")
public class SymbolsController {

    public static final int DEFAULT_MAX = 10;
    public static final int DEFAULT_OFFSET = 0;
    private final InMemoryStore inMemoryStore;
    private final SymbolsRepository symbolsRepository;

    public SymbolsController(InMemoryStore inMemoryStore, SymbolsRepository symbolsRepository) {
        this.inMemoryStore = inMemoryStore;
        this.symbolsRepository = symbolsRepository;
    }


    @Operation(summary = "Returns all available symbols from database using JPA")
    @ApiResponse(
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Tag(name = "symbols")
    @Get("/jpa")
    public List<SymbolEntity> getAllJpa() {
        return symbolsRepository.findAll();
    }


    @Operation(summary = "Returns all available symbols")
    @ApiResponse(
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @Tag(name = "symbols")
    @Get
    public List<Symbol> getAll() {
        return inMemoryStore.getSymbols().values().stream().toList();
    }

    @Get("{value}")
    public Symbol getSymbolByValue(@PathVariable String value) {
        return inMemoryStore.getSymbols().get(value);
    }

    @Get("/filter{?max,offset}")
    public List<Symbol> getSymbols(@QueryValue Optional<Integer> max, @QueryValue Optional<Integer> offset) {
        return inMemoryStore.getSymbols().values()
                .stream()
                .skip(offset.orElse(DEFAULT_OFFSET))
                .limit(max.orElse(DEFAULT_MAX))
                .toList();
    }


}
