package com.bartbruneel.controllers;

import com.bartbruneel.data.InMemoryStore;
import com.bartbruneel.entities.QuoteEntity;
import com.bartbruneel.entities.SymbolEntity;
import com.bartbruneel.errors.CustomError;
import com.bartbruneel.models.Quote;
import com.bartbruneel.models.QuoteDto;
import com.bartbruneel.repositories.QuotesRepository;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/quotes")
public class QuotesController {

    public static final int PAGE_SIZE = 5;
    private final InMemoryStore store;
    private final QuotesRepository quotesRepository;

    public QuotesController(InMemoryStore store,
                            QuotesRepository quotesRepository) {
        this.store = store;
        this.quotesRepository = quotesRepository;
    }

    @Get("/jpa")
    public List<QuoteEntity> getAllQuotesViaJPA() {
        return quotesRepository.findAll();
    }

    @Get("/{symbol}")
    public HttpResponse getQuotes(@PathVariable String symbol) {
        final Optional<Quote> quoteOptional =
                store.fetchQuote(symbol);
        return getResponseQuote(quoteOptional);
    }

    @Get("/{symbol}/jpa")
    public HttpResponse getQuotesJpa(@PathVariable String symbol) {
        final Optional<QuoteEntity> quoteOptional =
                quotesRepository.findBySymbol(new SymbolEntity(symbol));
        return getResponseQuote(quoteOptional);
    }

    @Get("jpa/ordered/desc")
    public List<QuoteDto> orderedDesc() {
        return quotesRepository.listOrderByVolumeDesc();
    }

    @Get("jpa/ordered/asc")
    public List<QuoteDto> orderedAsc() {
        return quotesRepository.listOrderByVolumeAsc();
    }

    @Get("/jpa/volume/{volume}")
    public List<QuoteDto> volumeFilter(@PathVariable BigDecimal volume) {
        return quotesRepository.findByVolumeGreaterThanOrderByVolumeAsc(volume, Pageable.from(0, Integer.MAX_VALUE));
    }

    @Get("/jpa/pagination{?page,volume}")
    public List<QuoteDto> volumeFilterPagination(@QueryValue Optional<Integer> page, Optional<BigDecimal> volume) {
        var myPage = page.isEmpty()? 0: page.get();
        final BigDecimal myVolume = volume.isEmpty() ? BigDecimal.ZERO : volume.get();
        return quotesRepository.findByVolumeGreaterThanOrderByVolumeAsc(myVolume, Pageable.from(myPage, PAGE_SIZE));
    }

    @Get("/jpa/pagination/{page}")
    public List<QuoteDto> allWithPagination(@PathVariable int page) {
        return quotesRepository.list(Pageable.from(page, 5)).getContent();
    }

    private MutableHttpResponse<?> getResponseQuote(Optional<?> quoteOptional) {
        if(quoteOptional.isEmpty()) {
            final CustomError notFound = CustomError.builder()
                    .status(HttpStatus.NOT_FOUND.getCode())
                    .error(HttpStatus.NOT_FOUND.name())
                    .message("quote for symbol not available")
                    .build();
            return HttpResponse.notFound(notFound);
        } else {
            return HttpResponse.ok(quoteOptional.get());
        }
    }


}
