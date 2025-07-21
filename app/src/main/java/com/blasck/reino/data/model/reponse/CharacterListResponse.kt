package com.blasck.reino.data.model.reponse

import com.blasck.reino.data.util.EMPTY_STRING
import com.blasck.reino.domain.entity.reponse.CharacterListEntity
import com.google.gson.annotations.SerializedName

data class CharacterListResponse(
    @SerializedName("list") val list: List<CharacterResponse> = listOf()
){
    data class CharacterResponse(
        @SerializedName("name") val name: String = EMPTY_STRING,
        @SerializedName("icon") val img: String = EMPTY_STRING,
        @SerializedName("player") val player: String = EMPTY_STRING,
        @SerializedName("id") val id: String = EMPTY_STRING
    )

    fun CharacterResponse.toEntity() =
        CharacterListEntity.CharacterEntity(
            name = name,
            img = img,
            player = player,
            id = id
        )

}

fun CharacterListResponse.toEntity() =
    CharacterListEntity(
        list.map { it.toEntity() }
    )

