package com.blasck.reino.domain.usecase

import com.blasck.reino.domain.entity.KingdomFailureEntity
import com.blasck.reino.domain.entity.KingdomResult
import com.blasck.reino.domain.entity.reponse.CharacterListEntity
import com.blasck.reino.domain.repository.KingdomRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface GetCharacterListByTypeUseCase {
    operator fun invoke(filter: String): Flow<KingdomResult<CharacterListEntity, KingdomFailureEntity>>
}

class GetCharacterListByTypeUseCaseImpl(
    private val repository: KingdomRepository,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
): GetCharacterListByTypeUseCase {
    override fun invoke(filter: String): Flow<KingdomResult<CharacterListEntity, KingdomFailureEntity>> {
        return flow{
            emit(repository.getCharacterByType(filter))
        }.flowOn(coroutineDispatcher)
    }
}


