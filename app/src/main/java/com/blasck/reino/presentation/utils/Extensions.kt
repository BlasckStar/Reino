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

fun Int.formatWithMax(max: Int): String {
    return if (max > 0) {
        "$this/$max"
    } else {
        toString()
    }
}

fun Double.formatNumber(): String {
    return if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        toString()
    }
}