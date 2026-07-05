package com.stickwithit.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SupabaseFetchResult<T> (
    val data: T,
    val count: Int
)

data class SupabaseOperationsResult (
    val result: Boolean,
    val message: String
)