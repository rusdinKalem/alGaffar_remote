package com.roesch.jwsalgaffar.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShortCut(
    name: String,
    @DrawableRes image: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.92f), shape = RoundedCornerShape(14.dp))
                .border(
                    BorderStroke(1.5.dp, Color(0xFF00796B)),
                    RoundedCornerShape(14.dp)
                )
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(12.dp)
            )
        }
        Text(
            text = name,
            color = Color(0xFF1B1B1B),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}