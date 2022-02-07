package com.bartbruneel.websockets

import io.micronaut.scheduling.annotation.Scheduled
import io.micronaut.websocket.WebSocketBroadcaster
import jakarta.inject.Singleton
import java.math.BigDecimal
import java.util.concurrent.ThreadLocalRandom

@Singleton
class PricePush(val broadcaster: WebSocketBroadcaster) {

    @Scheduled(fixedDelay = "5s")
    fun push() {
        broadcaster.broadcastAsync(
            PriceUpdate("AMZN", randomValue())
        )
    }

    private fun randomValue(): BigDecimal {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1500.0, 2000.0))
    }

}