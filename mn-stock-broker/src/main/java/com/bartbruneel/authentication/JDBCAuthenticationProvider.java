package com.bartbruneel.authentication;

import com.bartbruneel.entities.UserEntity;
import com.bartbruneel.repositories.UserRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Singleton
public class JDBCAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JDBCAuthenticationProvider.class);
    private final UserRepository userRepository;

    public JDBCAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Flux.create(emitter -> {
            final String identity = (String) authenticationRequest.getIdentity();
            LOG.debug("User {} tries to login...", identity);
            Optional<UserEntity> user = userRepository.findByEmail(identity);
            if(user.isPresent()) {
                LOG.debug("Found user: {}", user.get().getEmail());
                final String secret = (String) authenticationRequest.getSecret();
                if(user.get().getPassword().equals(secret)) {
                    LOG.debug("user looged in");
                    Map<String, Object> attributes = Map.of("hair_color", "brown", "language", "en");
                    emitter.next(AuthenticationResponse.success((String) identity, List.of("ROLE_USER"), attributes));
                } else {
                    LOG.debug("wrong password for user");
                }
            } else {
                LOG.debug("No user found with email: {}", identity);
                emitter.next(AuthenticationResponse.failure("Wrong username or password"));
            }
            emitter.complete();
        }, FluxSink.OverflowStrategy.ERROR);
    }
}
