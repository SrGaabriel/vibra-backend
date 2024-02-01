package io.github.vibraplatform.webserver.route

import io.github.vibraplatform.common.snowflake.Snowflake
import io.github.vibraplatform.common.snowflake.SnowflakeService
import io.github.vibraplatform.common.struct.UserLoginRequest
import io.github.vibraplatform.common.struct.UserLoginResponse
import io.github.vibraplatform.common.struct.UserRegistryData
import io.github.vibraplatform.common.struct.VibraInternalErrors
import io.github.vibraplatform.database.service.UserService
import io.github.vibraplatform.database.util.toDTO
import io.github.vibraplatform.webserver.auth.AuthService
import io.github.vibraplatform.webserver.auth.authenticate
import io.github.vibraplatform.webserver.util.receiveOrBadRequest
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoute() {
    val authService by inject<AuthService>()
    val userService by inject<UserService>()
    val snowflakeService by inject<SnowflakeService>()
    post<UserRoute> {
        val signup = call.receiveOrBadRequest<UserRegistryData>() ?: return@post
        val userWithTheSameEmail = userService.getUserByEmail(signup.email)
        if (userWithTheSameEmail != null) {
            call.respond(HttpStatusCode.Conflict, VibraInternalErrors.EmailAlreadyTaken)
            return@post
        }
        val hashedPassword = authService.hashPassword(signup.password)
        val user = userService.createUser(
            snowflakeService.nextNode().generate(),
            UserRegistryData(
                signup.username,
                signup.displayName,
                signup.email,
                hashedPassword
            )
        )
        call.respond(
            HttpStatusCode.Created, UserLoginResponse(
            user.toDTO(),
            authService.createJwtToken(Snowflake(user.id!!))
        ))
    }
    get<UserRoute.Id> { (id) ->
        println(id)
        when (val user = userService.getUser(id)) {
            null -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(HttpStatusCode.OK, user.toDTO())
        }
    }
    get<UserRoute.Me> {
        authenticate { user ->
            call.respond(HttpStatusCode.OK, user.toDTO())
        }
    }
    post<LoginRoute> {
        val login = call.receiveOrBadRequest<UserLoginRequest>() ?: return@post
        val user = if (login.email == null) userService.getUserByUsername(login.username!!) else userService.getUserByEmail(login.email!!)
        val hashedPassword = authService.hashPassword(login.password)

        if (user == null || user.password != hashedPassword) {
            call.respond(HttpStatusCode.Unauthorized, VibraInternalErrors.IncorrectLoginCredentials)
            return@post
        }
        call.respond(
            HttpStatusCode.Accepted, UserLoginResponse(
                user.toDTO(),
                authService.createJwtToken(Snowflake(user.id!!))
            )
        )
    }
}

@Resource("/users")
class UserRoute {
    @Resource("/{id}")
    data class Id(val id: Snowflake, val users: UserRoute) {
        @Resource("/followers")
        data class Followers(val id: Id)
        @Resource("/posts")
        data class Posts(val id: Id) {
            @Resource("/{page}")
            data class Page(val page: Int, val posts: Posts)
        }
    }

    @Resource("/@me")
    data class Me(val users: UserRoute) {
        @Resource("/settings")
        data class Settings(val me: Me) {
            @Resource("/avatar")
            data class Avatar(val settings: Settings)
            @Resource("/banner")
            data class Banner(val settings: Settings)
        }
    }

}

@Resource("/login")
class LoginRoute