package com.roesch.jwsalgaffar.screens

import android.bluetooth.BluetoothSocket
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.roesch.jwsalgaffar.components.DynamicSelectTextField
import com.roesch.jwsalgaffar.components.JudulInfo
import com.roesch.jwsalgaffar.components.TombolHome
import com.roesch.jwsalgaffar.components.TombolKirim
import com.roesch.jwsalgaffar.components.listType
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.roesch.jwsalgaffar.utils.PrefManager

@Composable
fun Screen01(navController: NavHostController, bluetoothSocket: BluetoothSocket? = null) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val options = listType()
    var type by remember { mutableStateOf(PrefManager.getString(context, "s1_type", "MASJID")) }
    var nama by remember { mutableStateOf(PrefManager.getString(context, "s1_nama", "")) }
    var alamat by remember { mutableStateOf(PrefManager.getString(context, "s1_alamat", "")) }

    val maxNamaChar = 40
    val maxAlamatChar = 50

    val typeCode = when (type) {
        "MASJID" -> "1"
        "MUSHOLLA" -> "2"
        "SURAU" -> "3"
        "LANGGAR" -> "4"
        else -> "1"
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.roesch.jwsalgaffar.R.drawable.bg02),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            alpha = 0.20f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            JudulInfo(
                judul = "TYPE, NAMA & ALAMAT MASJID",
                info = "Pilih tipe tempat sholat (Masjid/Musholla/Surau/Langgar). " +
                        "Input nama dan alamat tempat sholat yang akan ditampilkan pada modul JWS."
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dropdown Tipe Tempat Sholat
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DynamicSelectTextField(
                    selectedValue = type,
                    options = options,
                    label = "TYPE TEMPAT SHOLAT",
                    onValueChangedEvent = {
                        type = it
                        PrefManager.saveString(context, "s1_type", it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Input Nama Masjid / Tempat Sholat
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    value = nama,
                    onValueChange = {
                        if (it.length <= maxNamaChar) {
                            nama = it
                            PrefManager.saveString(context, "s1_nama", it)
                        }
                    },
                    label = { Text("NAMA TEMPAT SHOLAT") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.93f)
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp, top = 2.dp),
                text = "${nama.length}/$maxNamaChar",
                fontSize = 14.sp,
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Input Alamat Masjid (CMA)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    value = alamat,
                    onValueChange = {
                        if (it.length <= maxAlamatChar) {
                            alamat = it
                            PrefManager.saveString(context, "s1_alamat", it)
                        }
                    },
                    label = { Text("ALAMAT TEMPAT SHOLAT") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.93f)
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp, top = 2.dp),
                text = "${alamat.length}/$maxAlamatChar",
                fontSize = 14.sp,
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Tombol Kirim & Kembali
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            val commands = mutableListOf<String>()

                            // NMT: Tipe tempat sholat (1: Masjid, 2: Musholla, 3: Surau, 4: Langgar)
                            commands.add("NMT$typeCode")

                            // CMN: Nama tempat sholat
                            if (nama.isNotEmpty()) {
                                commands.add("CMN$nama")
                            }

                            // CMA: Alamat tempat sholat
                            if (alamat.isNotEmpty()) {
                                commands.add("CMA$alamat")
                            }

                            val success = com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder.sendDataList(commands)
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Data Masjid & Alamat berhasil dikirim!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Gagal mengirim data. Pastikan Bluetooth terhubung!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                ) {
                    TombolKirim()
                }

                Spacer(Modifier.height(20.dp))

                TombolHome(
                    name = "MENU UTAMA",
                    navController = navController
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Screen01Preview() {
    val navController = rememberNavController()
    Screen01(navController = navController)
}
