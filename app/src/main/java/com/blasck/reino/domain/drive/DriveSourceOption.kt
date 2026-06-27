package com.blasck.reino.domain.drive

import kotlinx.serialization.Serializable

@Serializable
data class DriveSourceOption(
    val name: String,
    val link: String,
    val description: String = "",
) {
    val folderId: String?
        get() = DriveFolderLinkParser.extractFolderId(link)
}
