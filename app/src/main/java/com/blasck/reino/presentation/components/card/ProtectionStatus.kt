package com.blasck.reino.presentation.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProtectionStatus(
    left: String,
    right: String,
    result: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {

        val typography = MaterialTheme.typography.bodyLarge
        Text(
            text = left,
            style = typography
        )

        Text(
            text = "+",
            style = typography
        )

        Text(
            text = right,
            style = typography
        )

        Text(
            text = "=",
            style = typography
        )

        Text(
            text = result,
            style = typography
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProtectionStatusPreview() {
    ProtectionStatus(
        left = "12",
        right = "10",
        result = "2"
    )
}
