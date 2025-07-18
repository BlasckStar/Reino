package com.blasck.reino.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CoilImage(imageUrl: String) {
    var imageRatio by remember { mutableStateOf<Float>(0f) }

    BoxWithConstraints {
        val screenWidth = maxWidth

        // Quando ratio está disponível, calcula a altura da imagem mantendo proporção
        val imageHeight = imageRatio.let { screenWidth / it } ?: 200.dp

        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = "Mapa do reino",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(screenWidth)
                .height(imageHeight)
                .clip(RoundedCornerShape(8.dp)) // Aplica cantos arredondados na imagem
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                ), // Borda aplicada com base no tamanho da imagem real
            onSuccess = {
                val width = it.result.drawable.intrinsicWidth
                val height = it.result.drawable.intrinsicHeight
                if (width > 0 && height > 0) {
                    imageRatio = width.toFloat() / height.toFloat()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CoilImagePreview() {
    CoilImage("https://i.pinimg.com/736x/dc/7f/07/dc7f07eab2b3f5b86d256ed7b90f5809.jpg")
}
