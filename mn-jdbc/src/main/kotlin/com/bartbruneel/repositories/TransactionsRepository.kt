package com.bartbruneel.repositories

import com.bartbruneel.entities.Transaction
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository

@Repository
@JdbcRepository(dialect = Dialect.MYSQL)
interface TransactionsRepository: CrudRepository<Transaction, Long> {

    override fun findAll(): MutableList<Transaction>
}