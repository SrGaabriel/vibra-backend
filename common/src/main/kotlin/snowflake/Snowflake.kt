package io.github.vibraplatform.common.snowflake

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@JvmInline
@Serializable
value class Snowflake(val value: Long): Comparable<Snowflake> {
    constructor(
        timestamp: Long,
        nodeId: Long,
        clusterId: Long,
        sequence: Long
    ): this(timestamp shl (SnowflakeNode.TIMESTAMP_SHIFT) or (nodeId shl SnowflakeNode.NODE_SHIFT) or (clusterId shl SnowflakeNode.SEQUENCE_BITS) or sequence)

    val timeDifference: Duration
        get() = (value shr SnowflakeNode.TIMESTAMP_SHIFT).seconds

    val timestamp: Instant
        get() = Instant.fromEpochSeconds(SnowflakeNode.VIBRA_EPOCH + timeDifference.inWholeSeconds)

    val nodeId: Long
        get() = (value and SnowflakeNode.MASK_NODE_ID) shr SnowflakeNode.NODE_SHIFT

    val clusterId: Long
        get() = (value and SnowflakeNode.MASK_CLUSTER_ID) shr SnowflakeNode.SEQUENCE_BITS

    val sequence: Long
        get() = (value and SnowflakeNode.MASK_SEQUENCE)

    override fun compareTo(other: Snowflake): Int = value.compareTo(other.value)

    override fun toString(): String = value.toString()
}