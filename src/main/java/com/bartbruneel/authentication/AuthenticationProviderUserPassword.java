package com.bartbruneel.authentication;

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

import java.util.Collections;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

    private final static Logger LOG = LoggerFactory.getLogger(AuthenticationProviderUserPassword.class);

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Flux.create(emitter -> {
            final Object identity = authenticationRequest.getIdentity();
            final Object secret = authenticationRequest.getSecret();
            LOG.debug("User {} tries to login...", identity);
            if(identity.equals("my-user") && secret.equals("secret")) {
                emitter.next(AuthenticationResponse.success((String) identity, Collections.emptyList()));
            } else {
                emitter.next(AuthenticationResponse.failure());
            }
            emitter.complete();
        }, FluxSink.OverflowStrategy.ERROR);
    }

}
