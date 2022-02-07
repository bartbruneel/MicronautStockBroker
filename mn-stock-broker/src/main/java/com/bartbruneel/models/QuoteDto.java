package com.bartbruneel.models;

import io.micronaut.core.annotation.Introspected;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Introspected
@Getter
@Setter
public class QuoteDto {

    private Integer id;
    private BigDecimal volume;


}
