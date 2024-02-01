package io.github.vibraplatform.cdn.server

import io.github.vibraplatform.cdn.server.image.ImageCompressor
import io.github.vibraplatform.cdn.server.image.StandardImageCompressor
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
            port = 3002
        }
        module {
            module()
        }
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
    val cdnKey = "CDN_KEY"

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        json()
    }
    install(Resources)


    install(Koin) {
        modules(org.koin.dsl.module {
            single<ImageCompressor> { StandardImageCompressor() }
        })
    }

    routing {
        route("/media") {

        }
    }
}