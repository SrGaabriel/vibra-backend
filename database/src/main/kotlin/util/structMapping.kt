package io.github.vibraplatform.database.util

import io.github.vibraplatform.common.snowflake.Snowflake
import io.github.vibraplatform.common.struct.*
import io.github.vibraplatform.database.dao.Post
import io.github.vibraplatform.database.dao.User

fun User.toDTO() = RawUser(
    id = Snowflake(id!!),
    username = username!!,
    displayName = displayName!!,
    followers = followers.size,
    biography = biography
)

fun User.asAuthor() = RawPostAuthor(
    id = Snowflake(id!!),
    username = username!!,
    displayName = displayName!!,
    avatar = null
)

fun Post.toDTO() = RawPost(
    id = Snowflake(id!!),
    content = content!!,
    author = author!!.asAuthor(),
    likes = likes.size,
)

fun Post.toAuthenticatedDTO(user: User, statistics: RawPostStatistics) = RawAuthenticatedPostView(
    id = Snowflake(id!!),
    content = content!!,
    author = author?.asAuthor(),
    statistics = RawPostStatistics(
        likes = likes.size,
        replies = replies.size
    ),
    relationship = RawPostUserRelationship(
        liked = likes.any { it.id == user.id },
        reposted = reposts.any { it.id == user.id }
    ),
)