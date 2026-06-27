package com.blasck.reino.domain.importer

import com.blasck.reino.domain.model.Character
import com.blasck.reino.domain.model.CharacterSheetFormat
import java.io.InputStream

interface CharacterSheetImporter {
    fun import(input: InputStream): CharacterSheetImportResult
}

sealed interface CharacterSheetImportResult {
    data class Success(
        val character: Character,
        val format: CharacterSheetFormat,
    ) : CharacterSheetImportResult

    data class Failure(
        val reason: CharacterSheetImportFailure,
    ) : CharacterSheetImportResult
}

sealed interface CharacterSheetImportFailure {
    data object InvalidFile : CharacterSheetImportFailure

    data class MissingSheet(
        val sheetName: String,
    ) : CharacterSheetImportFailure

    data class MissingRequiredField(
        val fieldName: String,
        val cellAddress: String,
    ) : CharacterSheetImportFailure

    data class InvalidFieldValue(
        val fieldName: String,
        val cellAddress: String,
        val value: String,
    ) : CharacterSheetImportFailure

    data class ReadError(
        val message: String?,
    ) : CharacterSheetImportFailure
}
