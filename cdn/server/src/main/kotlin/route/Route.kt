package io.github.vibraplatform.cdn.server.route

import io.github.vibraplatform.cdn.server.image.ImageCompressor
import io.github.vibraplatform.common.snowflake.Snowflake
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Route.mediaRoute(key: String) {
    val imageCompressor by inject<ImageCompressor>()
    post<ProfilesRoute.Avatar> { (profile) ->
        if (key != call.request.headers["Authorization"]) {
            call.respond(HttpStatusCode.Unauthorized)
            return@post
        }

        val userId = profile.id
        val file = call.receiveMultipart().readPart()

        if (file == null || file !is PartData.FileItem) {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val inputStream = file.streamProvider()
        val bytes = imageCompressor.compress(inputStream, 0.5f).toByteArray()

        val avatarFile = File("avatars/$userId.jpg")
        avatarFile.writeBytes(bytes)
    }
}

@Resource("/profiles/{id}/")
class ProfilesRoute(val id: Snowflake) {
    @Resource("/avatar")
    data class Avatar(val profile: ProfilesRoute)
    @Resource("/banner")
    data class Banner(val profile: ProfilesRoute)
}