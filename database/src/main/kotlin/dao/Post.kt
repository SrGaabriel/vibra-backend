package io.github.vibraplatform.database.dao

import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.Index
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity("Post")
class Post @JvmOverloads constructor(
    @Id @Index(unique = true)
    var id: Long? = null,
    var content: String? = null,
    @Relationship(type="LIKED", direction = Relationship.Direction.INCOMING)
    var likes: List<User> = emptyList(),
    @Relationship(type="REPOSTED", direction = Relationship.Direction.INCOMING)
    var reposts: List<User> = emptyList(),
    @Relationship(type="POSTED", direction = Relationship.Direction.INCOMING)
    var author: User? = null,
    @Relationship(type="REPLY_TO", direction = Relationship.Direction.OUTGOING)
    var replyTo: Post? = null,
    @Relationship(type="REPLY_TO", direction = Relationship.Direction.INCOMING)
    var replies: List<Post> = emptyList()
)