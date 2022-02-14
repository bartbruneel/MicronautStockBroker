package com.bartbruneel

import io.micronaut.core.annotation.Introspected
import java.math.BigDecimal

@Introspected
data class PriceUpdate(val symbol: String, val lastPrice: BigDecimal) {

}
