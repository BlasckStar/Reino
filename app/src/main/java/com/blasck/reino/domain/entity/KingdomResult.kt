package com.blasck.reino.domain.entity

sealed class KingdomResult<out S, out F> {
    data class Success<out S>(val data: S) : KingdomResult<S, Nothing>()
    data class Failure<out F>(val error: F) : KingdomResult<Nothing, F>()
    data class Error(val throwable: Throwable) : KingdomResult<Nothing, Nothing>()
}