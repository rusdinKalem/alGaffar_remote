package com.roesch.jwsalgaffar.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.roesch.jwsalgaffar.R

@Composable
fun SetRow1(navController: NavHostController) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        Box(
            modifier = Modifier
                .clickable(
                    onClick = {navController.navigate("01")}
                )
        )
        { ShortCut("MASJID", R.drawable.a1) }

        Box(
            modifier = Modifier
                .clickable(
                    onClick = {navController.navigate("02")}
                )
        )
        { ShortCut("KOORDINAT", R.drawable.a2) }

        Box(
            modifier = Modifier
                .clickable(
                    onClick = {
                        navController.navigate("03")
                    }
                )
        )
        { ShortCut("WAKTU", R.drawable.a3) }
    }
}

@Composable
fun SetRow2(navController: NavHostController) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    onClick = {
//                        val intent = Intent(this, Screen2Activity::class.java)
//                        startActivity(intent)
                        navController.navigate("04")
                    }
                )
        )
        { ShortCut("KOREKSI", R.drawable.a4) }

        Box(
            modifier = Modifier
                .clickable(
                    onClick = {navController.navigate("05")}
                )
        )
        { ShortCut("IQOMAT", R.drawable.a5) }

        Box(
            modifier = Modifier
                .clickable(
                    onClick = {navController.navigate("06")}
                )
        )
        { ShortCut("TUNGGU", R.drawable.a6) }
    }
}

@Composable
fun SetRow3(navController: NavHostController) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        Box(
            modifier = Modifier
                .clickable(
                    onClick = {navController.navigate("07")}
                )
        )
        { ShortCut("INFO", R.drawable.a7) }

        Box(
            modifier = Modifier
                .clickable(
                    onClick = {navController.navigate("08")}
                )
        )
        { ShortCut("TOOLS", R.drawable.a8) }

        Box(
            modifier = Modifier
                .clickable(
                    onClick = {navController.navigate("10")}
                )
        )
        { ShortCut("MP3 AUDIO", R.drawable.baseline_music_note_24) }
    }
}
