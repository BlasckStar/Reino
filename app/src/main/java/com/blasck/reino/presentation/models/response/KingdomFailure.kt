package com.blasck.reino.presentation.models.response

import com.blasck.reino.domain.entity.KingdomFailureEntity
import com.blasck.reino.presentation.utils.Constants

data class KingdomFailure(
    val code: String = Constants.EMPTY_STRING,
    val message: String = Constants.EMPTY_STRING,
    val description: String = Constants.EMPTY_STRING,
    val error: String = Constants.EMPTY_STRING
) {

    companion object{
        fun KingdomFailureEntity.fromEntity() =
            KingdomFailure(
                code = code,
                message = message,
                description = description,
                error = error
            )

    }

}