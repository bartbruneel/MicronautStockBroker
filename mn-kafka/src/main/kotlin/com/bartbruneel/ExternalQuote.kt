package com.bartbruneel

import java.math.BigDecimal

data class ExternalQuote(val symbol: String,
                    val lastPrice: BigDecimal,
                    val volume: BigDecimal) {

}
