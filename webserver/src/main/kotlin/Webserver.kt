package io.github.vibraplatform.webserver

import io.github.vibraplatform.common.snowflake.SnowflakeService
import io.github.vibraplatform.database.DatabaseManager
import io.github.vibraplatform.database.GraphDatabaseConnection
import io.github.vibraplatform.database.PostgreDatabaseConnection
import io.github.vibraplatform.database.service.PostService
import io.github.vibraplatform.database.service.UserService
import io.github.vibraplatform.webserver.auth.AuthService
import io.github.vibraplatform.webserver.route.followRoute
import io.github.vibraplatform.webserver.route.postRoute
import io.github.vibraplatform.webserver.route.userGadgetsRoute
import io.github.vibraplatform.webserver.route.userRoute
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.koin.ktor.plugin.Koin
import org.slf4j.event.Level

fun main() {
    embeddedServer(CIO, applicationEngineEnvironment {
        connector {
            port = 3001
        }
        module {
            module()
        }
        developmentMode = false
        watchPaths = listOf("classes/kotlin/main")
    }).start(wait = true)
}

fun Application.module() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }
    
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    
    install(ContentNegotiation) {
        json()
    }
    
    install(Resources)
    val authService = AuthService("JWT_SECRET_MOCK", "BCRYPT_SALT_MOCK")
    val databaseManager = DatabaseManager(
        PostgreDatabaseConnection(
            host = "127.0.0.1",
            port = "5432",
            database = "vibra",
            username = "postgres",
            password = "underarm turbofan tilt buffer throwback jolly impotence john"
        ),
        GraphDatabaseConnection(
            uri = "bolt://localhost:7687",
            username = "neo4j",
            password = "password"
        )
    )
    databaseManager.connect()
    databaseManager.createTables()

    val relationalDataSource = databaseManager.createRelationalDataSource()

    install(Koin) {
        modules(org.koin.dsl.module {
            single { authService }
            single { databaseManager }
            single { UserService(relationalDataSource) }
            single { PostService(relationalDataSource) }
            single { SnowflakeService(1) }
        })
    }
    
    routing {
        route("/api/v1/") {
            userRoute()
            followRoute()
            postRoute()
            userGadgetsRoute()
        }
    }
}