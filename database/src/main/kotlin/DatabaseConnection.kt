package io.github.vibraplatform.database

data class PostgreDatabaseConnection(
    val host: String,
    val port: String,
    val database: String,
    val username: String,
    val password: String
)

data class GraphDatabaseConnection(
    val uri: String,
    val username: String,
    val password: String
)