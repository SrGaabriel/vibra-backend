package io.github.vibraplatform.common.struct

import io.github.vibraplatform.common.snowflake.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class RawPost(
    val id: Snowflake,
    val content: String,
    val author: RawPostAuthor,
    val likes: Int,
)

@Serializable
data class RawAuthenticatedPostView(
    val id: Snowflake,
    val content: String,
    val author: RawPostAuthor?,
    val statistics: RawPostStatistics,
    val relationship: RawPostUserRelationship
)

@Serializable
data class RawPostStatistics(
    val likes: Int,
    val replies: Int
)

@Serializable
data class RawPostAuthor(
    val id: Snowflake,
    val username: String,
    val displayName: String,
    val avatar: String?
)

@Serializable
data class RawPostUserRelationship(
    val liked: Boolean,
    val reposted: Boolean
)

@Serializable
data class PostCreationRequest(
    val content: String
)