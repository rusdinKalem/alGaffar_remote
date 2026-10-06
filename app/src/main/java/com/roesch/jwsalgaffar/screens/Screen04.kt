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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.roesch.jwsalgaffar.components.DynamicSelectTextFieldSufix2
import com.roesch.jwsalgaffar.components.JudulInfo
import com.roesch.jwsalgaffar.components.TombolHome
import com.roesch.jwsalgaffar.components.TombolKirim
import com.roesch.jwsalgaffar.components.listBilanganBulat
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast

import androidx.compose.ui.platform.LocalContext
import com.roesch.jwsalgaffar.utils.PrefManager

@Composable
fun Screen04(
    navController: NavHostController,
    bluetoothSocket: BluetoothSocket? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val options1 : List<String> = listBilanganBulat()

    var input1 by remember { mutableStateOf(PrefManager.getString(context, "s4_hijriyah", "")) }
    var input2 by remember { mutableStateOf(PrefManager.getString(context, "s4_subuh", "")) }
    var input3 by remember { mutableStateOf(PrefManager.getString(context, "s4_dzuhur", "")) }
    var input4 by remember { mutableStateOf(PrefManager.getString(context, "s4_ashar", "")) }
    var input5 by remember { mutableStateOf(PrefManager.getString(context, "s4_maghrib", "")) }
    var input6 by remember { mutableStateOf(PrefManager.getString(context, "s4_isya", "")) }

    val datas = listOf(
        "NCH$input1",
        "NIS$input2",
        "NIL$input3",
        "NIA$input4",
        "NIM$input5",
        "NII$input6",
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
                    judul = "KOREKSI HIJRIYAH DAN WAKTU SHOLAT",
                    info = "Pilih nilai koreksi tanggal Hijriyah dan Waktu Sholat Yang Diinginkan"
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
                        label = "HIJRIYAH",
                        suffix = "HARI",
                        onValueChangedEvent ={
                            input1 = it
                            PrefManager.saveString(context, "s4_hijriyah", it)
                        }
                    )
                    DynamicSelectTextFieldSufix2(
                        selectedValue = input2,
                        options = options1,
                        label = "SUBUH",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input2 = it
                            PrefManager.saveString(context, "s4_subuh", it)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    DynamicSelectTextFieldSufix2(
                        selectedValue = input3,
                        options = options1,
                        label = "DZUHUR",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input3 = it
                            PrefManager.saveString(context, "s4_dzuhur", it)
                        }
                    )
                    DynamicSelectTextFieldSufix2(
                        selectedValue = input4,
                        options = options1,
                        label = "ASHAR",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input4 = it
                            PrefManager.saveString(context, "s4_ashar", it)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    DynamicSelectTextFieldSufix2(
                        selectedValue = input5,
                        options = options1,
                        label = "MAGHRIB",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input5 = it
                            PrefManager.saveString(context, "s4_maghrib", it)
                        }
                    )
                    DynamicSelectTextFieldSufix2(
                        selectedValue = input6,
                        options = options1,
                        label = "ISYA",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input6 = it
                            PrefManager.saveString(context, "s4_isya", it)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
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
                                            Toast.makeText(context, "Data Koreksi berhasil dikirim!", Toast.LENGTH_SHORT).show()
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
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Screen04Preview() {
    val navController = rememberNavController()
    Screen04(navController = navController)
}