package com.bartbruneel

import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.OffsetReset
import io.micronaut.configuration.kafka.annotation.Topic
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.context.env.Environment
import jakarta.inject.Singleton
import org.junit.Rule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.math.BigDecimal
import java.util.concurrent.ThreadLocalRandom



@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestContainerQuoteProducer {

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
    fun test_producingRecordsWorks() {
        val producer = context.getBean(ExternalQuoteProducer::class.java)
        producer.send("TEST", ExternalQuote("TEST",
            randomValue()
            , randomValue()))
    }

    private fun randomValue(): BigDecimal = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0.0, 1000.0))


}

@Singleton
@Requires(env = arrayOf(Environment.TEST))
class ExternalQuoteObserver {

    val LOG = LoggerFactory.getLogger(this.javaClass)

    val inspected = mutableListOf<ExternalQuote>()

    @KafkaListener(
        offsetReset = OffsetReset.EARLIEST
    )
    @Topic("external-quotes")
    fun receive(externalQuote: ExternalQuote) {
        LOG.debug("Consumed {}", externalQuote)
        inspected.add(externalQuote)
    }

}
