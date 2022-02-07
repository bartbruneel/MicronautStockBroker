package com.bartbruneel

import io.micronaut.configuration.kafka.annotation.KafkaListener
import io.micronaut.configuration.kafka.annotation.Topic
import org.slf4j.LoggerFactory

@KafkaListener(
    clientId = "mn-pricing-external-quote-consumer",
    groupId = "external-quote-consumer",
    batch = true
)
class ExternalQuoteConsumer(val producer: PriceUpdateProducer) {

    val LOG = LoggerFactory.getLogger(this.javaClass)

    @Topic("external-quotes")
    fun receive(externalQuotes: List<ExternalQuote>) {
        LOG.debug("Consuming batch of external quotes {}", externalQuotes)
        // Forward price updates
        val priceUpdates = externalQuotes.asSequence().map { quote ->
            PriceUpdate(quote.symbol, quote.lastPrice)
        }.toList()
        producer.send(priceUpdates)
            .doOnError{ e -> LOG.error("Failed to produce", e)}
            .doOnNext{ recordMetaData -> LOG.debug("Record sent to topic {} on offset {}", recordMetaData.topic(), recordMetaData.offset()) }
            .subscribe()
    }

}