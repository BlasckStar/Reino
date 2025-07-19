package com.blasck.reino.presentation.models.response

import com.blasck.reino.domain.entity.reponse.CharacterListEntity
import com.blasck.reino.presentation.utils.Constants

data class CharacterList(
    val list: List<CharacterInfo> = listOf()
) {
    data class CharacterInfo(
        val name: String = Constants.EMPTY_STRING,
        val img: String = Constants.EMPTY_STRING,
        val player: String = Constants.EMPTY_STRING,
        val id: String = Constants.EMPTY_STRING
    )

    companion object{
        fun CharacterListEntity.CharacterEntity.fromEntity() =
            CharacterInfo(
                name = name,
                img = img,
                player = player
            )

        fun CharacterListEntity.fromEntity() =
            CharacterList(
                list = list.map { it.fromEntity() }
            )
    }


}



