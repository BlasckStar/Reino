package com.blasck.reino.data.model.reponse

import com.blasck.reino.data.util.EMPTY_STRING
import com.blasck.reino.domain.entity.KingdomFailureEntity
import com.google.gson.annotations.SerializedName

data class KingdomFailureResponse(
    @SerializedName("code") val code: String = EMPTY_STRING,
    @SerializedName("message") val message: String = EMPTY_STRING,
    @SerializedName("description") val description: String = EMPTY_STRING,
    @SerializedName("error") val error: String = EMPTY_STRING
)

fun KingdomFailureResponse.toEntity() =
    KingdomFailureEntity(
        code = code,
        message = message,
        description = description,
        error = error
    )
