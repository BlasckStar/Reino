package com.blasck.reino.framework.mock

import com.blasck.reino.presentation.models.response.CharacterList

object CharacterListMock {
    val mockList = CharacterList(
        list = listOf(
            CharacterList.CharacterInfo(
                name = "Syrio Augusto",
                player = "Luiz Thomaz",
                img = "https://i.etsystatic.com/35849450/r/il/10128e/6770579271/il_300x300.6770579271_e3kh.jpg",
                id = "1"
            )
        )
    )

}