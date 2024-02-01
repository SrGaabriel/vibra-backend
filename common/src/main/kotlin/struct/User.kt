package io.github.vibraplatform.common.struct

import io.github.vibraplatform.common.snowflake.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class RawUser(
    val id: Snowflake,
    val username: String,
    val displayName: String,
    val followers: Int,
    val biography: String?,
)

@Serializable
data class UserLoginResponse(
    val user: RawUser,
    val token: String
)

@Serializable
data class UserLoginRequest(
    val email: String? = null,
    val username: String? = null,
    val password: String
)

@Serializable
data class UserRegistryData(
    val username: String,
    val displayName: String,
    val email: String,
    val password: String
)

@Serializable
data class UserSettingsPatch(
    val biography: String? = null
)