package com.bartbruneel.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Symbol", description = "Abbreviation of identification of publicly available stocks")
public record Symbol(@Schema(description = "symbol value", minLength = 1, maxLength = 5) String value) {}
