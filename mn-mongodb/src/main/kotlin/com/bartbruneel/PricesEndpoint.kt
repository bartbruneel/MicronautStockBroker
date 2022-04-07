package com.bartbruneel

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mongodb.client.result.InsertOneResult
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoCollection
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux

@Controller("/prices")
class PricesEndpoint(private val mongoClient: MongoClient) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Get("/")
    fun fetch(): Flux<Document> {
        val collection = getCollection()
        return collection.find().toFlux()
    }

    private fun getCollection(): MongoCollection<Document> {
        return mongoClient.getDatabase("prices").getCollection("example")
    }

    @Post("/")
    fun insert(@Body json: ObjectNode): Flux<InsertOneResult> {
        val collection = getCollection()
        val doc = Document.parse(json.toString())
        LOG.info("Insert {}", doc)
        return collection.insertOne(doc).toFlux()
    }
}
