package io.github.vibraplatform.common.struct

import kotlinx.serialization.Serializable

@Serializable
data class VibraError(
    val code: Int,
    val message: String
)

object VibraInternalErrors {
    val EmailAlreadyTaken = VibraError(
        code = 4001,
        message = "A user with the same email address already exists"
    )
    val IncorrectLoginCredentials = VibraError(
        code = 4002,
        message = "Either the username or password is incorrect"
    )
}