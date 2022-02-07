package com.bartbruneel.websockets

import io.micronaut.websocket.CloseReason
import io.micronaut.websocket.WebSocketSession
import io.micronaut.websocket.annotation.OnClose
import io.micronaut.websocket.annotation.OnMessage
import io.micronaut.websocket.annotation.OnOpen
import io.micronaut.websocket.annotation.ServerWebSocket
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux

@ServerWebSocket("/ws/simple/prices")
class SimpleWebSocketServer {

    val LOG: Logger = LoggerFactory.getLogger(this::class.java)

    @OnOpen
    fun onOpen(session: WebSocketSession): Publisher<String> {
        return session.send("Connected!")
    }

    @OnMessage
    fun onMessage(message: String, session: WebSocketSession): Publisher<String> {
        LOG.info("Received message: {} from sessions {}", message, session.id)
        if(message.contentEquals("disconnect me")) {
            LOG.info("Client close requested!");
            session.close(CloseReason.NORMAL)
            return Flux.empty()
        }
        return session.send("Not supported => ($message)")
    }

    @OnClose
    fun onClose(session: WebSocketSession) {
        LOG.info("Session closed ${session.id}")
    }
}