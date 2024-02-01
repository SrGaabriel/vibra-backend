package io.github.vibraplatform.common.snowflake

object SnowflakeAgeComparator: Comparator<Snowflake> {
    override fun compare(o1: Snowflake, o2: Snowflake): Int {
        println("SnowflakeAgeComparator.compare($o1, $o2)")
        println("Time difference: ${o1.timeDifference - o2.timeDifference}")
        println("Sequence: ${o1.sequence - o2.sequence}")
        println("Node ID: ${o1.nodeId - o2.nodeId}")
        println("Cluster ID: ${o1.clusterId - o2.clusterId}")
        val timeDifference = o1.timeDifference.compareTo(o2.timeDifference)
        if (timeDifference != 0) return timeDifference
        return o1.sequence.compareTo(o2.sequence)
    }
}

inline fun <reified T> snowflakeAgeComparator(crossinline selector: (T) -> Snowflake): Comparator<T> =
    Comparator { o1, o2 -> SnowflakeAgeComparator.compare(selector(o1), selector(o2)) }