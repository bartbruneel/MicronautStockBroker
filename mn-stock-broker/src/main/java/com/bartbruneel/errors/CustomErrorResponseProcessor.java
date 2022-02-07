package com.bartbruneel.errors;

import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.server.exceptions.response.ErrorContext;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Singleton;

@Singleton
public class CustomErrorResponseProcessor implements ErrorResponseProcessor<CustomError> {


    @Override
    public MutableHttpResponse<CustomError> processResponse(ErrorContext errorContext,
                                                            MutableHttpResponse<?> response) {
        CustomError customError;
        if(!errorContext.hasErrors()) {
            customError = new CustomError(
                    response.getStatus().getCode(),
                    response.getStatus().name(),
                    "No custom errors found..."
            );
        } else {
            var firstError = errorContext.getErrors().get(0);
            customError = new CustomError(
                    response.getStatus().getCode(),
                    response.getStatus().name(),
                    firstError.getMessage()
            );
        }
        return response.body(customError).contentType(MediaType.APPLICATION_JSON);
    }
}
