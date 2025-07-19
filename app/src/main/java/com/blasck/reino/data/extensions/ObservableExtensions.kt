@file:Suppress("UNCHECKED_CAST")

package com.blasck.reino.data.extensions

import com.blasck.reino.data.model.reponse.KingdomFailureResponse
import com.blasck.reino.data.model.reponse.toEntity
import com.blasck.reino.domain.entity.KingdomFailureEntity
import com.blasck.reino.domain.entity.KingdomResult
import com.blasck.reino.domain.extensions.ResponseExtension.toObject
import com.blasck.reino.presentation.utils.isNotNull
import retrofit2.Response

object ObservableExtensions {
    fun <T,V> Response<T>.toResult(
        transform: (value: T) -> V
    ): KingdomResult<V, KingdomFailureEntity> {
        return try{
            if(body().isNotNull() && isSuccessful){
                val body = body() as T
                KingdomResult.Success(transform(body))
            }else{
                val error = errorBody()?.toObject<KingdomFailureResponse>()?.toEntity()
                    ?: return KingdomResult.Error(Throwable("Error"))
                KingdomResult.Failure(error)
            }
        } catch (e: Exception){
            KingdomResult.Error(e)
        }
    }

    inline fun <T> safeKingdomBlock(block: () -> KingdomResult<T, KingdomFailureEntity>): KingdomResult<T, KingdomFailureEntity> {
        return try {
            block()
        } catch (e: Exception) {
            KingdomResult.Error(e)
        }
    }

}