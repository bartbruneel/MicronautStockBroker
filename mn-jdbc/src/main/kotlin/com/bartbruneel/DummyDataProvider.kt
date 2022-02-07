package com.bartbruneel

import com.bartbruneel.entities.Transaction
import com.bartbruneel.repositories.TransactionsRepository
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ThreadLocalRandom

@Singleton
class DummyDataProvider(private val repository: TransactionsRepository) {

    private val LOG: Logger = LoggerFactory.getLogger(this.javaClass)

    @Scheduled(fixedDelay = "1s")
    fun generate() {
        val transaction = Transaction(
            user = UUID.randomUUID().toString(),
            symbol = "SYMBOL",
            modification = randomValue()
        )
        LOG.info("Content {}", repository.findAll().size)
        repository.save(transaction)
    }

    private fun randomValue(): BigDecimal = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1.0, 100.0))
}