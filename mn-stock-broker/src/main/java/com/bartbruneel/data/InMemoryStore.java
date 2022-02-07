package com.bartbruneel.data;

import com.bartbruneel.models.Quote;
import com.bartbruneel.models.Symbol;
import com.github.javafaker.Faker;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Singleton
public class InMemoryStore {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryStore.class);
    private final Map<String, Symbol> symbols = new HashMap<>();
    private final Map<String, Quote> cachedQuotes = new HashMap<>();
    private final Faker faker = new Faker();

    private static final ThreadLocalRandom localRandom =
            ThreadLocalRandom.current();

    @PostConstruct
    public void initialize() {
        initializeWith(10);
    }

    public void initializeWith(int numberOfEntries) {
        symbols.clear();
        IntStream.range(0, numberOfEntries).forEach(i ->
                addNewSymbol());
        symbols.values().forEach(symbol -> cachedQuotes.put(symbol.value(), initRandomQuote(symbol)));
    }

    private void addNewSymbol() {
        var symbol = new Symbol(faker.stock().nsdqSymbol());
        symbols.put(symbol.value(), symbol);
        LOG.debug("added symbol " + symbol);
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public Optional<Quote> fetchQuote(String symbol) {
        return Optional.ofNullable(cachedQuotes.get(symbol));
    }

    public Quote initRandomQuote(final Symbol symbol) {
        return Quote
                .builder()
                .symbol(symbol)
                .bid(randomValue())
                .ask(randomValue())
                .lastPrice(randomValue())
                .volume(randomValue())
                .build();
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(localRandom.nextDouble(1, 100));
    }
}
