package com.bartbruneel.errors;

public record CustomError(
        int status,
        String error,
        String message
) {
}
