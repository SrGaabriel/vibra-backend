package io.github.vibraplatform.database.data

import org.neo4j.ogm.session.Session

fun interface RelationalDataSource {
    fun getSession(): Session
}