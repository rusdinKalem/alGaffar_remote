package com.roesch.jwsalgaffar.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.roesch.jwsalgaffar.R
import com.roesch.jwsalgaffar.components.SetRow1
import com.roesch.jwsalgaffar.components.SetRow2
import com.roesch.jwsalgaffar.components.SetRow3
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import com.roesch.jwsalgaffar.utils.ConnectionMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var showSwitchConfirmDialog by remember { mutableStateOf(false) }
    var isSwitching by remember { mutableStateOf(false) }

    val currentMode = BluetoothConnectionHolder.connectionMode

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Islamic Background Wallpaper (Masjid Nabawi Green Dome)
        Image(
            painter = painterResource(id = R.drawable.bg02),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.30f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(35.dp))

            // Header Banner Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.90f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MENU PENGATURAN",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1B1B)
                    )
                    Text(
                        text = "JWS alGaffar Controller",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00796B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Badge Indikator Mode Aktif
                    Box(
                        modifier = Modifier
                            .background(
                                if (currentMode == ConnectionMode.BLUETOOTH) Color(0xFFE0F2F1) else Color(0xFFE1F5FE),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (currentMode == ConnectionMode.BLUETOOTH) Color(0xFF00796B).copy(alpha = 0.5f) else Color(0xFF0288D1).copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(
                                    id = if (currentMode == ConnectionMode.BLUETOOTH)
                                        R.drawable.baseline_bluetooth_connected_24
                                    else
                                        R.drawable.baseline_wifi_24
                                ),
                                contentDescription = null,
                                tint = if (currentMode == ConnectionMode.BLUETOOTH) Color(0xFF00796B) else Color(0xFF0288D1),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (currentMode == ConnectionMode.BLUETOOTH)
                                    "TERHUBUNG: BLUETOOTH"
                                else
                                    "TERHUBUNG: WIFI ESP32",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentMode == ConnectionMode.BLUETOOTH) Color(0xFF004D40) else Color(0xFF01579B)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 9 Menu Utama (Grid 3x3)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SetRow1(navController)
                SetRow2(navController)
                SetRow3(navController)
            }

            Spacer(modifier = Modifier.height(22.dp))

            // =====================================================================
            // KARTU SWITCH MODE HARDWARE ESP32 (WIFI <-> BLUETOOTH)
            // =====================================================================
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(horizontal = 8.dp)
                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(16.dp))
                    .clickable { showSwitchConfirmDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
                border = BorderStroke(
                    1.5.dp,
                    if (currentMode == ConnectionMode.BLUETOOTH) Color(0xFF00796B) else Color(0xFF0288D1)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                if (currentMode == ConnectionMode.BLUETOOTH) Color(0xFFE0F2F1) else Color(0xFFE1F5FE),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (currentMode == ConnectionMode.BLUETOOTH)
                                    R.drawable.baseline_wifi_24
                                else
                                    R.drawable.baseline_bluetooth_connected_24
                            ),
                            contentDescription = null,
                            tint = if (currentMode == ConnectionMode.BLUETOOTH) Color(0xFF00796B) else Color(0xFF0288D1),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentMode == ConnectionMode.BLUETOOTH)
                                "GANTI JWS KE MODE WIFI"
                            else
                                "GANTI JWS KE MODE BLUETOOTH",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF1B1B1B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (currentMode == ConnectionMode.BLUETOOTH)
                                "Reboot ESP32 ke Web Portal Hotspot (192.168.4.1)"
                            else
                                "Reboot ESP32 ke Bluetooth SPP (JWS-RGB-P5)",
                            fontSize = 11.sp,
                            color = Color(0xFF616161)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.baseline_send_24),
                        contentDescription = "Switch Mode",
                        tint = if (currentMode == ConnectionMode.BLUETOOTH) Color(0xFF00796B) else Color(0xFF0288D1),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Jika dalam mode WiFi, sediakan juga tombol cepat buka Web Dashboard browser
            if (currentMode == ConnectionMode.WIFI) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(horizontal = 8.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp))
                        .clickable {
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.4.1"))
                                context.startActivity(browserIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Gagal membuka browser: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.94f)),
                    border = BorderStroke(1.dp, Color(0xFFEF6C00).copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFFFF3E0), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_add_to_home_screen_24),
                                contentDescription = null,
                                tint = Color(0xFFEF6C00),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "BUKA WEB DASHBOARD",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = "Buka halaman portal di http://192.168.4.1",
                                fontSize = 11.sp,
                                color = Color(0xFF757575)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // =====================================================================
        // BOTTOM ACTION BUTTONS: SET KONEKSI & EXIT
        // =====================================================================
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .fillMaxWidth(0.92f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // SET KONEKSI Button
            Row(
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                    .background(
                        color = Color(0xFFEF6C00),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        (context as? Activity)?.finish()
                    }
                    .padding(horizontal = 8.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_settings_bluetooth_24),
                    contentDescription = "Bluetooth",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SET KONEKSI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            // EXIT Button
            Row(
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                    .background(
                        color = Color(0xFFD32F2F),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        (context as? Activity)?.finishAffinity()
                    }
                    .padding(horizontal = 8.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Exit",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "EXIT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }

        // =====================================================================
        // DIALOG KONFIRMASI SWITCH MODE ESP32
        // =====================================================================
        if (showSwitchConfirmDialog) {
            Dialog(
                onDismissRequest = {
                    if (!isSwitching) showSwitchConfirmDialog = false
                }
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    if (currentMode == ConnectionMode.BLUETOOTH) Color(0xFFE0F2F1) else Color(0xFFE1F5FE),
                                    RoundedCornerShape(28.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (currentMode == ConnectionMode.BLUETOOTH)
                                        R.drawable.baseline_wifi_24
                                    else
                                        R.drawable.baseline_bluetooth_connected_24
                                ),
                                contentDescription = null,
                                tint = if (currentMode == ConnectionMode.BLUETOOTH) Color(0xFF00796B) else Color(0xFF0288D1),
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (currentMode == ConnectionMode.BLUETOOTH)
                                "Ganti JWS ke Mode WiFi?"
                            else
                                "Ganti JWS ke Mode Bluetooth?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF1B1B1B),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (currentMode == ConnectionMode.BLUETOOTH)
                                "ESP32 akan reboot dan menyalakan Hotspot WiFi 'JWS-RGB-P5' (Web Portal 192.168.4.1).\n\nKoneksi Bluetooth saat ini akan terputus."
                            else
                                "ESP32 akan reboot dan menyalakan Bluetooth 'JWS-RGB-P5'.\n\nJaringan WiFi ESP32 akan dimatikan.",
                            fontSize = 13.sp,
                            color = Color(0xFF424242),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        if (isSwitching) {
                            CircularProgressIndicator(
                                color = if (currentMode == ConnectionMode.BLUETOOTH) Color(0xFF00796B) else Color(0xFF0288D1),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Mengirim perintah alih mode...",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showSwitchConfirmDialog = false },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("BATAL", color = Color(0xFF616161), fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            isSwitching = true
                                            val cmd = if (currentMode == ConnectionMode.BLUETOOTH) "CSW0" else "CSW1"
                                            val success = BluetoothConnectionHolder.sendData(cmd)
                                            if (success) {
                                                Toast.makeText(
                                                    context,
                                                    if (currentMode == ConnectionMode.BLUETOOTH)
                                                        "ESP32 beralih ke Mode WiFi 'JWS-RGB-P5'. Silakan hubungkan WiFi HP Anda."
                                                    else
                                                        "ESP32 beralih ke Mode Bluetooth 'JWS-RGB-P5'.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                delay(800)
                                                BluetoothConnectionHolder.close()
                                                showSwitchConfirmDialog = false
                                                (context as? Activity)?.finish()
                                            } else {
                                                isSwitching = false
                                                Toast.makeText(
                                                    context,
                                                    "Gagal mengirim perintah. Pastikan JWS masih terhubung!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentMode == ConnectionMode.BLUETOOTH)
                                            Color(0xFF00796B)
                                        else
                                            Color(0xFF0288D1)
                                    )
                                ) {
                                    Text("YA, GANTI", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}

