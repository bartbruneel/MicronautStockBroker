package com.bartbruneel

import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.LocalTime

@Controller("/time")
class RateLimitedTimeEndpoint(private val redis: StatefulRedisConnection<String, String>) {

    companion object {
        val QUOTA_PER_MINUTE: Int = 10
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @ExecuteOn(TaskExecutors.IO)
    @Get("/")
    fun time(): String {
        val key = "EXAMPLE::TIME"
        return checkRateLimitOnKey(key, LocalTime.now())
    }

    @ExecuteOn(TaskExecutors.IO)
    @Get("/utc")
    fun utcTime(): String {
        val key = "EXAMPLE::UTC::TIME"
        return checkRateLimitOnKey(key, LocalTime.now(Clock.systemUTC()))
    }

    private fun checkRateLimitOnKey(key: String, now: LocalTime): String {
        val value: String? = redis.sync().get(key)
        val currentQuota: Int = value?.toInt() ?: 0
        if (currentQuota >= QUOTA_PER_MINUTE) {
            val err = String.format("Rate limit reached %s %s/%s", key, currentQuota, QUOTA_PER_MINUTE)
            LOG.info(err)
            return err
        }
        LOG.info("Current quota {} in {}/{}", key, currentQuota, QUOTA_PER_MINUTE)
        increaseQuote(key, now)
        return now.toString()
    }

    private fun increaseQuote(key: String, now: LocalTime) {
        val commands: RedisCommands<String, String> = redis.sync()
        commands.multi()
        commands.incrby(key, 1)
        val remainingSeconds = (60 - now.second).toLong()
        commands.expire(key, remainingSeconds)
        commands.exec()
    }

}