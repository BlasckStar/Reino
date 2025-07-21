package com.blasck.reino.domain.repository

import com.blasck.reino.domain.entity.KingdomFailureEntity
import com.blasck.reino.domain.entity.KingdomResult
import com.blasck.reino.domain.entity.reponse.CharacterListEntity

interface KingdomRepository {
    suspend fun getCharacterByType(filter: String): KingdomResult<CharacterListEntity, KingdomFailureEntity>
    suspend fun getAllCharacters(): KingdomResult<CharacterListEntity, KingdomFailureEntity>
}