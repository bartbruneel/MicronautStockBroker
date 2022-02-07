package com.bartbruneel.controllers;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/secured")
public class SecuredEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(SecuredEndpoint.class);

    @Get("/status")
    public List<String> status(Principal principal) {
        Authentication details = (Authentication) principal;
        LOG.debug("User details: {}", details.getAttributes());
        String hairColor = (String) details.getAttributes().get("hair_color");
        String language = (String)  details.getAttributes().get("language");
        return List.of(details.getName(),
                 hairColor,
                language);
    }
}
