package io.github.vibraplatform.database.util

import io.github.vibraplatform.common.struct.Page
import org.jetbrains.exposed.sql.EmptySizedIterable

inline fun <reified T> Iterable<T>.limit(n: Int, offset: Long): Iterable<T> {
    return if (offset >= Int.MAX_VALUE) {
        EmptySizedIterable()
    } else {
        drop(offset.toInt()).take(n)
    }
}

inline fun <reified T, reified R> Page<T>.map(func: (T) -> R): Page<R> =
    Page(page, pageSize, totalPages, items.map(func))

inline fun <reified T> Iterable<T>.paginate(page: Int, pageSize: Int) =
    Page(page, pageSize, count().let { if (it % pageSize == 0) it / pageSize else it / pageSize + 1 }, paginated(page, pageSize).toList())

inline fun <reified T> Iterable<T>.paginated(page: Int, pageSize: Int) =
    limit(pageSize, ((page - 1) * pageSize).toLong())
