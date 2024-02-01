package io.github.vibraplatform.database.service

import io.github.vibraplatform.common.snowflake.Snowflake
import io.github.vibraplatform.common.struct.RawPostStatistics
import io.github.vibraplatform.database.dao.Post
import io.github.vibraplatform.database.dao.User
import io.github.vibraplatform.database.data.DataService
import io.github.vibraplatform.database.data.RelationalDataSource
import io.github.vibraplatform.database.util.openRelationalTransaction
import io.github.vibraplatform.database.util.openSuspendedSession

class PostService(override val datasource: RelationalDataSource): DataService {
    suspend fun createPost(
        id: Snowflake,
        author: User,
        content: String,
        replyTo: Post? = null
    ): Post = openRelationalTransaction {
        val post = queryForObject(
            Post::class.java,
            """
                CREATE (p:Post {
                    id: ${'$'}id,
                    content: ${'$'}content
                })
                RETURN p
            """.trimIndent(),
            mapOf(
                "id" to id.value,
                "content" to content
            )
        )
        query(
            """
                MATCH (u:User {id: ${'$'}userId}), (p:Post {id: ${'$'}postId})
                MERGE (u)-[:POSTED]->(p)
            """.trimIndent(),
            mapOf(
                "userId" to author.id,
                "postId" to id.value
            )
        )
        post.author = author
        if (replyTo != null) {
            query(
                """
                    MATCH (p:Post {id: ${'$'}postId}), (r:Post {id: ${'$'}replyToId})
                    MERGE (p)-[:REPLY_TO]->(r)
                """.trimIndent(),
                mapOf(
                    "postId" to id.value,
                    "replyToId" to replyTo.id
                )
            )
            post.replyTo = replyTo
        }
        save(post)
        post
    }

    suspend fun getPost(postId: Snowflake): Post? = openSuspendedSession {
        load(Post::class.java, postId.value)
    }

    suspend fun getPostReplies(postId: Snowflake): Iterable<Post> = openSuspendedSession {
        query(
            Post::class.java,
            """
                MATCH (p:Post {id: ${'$'}postId})-[:REPLY_TO]->(r:Post)
                RETURN r
            """.trimIndent(),
            mapOf("postId" to postId.value),
        )
    }

    suspend fun getPostStatistics(postId: Snowflake): RawPostStatistics = openSuspendedSession {
        val likes = queryForObject(
            java.lang.Integer::class.java,
            """
                MATCH (p:Post {id: ${'$'}postId})<-[:LIKED]-(:User)
                RETURN count(p)
            """.trimIndent(),
            mapOf("postId" to postId.value)
        ).toInt()
        val replies = queryForObject(
            java.lang.Integer::class.java,
            """
                MATCH (p:Post {id: ${'$'}postId})-[:REPLY_TO]->(:Post)
                RETURN count(p)
            """.trimIndent(),
            mapOf("postId" to postId.value)
        ).toInt()
        RawPostStatistics(
            likes = likes,
            replies = replies
        )
    }
}