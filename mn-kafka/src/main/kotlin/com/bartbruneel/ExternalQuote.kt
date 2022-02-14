package com.bartbruneel

import io.micronaut.core.annotation.Introspected
import java.math.BigDecimal

@Introspected
data class ExternalQuote(val symbol: String,
                    val lastPrice: BigDecimal,
                    val volume: BigDecimal) {

}
