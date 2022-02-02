package com.bartbruneel.authentication;

import com.bartbruneel.entities.SymbolEntity;
import com.bartbruneel.entities.UserEntity;
import com.bartbruneel.repositories.SymbolsRepository;
import com.bartbruneel.repositories.UserRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

@Singleton
@Requires(notEnv = Environment.TEST)
public class TestDataProvider {

    private static final Logger LOG = LoggerFactory.getLogger(TestDataProvider.class);

    private final UserRepository userRepository;
    private final SymbolsRepository symbolsRepository;

    public TestDataProvider(UserRepository userRepository, SymbolsRepository symbolsRepository) {
        this.userRepository = userRepository;
        this.symbolsRepository = symbolsRepository;
    }

    @EventListener
    public void init(StartupEvent event) {
        initUsers();
        initSymbols();
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

    private void saveUser(String email) {
        final UserEntity entity = new UserEntity();
        entity.setEmail(email);
        entity.setPassword("secret");
        userRepository.save(entity);
    }
}
