package io.github.vibraplatform.webserver.route

import io.github.vibraplatform.common.snowflake.Snowflake
import io.github.vibraplatform.common.snowflake.SnowflakeService
import io.github.vibraplatform.common.snowflake.snowflakeAgeComparator
import io.github.vibraplatform.common.struct.PostCreationRequest
import io.github.vibraplatform.database.dao.Post
import io.github.vibraplatform.database.service.PostService
import io.github.vibraplatform.database.service.UserService
import io.github.vibraplatform.database.util.*
import io.github.vibraplatform.webserver.auth.authenticate
import io.github.vibraplatform.webserver.auth.authenticateOrNull
import io.github.vibraplatform.webserver.util.receiveOrBadRequest
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.resources.post
import org.koin.ktor.ext.inject
import java.time.Instant

fun Route.postRoute() {
    val userService by inject<UserService>()
    val postService by inject<PostService>()
    val snowflakeService by inject<SnowflakeService>()
    post<PostRoute> {
        authenticate { user ->
            val postForm = call.receiveOrBadRequest<PostCreationRequest>() ?: return@post
            val post = postService.createPost(
                snowflakeService.nextNode().generate(),
                user,
                postForm.content
            )
            call.respond(HttpStatusCode.Created, post.toDTO())
        }
    }
    get<UserRoute.Id.Posts.Page> { (page, posts) ->
        val authorId = posts.id.id
        val author = userService.getUser(authorId)
        if (author == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        val user = authenticateOrNull()
        val page = author.posts
            .filter { it.replyTo == null }
            .sortedWith(snowflakeAgeComparator<Post> { Snowflake(it.id!!) }.reversed())
            .paginate(page, 15)

        if (user == null) {
            call.respond(HttpStatusCode.OK, page.map { it.toDTO() })
        } else {
            call.respond(HttpStatusCode.OK, page.map { it.toAuthenticatedDTO(user, postService.getPostStatistics(Snowflake(it.id!!))) })
        }
    }
    get<PostRoute.Id> { (id) ->
        val post = postService.getPost(id)
        val user = authenticateOrNull()

        if (post == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }

        if (user == null) {
            call.respond(HttpStatusCode.OK, post.toDTO())
        } else {
            call.respond(HttpStatusCode.OK, post.toAuthenticatedDTO(user, postService.getPostStatistics(id)))
        }
    }
    post<PostRoute.Id.Replies> { (id) ->
        authenticate { user ->
            val post = postService.getPost(id.id)
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@post
            }
            val postForm = call.receiveOrBadRequest<PostCreationRequest>() ?: return@post
            val reply = postService.createPost(
                snowflakeService.nextNode().generate(),
                user,
                postForm.content,
                post
            )
            call.respond(HttpStatusCode.Created, reply.toDTO())
        }
    }
    get<PostRoute.Id.Replies.Page> { (page, replies) ->
        val postId = replies.id.id
        val post = postService.getPost(postId)
        if (post == null) {
            call.respond(HttpStatusCode.NotFound)
            return@get
        }
        val user = authenticateOrNull()
        val page = postService.getPostReplies(postId)
            .sortedByDescending { it.likes.size }
            .paginate(page, 15)

        if (user == null) {
            call.respond(HttpStatusCode.OK, page.map { it.toDTO() })
        } else {
            call.respond(HttpStatusCode.OK, page.map { it.toAuthenticatedDTO(user, postService.getPostStatistics(Snowflake(it.id!!))) })
        }
    }
    createStatisticEndpoints<PostRoute.Id.Likes>(
        postService,
        "LIKED"
    ) { it.id.id }
    createStatisticEndpoints<PostRoute.Id.Reposts>(
        postService,
        "REPOSTED"
    ) { it.id.id }
}

@Resource("/posts")
class PostRoute {
    @Resource("/{id}")
    data class Id(val id: Snowflake, val posts: PostRoute) {
        @Resource("/likes")
        data class Likes(val id: Id)
        @Resource("/reposts")
        data class Reposts(val id: Id)
        @Resource("/replies")
        data class Replies(val id: Id) {
            @Resource("/{page}")
            data class Page(val page: Int, val id: Replies)
        }
    }
}

inline fun <reified T : Any> Route.createStatisticEndpoints(
    postService: PostService,
    relationshipName: String,
    crossinline postIdFetcher: (T) -> Snowflake,
) {
    post<T> {
        authenticate { user ->
            val postId = postIdFetcher(it)
            val post = postService.getPost(postId)
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@post
            }
            postService.openSuspendedSession {
                query(
                    """
                        MATCH (u:User {id: ${'$'}userId}), (p:Post {id: ${'$'}postId})
                        MERGE (u)-[:${relationshipName}]->(p)
                    """.trimIndent(),
                    mapOf(
                        "userId" to user.id,
                        "postId" to postId.value
                    )
                )
            }
            call.respond(HttpStatusCode.OK)
        }
    }
    delete<T> {
        authenticate { user ->
            val postId = postIdFetcher(it)
            val post = postService.getPost(postId)
            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }
            postService.openSuspendedSession {
                query(
                    """
                        MATCH (u:User {id: ${'$'}userId})-[r:${relationshipName}]->(p:Post {id: ${'$'}postId})
                        DELETE r
                    """.trimIndent(),
                    mapOf(
                        "userId" to user.id,
                        "postId" to postId.value
                    )
                )

            }
            call.respond(HttpStatusCode.OK)
        }
    }
}