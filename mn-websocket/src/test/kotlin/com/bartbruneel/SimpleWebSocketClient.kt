package com.bartbruneel

import io.micronaut.websocket.WebSocketSession
import io.micronaut.websocket.annotation.ClientWebSocket
import io.micronaut.websocket.annotation.OnMessage
import io.micronaut.websocket.annotation.OnOpen
import reactor.core.publisher.Flux

@ClientWebSocket("/ws/simple/prices")
abstract class SimpleWebSocketClient: AutoCloseable {

    lateinit var session: WebSocketSession;
    val observedMessages: MutableCollection<String> = mutableListOf()
        get() = field

    @OnOpen
    fun onOpen(session: WebSocketSession) {
        this.session = session;
    }

    abstract fun send(message: String);

    abstract fun sendReactive(message: String): Flux<String>

    @OnMessage
    fun onMessage(message: String) {
        observedMessages.add(message)
    }

}