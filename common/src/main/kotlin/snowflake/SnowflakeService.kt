package io.github.vibraplatform.common.snowflake

import java.util.concurrent.ConcurrentHashMap

class SnowflakeService(val cluster: Long) {
    private val nodes = ConcurrentHashMap<Long, SnowflakeNode>()

    fun nextNode() = node(Thread.currentThread().id)

    fun node(nodeId: Long): SnowflakeNode {
        val existing = nodes[nodeId]
        if (existing != null) return existing

        val new = SnowflakeNode(cluster, nodeId)
        nodes[nodeId] = new
        return new
    }

    fun getNode(nodeId: Long): SnowflakeNode? = nodes[nodeId]

    fun generate(): Snowflake =
        nextNode().generate()
}