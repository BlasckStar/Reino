package com.blasck.reino.framework.service

import com.blasck.reino.data.model.reponse.CharacterListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KingdomService {

    @GET("characters")
    suspend fun getCharacterByType(@Query("type") filterType: String): Response<CharacterListResponse>

    @GET("characters")
    suspend fun getAllCharacters(): Response<CharacterListResponse>

}