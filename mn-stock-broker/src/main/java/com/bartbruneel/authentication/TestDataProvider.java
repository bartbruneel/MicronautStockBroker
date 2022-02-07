package com.bartbruneel.authentication;

import com.bartbruneel.entities.QuoteEntity;
import com.bartbruneel.entities.SymbolEntity;
import com.bartbruneel.entities.UserEntity;
import com.bartbruneel.repositories.QuotesRepository;
import com.bartbruneel.repositories.SymbolsRepository;
import com.bartbruneel.repositories.UserRepository;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@Singleton
//@Requires(notEnv = Environment.TEST)
public class TestDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TestDataProvider.class);
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private final UserRepository userRepository;
    private final SymbolsRepository symbolsRepository;
    private final QuotesRepository quotesRepository;

    public TestDataProvider(UserRepository userRepository, SymbolsRepository symbolsRepository, QuotesRepository quotesRepository) {
        this.userRepository = userRepository;
        this.symbolsRepository = symbolsRepository;
        this.quotesRepository = quotesRepository;
    }

    @EventListener
    public void init(StartupEvent event) {
        initUsers();
        initSymbols();
        initQuotes();
    }

    private void initQuotes() {
        if(quotesRepository.findAll().isEmpty()) {
            symbolsRepository.findAll().forEach(symbol -> {
                var quote =new QuoteEntity();
                quote.setSymbol(symbol);
                quote.setAsk(randomValue());
                quote.setBid(randomValue());
                quote.setLastPrice(randomValue());
                quote.setVolume(randomValue());
                quotesRepository.save(quote);
            });
        }
    }

    private void initSymbols() {
        if(symbolsRepository.findAll().isEmpty()) {
            LOG.info("Adding test data as empty database was found!");
            Stream.of("AAPL", "AMZN", "GOOGL", "TSLA")
                    .map(SymbolEntity::new)
                    .forEach(symbolsRepository::save);
        }
    }

    private void initUsers() {
        final String email = "bart@bart.com";
        final String emailMyUser = "my-user@bart.com";
        if(userRepository.findByEmail(email).isEmpty()) {
            saveUser(email);
        }
        if(userRepository.findByEmail(emailMyUser).isEmpty()) {
            saveUser(emailMyUser);
        }
    }

    private BigDecimal randomValue() {
        return BigDecimal.valueOf(RANDOM.nextDouble(1, 100));
    }

    private void saveUser(String email) {
        final UserEntity entity = new UserEntity();
        entity.setEmail(email);
        entity.setPassword("secret");
        userRepository.save(entity);
    }
}
