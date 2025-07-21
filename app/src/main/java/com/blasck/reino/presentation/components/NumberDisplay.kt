package com.blasck.reino.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.blasck.reino.system.theme.KingdomTheme

@Composable
fun NumberDisplay(
    number: String,
    title: String
) {

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.displaySmall
        )
        Text(
            text = number,
            style = MaterialTheme.typography.bodySmall
        )
    }

}

@Preview(showBackground = true)
@Composable
fun NumberDisplayPreview() {
    KingdomTheme {
        NumberDisplay(number = "12", title = "Fadiga")
    }
}