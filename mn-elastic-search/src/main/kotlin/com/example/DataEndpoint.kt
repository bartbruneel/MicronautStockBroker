package com.example

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.get.GetRequest
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.asyncsearch.AsyncSearchResponse
import org.elasticsearch.client.asyncsearch.SubmitAsyncSearchRequest
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

@Controller("/data")
class DataEndpoint(private val client: RestHighLevelClient) {

    companion object {
        private val LOG = LoggerFactory.getLogger(DataEndpoint.javaClass)

    }

    @ExecuteOn(TaskExecutors.IO)
    @Get("/document/sync/{id}")
    fun byId(@PathVariable("id") documentId: String): String {
        val response = client.get(GetRequest(Constants.INDEX, documentId), RequestOptions.DEFAULT)
        val source = response.sourceAsString
        LOG.debug("Response /document/sync/{} => {}", documentId, source)
        return source
    }

    @Get("/document/async/{id}")
    fun byIdAsync(@PathVariable("id") documentId: String): CompletableFuture<String> {
        val whenDone: CompletableFuture<String> = CompletableFuture()
        val listener = object :  ActionListener<GetResponse> {
            override fun onFailure(e: Exception?) {
                whenDone.completeExceptionally(e)
            }

            override fun onResponse(response: GetResponse?) {
                val source = response?.sourceAsString
                LOG.debug("Response /document/async/{} => {}", documentId, source)
                whenDone.complete(source)
            }

        }
        client.getAsync(GetRequest(Constants.INDEX, documentId), RequestOptions.DEFAULT, listener)
        return whenDone
    }

    @Get("/document/async/firstname/{search}")
    fun byFirstNameAsync(@PathVariable("search") search: String): CompletableFuture<String> {
        val whenDone: CompletableFuture<String> = CompletableFuture()
        val listener = object :  ActionListener<AsyncSearchResponse> {
            override fun onFailure(e: Exception?) {
                whenDone.completeExceptionally(e)
            }

            override fun onResponse(response: AsyncSearchResponse?) {
                val hits = response?.searchResponse?.hits?.hits
                val response = hits?.asSequence()?.map(SearchHit::getSourceAsString)?.toList()
                LOG.debug("Response: /document/async/firstname/{} => {}", search, response)
                whenDone.complete(response.toString())
            }


        }
        val searchSourceBuilder = SearchSourceBuilder()
            .query(QueryBuilders.matchQuery(Constants.FIRST_NAME, search))
        client.asyncSearch().submitAsync(SubmitAsyncSearchRequest(searchSourceBuilder, Constants.INDEX),
        RequestOptions.DEFAULT,
        listener)
        return whenDone
    }
}