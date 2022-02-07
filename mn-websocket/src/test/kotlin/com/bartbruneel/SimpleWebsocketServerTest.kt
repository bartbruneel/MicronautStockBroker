package com.bartbruneel
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.websocket.WebSocketClient
import org.awaitility.Awaitility
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.kotlin.core.publisher.toMono

@MicronautTest
class SimpleWebsocketServerTest(@Client("http://localhost:8180") val client: WebSocketClient ) {

    val LOG: Logger = LoggerFactory.getLogger(SimpleWebsocketServerTest::class.java)

    @Test
    fun test_canReceiveMessagesWithClient() {
        val webSocketClient: SimpleWebSocketClient = client
            .connect(SimpleWebSocketClient::class.java, "/ws/simple/prices")
            .toMono()
            .block()
        LOG.info("Client session ${webSocketClient.session}")
        webSocketClient.send("Hello")
        Awaitility.await().untilAsserted {
            val messages = webSocketClient.observedMessages;
            LOG.info("Observed messages ${webSocketClient.observedMessages.size} - $messages")
            Assertions.assertEquals("Connected!", messages.elementAt(0))
            Assertions.assertEquals("Not supported => (Hello)", messages.elementAt(1))
        }
    }

    @Test
    fun test_canReceiveMessagesWithClientAsync() {
        val webSocketClient: SimpleWebSocketClient = client
            .connect(SimpleWebSocketClient::class.java, "/ws/simple/prices")
            .toMono()
            .block()
        LOG.info("Client session ${webSocketClient.session}")
        webSocketClient.sendReactive("Hello").blockFirst()
        Awaitility.await().untilAsserted {
            val messages = webSocketClient.observedMessages;
            LOG.info("Observed messages ${webSocketClient.observedMessages.size} - $messages")
            Assertions.assertEquals("Connected!", messages.elementAt(0))
            Assertions.assertEquals("Not supported => (Hello)", messages.elementAt(1))
        }
    }

}
