package io.github.vibraplatform.database.util

import io.github.vibraplatform.database.data.DataService
import io.github.vibraplatform.database.data.RelationalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.neo4j.ogm.session.Session
import kotlin.coroutines.CoroutineContext

fun <T> DataService.openSession(op: Session.() -> T) =
    datasource.getSession().op()

suspend fun <T> DataService.openSuspendedSession(
    context: CoroutineContext = Dispatchers.IO,
    op: suspend Session.() -> T
) = CoroutineScope(context).async {
    datasource.getSession().op()
}.await()

suspend fun <T> DataService.openRelationalTransaction(scope: suspend Session.() -> T) = openSuspendedSession {
    val transaction = beginTransaction()
    var savedResult: T? = null
    try {
        val result = scope()
        transaction.commit()
        savedResult = result
    } catch (e: Exception) {
        transaction.rollback()
    }
    savedResult ?: throw RuntimeException("Transaction failed")
}

inline fun <reified T : Any> RelationalDataSource.parameterizedObjectQuery(query: String, vararg params: Pair<String, Any?>): String {
    var result = query
    params.forEach { (key, value) ->
        result = result.replace("", value.toString())
    }
    return result
}