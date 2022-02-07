package com.bartbruneel.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class Greeting {

    public final String myText = "Hello World";
    public final BigDecimal id = BigDecimal.valueOf(123456789);
    public final Instant timeUTC = Instant.now();

}
