package com.blasck.reino.presentation.utils

fun Any?.isNull(): Boolean {
    return this == null
}

fun Any?.isNotNull(): Boolean {
    return this != null
}

fun String.isNotNullOrEmpty(): Boolean {
    return this.isNotNull() && this.isNotEmpty()
}

fun Int.isNotNullOrZero(): Boolean {
    return this.isNotNull() && this != 0
}
