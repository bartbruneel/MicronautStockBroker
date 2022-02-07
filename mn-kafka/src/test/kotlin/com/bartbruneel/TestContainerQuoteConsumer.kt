package com.bartbruneel

import io.micronaut.configuration.kafka.annotation.*
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment
import jakarta.inject.Singleton
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.org.awaitility.Awaitility
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal
import java.util.concurrent.ThreadLocalRandom



@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestContainerQuoteConsumer {

    val LOG = LoggerFactory.getLogger(this.javaClass)

    @Rule
    val kafka: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))

    lateinit var context: ApplicationContext

    @BeforeAll
    fun startKafka() {
        kafka.start()
        LOG.debug("Bootstrap Servers: {}", kafka.bootstrapServers)

        val map = mapOf("kafka.bootstrap.servers" to kafka.bootstrapServers)
        context =
            ApplicationContext.builder()
                .environments(Environment.TEST)
                .properties(map)
                .start()
    }

    @AfterAll
    fun stopKafka() {
        kafka.stop()
        context.close()
    }

    @Test
    fun test_consumingPriceUpdatesWorks() {
        val observer = context.getBean(PriceUpdateObserver::class.java)
        val testProducer = context.getBean(TestScopedExternalQuoteProducer::class.java)
        repeat(4) {
            testProducer.send(ExternalQuote(
                "TEST $it",
            randomValue(),
            randomValue()))
        }
        Awaitility.await().untilAsserted {
            assertEquals(4, observer.inspected.size)
        }

    }

    private fun randomValue(): BigDecimal = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0.0, 1000.0))


}

@Singleton
@Requires(env = [Environment.TEST])
class PriceUpdateObserver {

    val LOG = LoggerFactory.getLogger(this.javaClass)

    val inspected = mutableListOf<PriceUpdate>()

    @KafkaListener(
        offsetReset = OffsetReset.EARLIEST
    )
    @Topic("price_update")
    fun receive(priceUpdates: List<PriceUpdate>) {
        LOG.debug("Consumed {}", priceUpdates)
        inspected.addAll(priceUpdates)
    }

}

@KafkaClient
@Requires(env = [Environment.TEST])
interface TestScopedExternalQuoteProducer {

    @Topic("external-quotes")
    fun send(externalQuote: ExternalQuote)
}
