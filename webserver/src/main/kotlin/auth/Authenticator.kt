package io.github.vibraplatform.webserver.auth

import io.github.vibraplatform.database.dao.User
import io.github.vibraplatform.database.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import org.koin.ktor.ext.inject

suspend inline fun PipelineContext<Unit, ApplicationCall>.authenticate(scope: (User) -> Unit) =
    authenticateCatching { call.respond(HttpStatusCode.Unauthorized); null }?.let(scope)

suspend inline fun PipelineContext<Unit, ApplicationCall>.authenticateOrNull(): User? =
    authenticateCatching { null }

suspend inline fun PipelineContext<Unit, ApplicationCall>.authenticateCatching(fallback: () -> User?): User? {
    val userService by call.inject<UserService>()
    val authService by call.inject<AuthService>()

    println("Debug 1")
    val headerToken = call.request.header(HttpHeaders.Authorization)?.substring(7) ?: return fallback()
    println("Debug 2")
    val userId = authService.decodeToken(headerToken) ?: return fallback()
    println("Debug 3")

    return userService.getUser(userId)
}