package com.roesch.jwsalgaffar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.roesch.jwsalgaffar.R

@Composable
fun TombolIcon(name: String?, icon: ImageVector, navController: NavController, nav: String) {
    Row(
        modifier = Modifier
            .clickable(onClick = { navController.navigate(nav) })
            .fillMaxWidth(0.65f)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp))
            .background(color = Color(0xFF00796B), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Icon(
            imageVector = icon,
            contentDescription = name,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun TombolHome(name: String?, navController: NavController) {
    Row(
        modifier = Modifier
            .clickable(onClick = {
                navController.navigate("Home") {
                    popUpTo("Home") { inclusive = true }
                }
            })
            .fillMaxWidth(0.65f)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp))
            .background(color = Color(0xFF37474F), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name ?: "MENU UTAMA",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Icon(
            painter = painterResource(id = R.drawable.baseline_add_to_home_screen_24),
            contentDescription = "Home",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun TombolKirim() {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.65f)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp))
            .background(color = Color(0xFF00796B), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "KIRIM DATA",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Icon(
            painter = painterResource(id = R.drawable.baseline_send_24),
            contentDescription = "Kirim",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}
