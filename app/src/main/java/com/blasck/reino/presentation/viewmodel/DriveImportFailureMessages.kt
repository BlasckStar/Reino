package com.blasck.reino.presentation.viewmodel

import com.blasck.reino.domain.importer.CharacterSheetImportFailure

fun CharacterSheetImportFailure.toDriveImportMessage(): String =
    when (this) {
        CharacterSheetImportFailure.InvalidFile ->
            "O arquivo do Drive nao e uma ficha XLSX valida."

        is CharacterSheetImportFailure.MissingSheet ->
            "A ficha do Drive nao possui a aba obrigatoria \"$sheetName\"."

        is CharacterSheetImportFailure.MissingRequiredField ->
            "O campo obrigatorio \"$fieldName\" nao foi encontrado na celula $cellAddress."

        is CharacterSheetImportFailure.InvalidFieldValue ->
            "O valor \"$value\" do campo \"$fieldName\" na celula $cellAddress nao e valido."

        is CharacterSheetImportFailure.ReadError ->
            "Nao foi possivel ler a ficha do Drive. ${message.orEmpty()}".trim()
    }
