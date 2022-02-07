package com.bartbruneel.websockets

import java.math.BigDecimal

data class PriceUpdate(val symbol: String, val price: BigDecimal) {

}
