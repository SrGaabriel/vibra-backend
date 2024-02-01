package io.github.vibraplatform.common.snowflake


class SnowflakeNode(private val clusterId: Long, private val nodeId: Long) {
    @Volatile
    var lastGenerated = -1L
    @Volatile
    var sequence = 0L

    @Synchronized
    fun generate(): Snowflake {
        val timestamp = (System.currentTimeMillis() / 1000) - VIBRA_EPOCH
        assert(timestamp >= lastGenerated)

        sequence = if (timestamp == lastGenerated) sequence + 1 else 0
        lastGenerated = timestamp

        return Snowflake(timestamp, nodeId, clusterId, sequence)
    }

    companion object {
        const val VIBRA_EPOCH = 164_038_400

        const val NODE_ID_BITS = 5
        const val CLUSTER_ID_BITS = 5
        const val SEQUENCE_BITS = 12

        const val TIMESTAMP_SHIFT = NODE_ID_BITS + CLUSTER_ID_BITS + SEQUENCE_BITS
        const val NODE_SHIFT = CLUSTER_ID_BITS + SEQUENCE_BITS

        const val MASK_NODE_ID = (1L shl NODE_ID_BITS) - 1 shl NODE_SHIFT
        const val MASK_CLUSTER_ID = (1L shl CLUSTER_ID_BITS) -1 shl SEQUENCE_BITS
        const val MASK_SEQUENCE = (1L shl SEQUENCE_BITS) - 1
    }
}