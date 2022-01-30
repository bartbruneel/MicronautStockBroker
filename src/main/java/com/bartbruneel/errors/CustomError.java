package com.bartbruneel.errors;

import com.bartbruneel.models.RestApiResponse;

public record CustomError(
        int status,
        String error,
        String message
) implements RestApiResponse {
}
