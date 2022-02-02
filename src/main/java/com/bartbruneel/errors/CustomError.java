package com.bartbruneel.errors;

import com.bartbruneel.models.RestApiResponse;
import lombok.Builder;

public record CustomError(
        int status,
        String error,
        String message
) implements RestApiResponse {
    @Builder
    public CustomError {}
}
