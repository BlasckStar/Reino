package com.blasck.reino.domain.entity.reponse

data class CharacterListEntity(
    val list: List<CharacterEntity> = listOf()
){
    data class CharacterEntity(
        val name: String,
        val img: String,
        val player: String,
        val id: String
        )
}
