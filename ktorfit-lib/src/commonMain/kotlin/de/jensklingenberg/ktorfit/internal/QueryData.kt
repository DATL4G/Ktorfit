package de.jensklingenberg.ktorfit.internal

class QueryData(
    val encoded: Boolean = false,
    val data: Any?,
    val key: String,
    val type: String
)


typealias FieldData = QueryData