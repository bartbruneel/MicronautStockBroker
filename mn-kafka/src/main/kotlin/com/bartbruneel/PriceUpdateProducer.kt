package com.bartbruneel

import io.micronaut.configuration.kafka.annotation.KafkaClient
import io.micronaut.configuration.kafka.annotation.Topic
import org.apache.kafka.clients.producer.RecordMetadata
import reactor.core.publisher.Flux

@KafkaClient(batch = true)
interface PriceUpdateProducer {

    @Topic("price_update")
    fun send(update: List<PriceUpdate>): Flux<RecordMetadata>

}