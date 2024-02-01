package io.github.vibraplatform.webserver.route

import io.github.vibraplatform.database.dao.User
import io.github.vibraplatform.database.service.UserService
import io.github.vibraplatform.database.util.openRelationalTransaction
import io.github.vibraplatform.webserver.auth.authenticate
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.delete
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.followRoute() {
    val userService by inject<UserService>()
    post<UserRoute.Id.Followers> { (id) ->
        authenticate { user ->
            val targetId = id.id
            if (user.id == targetId.value) {
                call.respond(HttpStatusCode.Conflict)
                return@authenticate
            }

            userService.openRelationalTransaction {
                val target = load(User::class.java, targetId.value)
                if (target == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@openRelationalTransaction
                }
                query(
                    """
                    MATCH (u:User {id: ${'$'}userId}), (t:User {id: ${'$'}targetId})
                    MERGE (u)-[:FOLLOWS]->(t)
                """.trimIndent(),
                    mapOf(
                        "userId" to user.id,
                        "targetId" to targetId.value
                    )
                )
            }
            call.respond(HttpStatusCode.OK)
        }
    }
    delete<UserRoute.Id.Followers> { (id) ->
        authenticate { user ->
            val targetId = id.id
            if (user.id == targetId.value) {
                call.respond(HttpStatusCode.Conflict)
                return@authenticate
            }

            userService.openRelationalTransaction {
                val target = load(User::class.java, targetId.value)
                if (target == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@openRelationalTransaction
                }
                query(
                    """
                    MATCH (u:User {id: ${'$'}userId})-[r:FOLLOWS]->(t:User {id: ${'$'}targetId})
                    DELETE r
                """.trimIndent(),
                    mapOf(
                        "userId" to user.id,
                        "targetId" to targetId.value
                    )
                )
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}