package com.roesch.jwsalgaffar.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier

@Composable
fun Slider(kali: Int, label : String, modifier: Modifier = Modifier) {

    var sliderPosition by remember { mutableFloatStateOf(0f) }
    val dataSlider = sliderPosition*kali
    Column {
        Text(text = "$label = ${dataSlider}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it }
        )

    }
}