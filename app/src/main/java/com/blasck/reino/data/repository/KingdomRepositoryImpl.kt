package com.blasck.reino.data.repository

import com.blasck.reino.data.datasource.KingdomDataSource
import com.blasck.reino.data.extensions.ObservableExtensions.safeKingdomBlock
import com.blasck.reino.data.extensions.ObservableExtensions.toResult
import com.blasck.reino.data.model.reponse.toEntity
import com.blasck.reino.domain.entity.KingdomFailureEntity
import com.blasck.reino.domain.entity.KingdomResult
import com.blasck.reino.domain.entity.reponse.CharacterListEntity
import com.blasck.reino.domain.repository.KingdomRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KingdomRepositoryImpl(
    private val kingdomDataSource: KingdomDataSource,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
): KingdomRepository {
    override suspend fun getCharacterByType(filter: String): KingdomResult<CharacterListEntity, KingdomFailureEntity> =
        withContext(coroutineDispatcher){
            safeKingdomBlock {
                kingdomDataSource.getCharacterByType(filter).toResult { it.toEntity() }
            }
        }


    override suspend fun getAllCharacters(): KingdomResult<CharacterListEntity, KingdomFailureEntity> =
        withContext(coroutineDispatcher){
            safeKingdomBlock {
                kingdomDataSource.getAllCharacters().toResult{ it.toEntity()}
            }
        }

}

