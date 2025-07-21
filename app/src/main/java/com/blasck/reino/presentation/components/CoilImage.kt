package com.blasck.reino.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CoilImage(
    imageUrl: String,
    maxWidth: Dp? = null,
    maxHeight: Dp? = null
) {
    var imageRatio by remember { mutableStateOf<Float?>(null) }

    BoxWithConstraints {
        val screenWidth = maxWidth ?: maxWidth
        val calculatedWidth = maxWidth ?: this.maxWidth

        // Calcula a altura da imagem com base na razão de aspecto ou usa maxHeight
        val calculatedHeight = when {
            maxHeight != null -> maxHeight
            imageRatio != null -> calculatedWidth / imageRatio!!
            else -> 200.dp
        }

        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = "Mapa do reino",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .width(calculatedWidth)
                .height(calculatedHeight)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                ),
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            },
            error = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Erro ao carregar imagem", color = MaterialTheme.colorScheme.error)
                }
            },
            success = {
                val width = it.result.drawable.intrinsicWidth
                val height = it.result.drawable.intrinsicHeight
                if (width > 0 && height > 0) {
                    imageRatio = width.toFloat() / height.toFloat()
                }
                SubcomposeAsyncImageContent()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CoilImagePreview() {
    CoilImage("https://i.pinimg.com/736x/dc/7f/07/dc7f07eab2b3f5b86d256ed7b90f5809.jpg")
}