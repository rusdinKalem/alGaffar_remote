package com.roesch.jwsalgaffar.screens

import android.bluetooth.BluetoothSocket
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.roesch.jwsalgaffar.components.DynamicSelectTextField
import com.roesch.jwsalgaffar.components.DynamicSelectTextFieldSufix2
import com.roesch.jwsalgaffar.components.JudulInfo
import com.roesch.jwsalgaffar.components.TombolHome
import com.roesch.jwsalgaffar.components.TombolKirim
import com.roesch.jwsalgaffar.components.listBilanganBulat
import com.roesch.jwsalgaffar.components.listZona
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import android.widget.Toast

import androidx.compose.ui.platform.LocalContext
import com.roesch.jwsalgaffar.utils.PrefManager

@Composable
fun Screen02(
    navController: NavHostController,
    bluetoothSocket: BluetoothSocket? = null,
    currentLocation: LatLng = LatLng(-6.200000, 106.816666)
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val options1 = listBilanganBulat()

    val options2 = listZona()
    var input1 by remember { mutableStateOf(PrefManager.getString(context, "s2_ihtiyati", "")) }
    var input2 by remember { mutableStateOf(PrefManager.getString(context, "s2_mdpl", "")) }
    var input3 by remember { mutableStateOf("") }
    var input4 by remember { mutableStateOf("") }
    var input5 by remember { mutableStateOf(PrefManager.getString(context, "s2_zona", "Waktu Indonesia Barat")) }

    val lat = currentLocation.latitude.toString()
    val long = currentLocation.longitude.toString()

    val zona = when(input5){
        "Waktu Indonesia Barat" ->  7
        "Waktu Indonesia Tengah" ->  8
        "Waktu Indonesia Timur" ->  9
        else -> ""
    }

    val datas = listOf(
        "NIH$input1",
        "NAL$input2",
        "NLA$lat",
        "NLO$long",
        "NTZ$zona"
    )
    Column(
        Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ){
                JudulInfo(
                    judul = "IHTIYATI & KOORDINAT TEMPAT SHOLAT",
                    info = "Input Ihtiyati (Waktu Kehati-hatian/Toleransi Waktu), dan MDPL (Meter Di atas Permukaan Laut) serta Time Zone di Lokasi Tempat Ibadah dimaksud.\n"+
                            "Untuk Koordinat(Latitude/Longitude) akan diisi otomatis jika Layanan Lokasi pada HP diaktifkan"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    DynamicSelectTextFieldSufix2(
                        selectedValue = input1,
                        options = options1,
                        label = "IHTIYATI",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input1 = it
                            PrefManager.saveString(context, "s2_ihtiyati", it)
                        }
                    )
                    OutlinedTextField(
                        value = input2,
                        onValueChange = {
                            input2 = it
                            PrefManager.saveString(context, "s2_mdpl", it)
                        },
                        label = { Text("MDPL")},
                        modifier = Modifier
                            .width(176.dp)
                    )


                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {


                    OutlinedTextField(
                        value = lat,
                        onValueChange = { input3 = it },
                        label = { Text("LATITUDE") },
                        modifier = Modifier
                            .width(176.dp)
                    )

                    OutlinedTextField(
                        value = long,
                        onValueChange = { input4 = it },
                        label = { Text("LONGITUDE") },
                        modifier = Modifier
                            .width(176.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DynamicSelectTextField(
                        selectedValue = input5,
                        options = options2,
                        label = "ZONA WAKTU",
                        onValueChangedEvent ={
                            input5 = it
                            PrefManager.saveString(context, "s2_zona", it)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    coroutineScope.launch {
                                        val success = BluetoothConnectionHolder.sendDataList(datas)
                                        if (success) {
                                            Toast.makeText(context, "Data Koordinat & Ihtiyati berhasil dikirim!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Gagal mengirim data. Pastikan Bluetooth terhubung!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                    ) {
                        TombolKirim()
                    }
                    Spacer(Modifier.height(20.dp))
                    TombolHome(
                        "MENU UTAMA",
                        navController
                    )
                }

                Spacer(Modifier.height(190.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Created by @Roesch ",
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(20.dp))

            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Screen02Preview() {
    val navController = rememberNavController()
    Screen02(navController = navController)
}