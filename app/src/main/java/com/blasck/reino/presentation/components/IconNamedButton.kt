package com.blasck.reino.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

@Composable
fun IconNamedButton(
    title: String,
    icon: Int,
    onClick: () -> Unit
) {
    Button(onClick = { onClick }) {
        Row {
            Icon(painter = painterResource(id = icon), contentDescription = "")
            Text(text = title)
        }
    }
}