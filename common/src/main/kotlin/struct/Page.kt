package io.github.vibraplatform.common.struct

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Page<T>(
    val page: Int,
    @SerialName("page_size")
    val pageSize: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    val items: List<T>,
)