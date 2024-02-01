package io.github.vibraplatform.database.dao

import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.Index
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity("User")
class User @JvmOverloads constructor(
    @Id @Index(unique = true)
    var id: Long? = null,
    var email: String? = null,
    var username: String? = null,
    var password: String? = null,
    var biography: String? = null,
    var displayName: String? = null,
    @Relationship(type="FOLLOWS", direction = Relationship.Direction.OUTGOING)
    var following: ArrayList<User> = arrayListOf(),
    @Relationship(type="FOLLOWS", direction = Relationship.Direction.INCOMING)
    var followers: ArrayList<User> = arrayListOf(),
    @Relationship(type="LIKED", direction = Relationship.Direction.OUTGOING)
    var liked: ArrayList<Post> = arrayListOf(),
    @Relationship(type="POSTED", direction = Relationship.Direction.OUTGOING)
    var posts: ArrayList<Post> = arrayListOf(),
) {
    fun copy(
        id: Long? = this.id,
        email: String? = this.email,
        username: String? = this.username,
        password: String? = this.password,
        biography: String? = this.biography,
        displayName: String? = this.displayName,
        following: ArrayList<User> = this.following,
        followers: ArrayList<User> = this.followers,
        liked: ArrayList<Post> = this.liked,
        posts: ArrayList<Post> = this.posts,
    ): User = User(
        id = id,
        email = email,
        username = username,
        password = password,
        biography = biography,
        displayName = displayName,
        following = following,
        followers = followers,
        liked = liked,
        posts = posts,
    )
}