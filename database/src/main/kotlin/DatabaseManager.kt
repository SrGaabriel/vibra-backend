package io.github.vibraplatform.database

import com.zaxxer.hikari.HikariDataSource
import io.github.vibraplatform.database.data.RelationalDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.neo4j.ogm.session.SessionFactory

class DatabaseManager(
    private val postgreDatabaseConnection: PostgreDatabaseConnection,
    private val graphDatabaseConnection: GraphDatabaseConnection
) {
    fun connect() {
        val hikariDataSource = HikariDataSource().also { datasource ->
            with(postgreDatabaseConnection) {
                datasource.jdbcUrl = "jdbc:postgresql://${host}:${port}/${database}?useTimezone=true&serverTimezone=UTC"
                datasource.username = username
                datasource.password = password
            }
        }
        Database.connect(hikariDataSource)
    }
    
    fun createTables() = transaction {
        SchemaUtils.createMissingTablesAndColumns(

        )
    }

    fun createRelationalDataSource(): RelationalDataSource {
        val configuration = org.neo4j.ogm.config.Configuration.Builder()
            .uri(graphDatabaseConnection.uri)
            .credentials(graphDatabaseConnection.username, graphDatabaseConnection.password)
            .build()
        val sessionFactory = SessionFactory(configuration, "io.github.vibraplatform.database.dao")
        return RelationalDataSource { sessionFactory.openSession() }
    }
}