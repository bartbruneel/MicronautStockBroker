package com.bartbruneel

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.concurrent.ThreadLocalRandom

@Singleton
class EventScheduler(val producer: ExternalQuoteProducer) {

    val SYMBOLS = listOf("AAPL", "AMZN", "FB", "GOOG", "MFST", "NFLX")
    val LOG = LoggerFactory.getLogger(this.javaClass)

    @Scheduled(fixedDelay = "10s")
    fun generate() {
        val random = ThreadLocalRandom.current()
        val quote = ExternalQuote(
            SYMBOLS.get(random.nextInt(0, SYMBOLS.size - 1)),
            randomValue(random),
            randomValue(random)
        )
        LOG.debug("Generate external quote {}", quote)
        producer.send(quote.symbol, quote)
    }

    fun randomValue(random: ThreadLocalRandom): BigDecimal = BigDecimal.valueOf(random.nextDouble(0.0, 1000.0))
}