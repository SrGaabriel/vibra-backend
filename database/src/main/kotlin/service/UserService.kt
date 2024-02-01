package io.github.vibraplatform.database.service

import io.github.vibraplatform.common.snowflake.Snowflake
import io.github.vibraplatform.common.struct.UserRegistryData
import io.github.vibraplatform.database.dao.User
import io.github.vibraplatform.database.data.DataService
import io.github.vibraplatform.database.data.RelationalDataSource
import io.github.vibraplatform.database.util.openSuspendedSession

class UserService(override val datasource: RelationalDataSource): DataService {
    suspend fun createUser(id: Snowflake, data: UserRegistryData): User = openSuspendedSession {
        queryForObject(
            User::class.java,
            """
                CREATE (u:User {
                    id: ${'$'}id,
                    username: ${'$'}username,
                    displayName: ${'$'}displayName,
                    email: ${'$'}email,
                    password: ${'$'}password
                })
                RETURN u
            """.trimIndent(),
            mapOf(
                "id" to id.value,
                "username" to data.username,
                "displayName" to data.displayName,
                "email" to data.email,
                "password" to data.password
            )
        ) ?: throw RuntimeException("User with id $id already exists")
    }

    suspend fun updateUser(id: Snowflake, update: (User) -> Unit): User = openSuspendedSession {
        val user = queryForObject(
            User::class.java,
            "MATCH (u:User {id: ${'$'}id}) RETURN u",
            mapOf("id" to id.value)
        )
        update(user)
        save(user)
        user ?: throw RuntimeException("User with id $id does not exist")
    }

    suspend fun updateUser(user: User, update: () -> User) = openSuspendedSession {
        save(update())
    }

    suspend fun getUser(id: Snowflake): User? = openSuspendedSession {
        load(User::class.java, id.value)
    }

    suspend fun getUserByEmail(email: String): User? = openSuspendedSession {
        runCatching {
            queryForObject(
                User::class.java,
                """
                MATCH (u:User {email: ${'$'}email})
                RETURN u
            """.trimIndent(),
                mapOf("email" to email)
            )
        }.getOrNull()
    }
    
    suspend fun getUserByUsername(username: String): User? = openSuspendedSession {
        queryForObject(
            User::class.java,
            """
                MATCH (u:User {username: $username})
                RETURN u
            """.trimIndent(),
            EMPTY_MAP
        )
    }

    suspend fun deleteUser(id: Snowflake): Unit = openSuspendedSession {
        query(
            """
                MATCH (u:User {id: $id})
                DETACH DELETE u
            """.trimIndent(),
            EMPTY_MAP
        )
    }

    companion object {
        private val EMPTY_MAP = mapOf<String, Any>()
    }
}