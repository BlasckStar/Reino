package com.blasck.reino.framework.datasource

import com.blasck.reino.data.datasource.KingdomDataSource
import com.blasck.reino.data.model.reponse.CharacterListResponse
import com.blasck.reino.framework.service.KingdomService
import retrofit2.Response
import retrofit2.Retrofit

class KingdomDataSourceImpl(
    private val connector: Retrofit
): KingdomDataSource {

    private val service by lazy { connector.create(KingdomService::class.java) }

    override suspend fun getCharacterByType(filterType: String): Response<CharacterListResponse> {
        return service.getCharacterByType(filterType)
    }

    override suspend fun getAllCharacters(): Response<CharacterListResponse> {
        return service.getAllCharacters()
    }

}