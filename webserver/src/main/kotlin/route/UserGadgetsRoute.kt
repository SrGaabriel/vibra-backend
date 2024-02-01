package io.github.vibraplatform.webserver.route

import io.github.vibraplatform.common.struct.UserSettingsPatch
import io.github.vibraplatform.database.dao.User
import io.github.vibraplatform.database.service.UserService
import io.github.vibraplatform.webserver.auth.authenticate
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.patch
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userGadgetsRoute() {
    val userService by inject<UserService>()
    patch<UserRoute.Me.Settings> {
        authenticate { user ->
            val patch = call.receiveOrNull<UserSettingsPatch>() ?: return@patch

            var updated = false
            userService.updateUser(user) {
                if (patch.biography != null) {
                    updated = true
                    user.copy(biography = patch.biography)
                } else {
                    user
                }
            }
            call.respond(if (updated) HttpStatusCode.OK else HttpStatusCode.NoContent)
        }
    }
}


