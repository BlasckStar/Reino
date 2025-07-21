package com.blasck.reino.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blasck.reino.R
import com.blasck.reino.system.theme.KingdomTheme

@Composable
fun IconNamedButton(
    title: String,
    icon: Int,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {


    Button(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(4.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = MaterialTheme.colorScheme.surface
        )
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = title,
                fontSize = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IconNamedButtonPreview() {
    KingdomTheme {
        IconNamedButton("Teste", R.drawable.ic_btn_dedicated, true) { }
    }
}

@Preview(showBackground = true)
@Composable
fun IconNamedButtonDisabledPreview() {
    KingdomTheme {
        IconNamedButton("Teste", R.drawable.ic_btn_dedicated, false) { }
    }
}