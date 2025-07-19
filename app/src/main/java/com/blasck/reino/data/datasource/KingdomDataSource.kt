package com.blasck.reino.data.datasource

import com.blasck.reino.data.model.reponse.CharacterListResponse
import retrofit2.Response

interface KingdomDataSource {

    suspend fun getCharacterByType(
        filterType: String
    ): Response<CharacterListResponse>

    suspend fun getAllCharacters(): Response<CharacterListResponse>

}