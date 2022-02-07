package com.bartbruneel.entities

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.NamingStrategy
import io.micronaut.data.model.naming.NamingStrategies
import java.math.BigDecimal

@MappedEntity(value = "transactions")
class Transaction(@field:Id @GeneratedValue val transactionId: Long = 0,
                  val user: String,
                  val symbol: String,
                  val modification: BigDecimal) {

}