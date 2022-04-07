package com.example

import com.github.javafaker.Faker
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.xcontent.XContentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

@Singleton
class TestDataProvider(private val client: RestHighLevelClient) {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(this::class.java)
        private val FAKER: Faker = Faker()
    }

    @Scheduled(fixedDelay = "10s")
    fun insertDocument() {
        val document = HashMap<String, String>()
        document[Constants.FIRST_NAME] = FAKER.name().firstName()
        document[Constants.LAST_NAME] = FAKER.name().lastName()
        val listener = object : ActionListener<IndexResponse> {
            override fun onResponse(response: IndexResponse?) {
                LOG.debug("Added document {} with id {}", document, response?.id)
            }

            override fun onFailure(e: Exception?) {
                LOG.error("Failed to insert document: {}", e)
            }

        }
        val indexRequest = IndexRequest()
            .index(Constants.INDEX)
            .id(UUID.randomUUID().toString())
            .source(document, XContentType.JSON)
        client.indexAsync(
            indexRequest,
            RequestOptions.DEFAULT,
            listener
        )
    }


}