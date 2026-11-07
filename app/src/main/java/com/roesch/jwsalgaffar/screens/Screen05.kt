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
import com.roesch.jwsalgaffar.components.DynamicSelectTextFieldSufix
import com.roesch.jwsalgaffar.components.DynamicSelectTextFieldSufix2
import com.roesch.jwsalgaffar.components.JudulInfo
import com.roesch.jwsalgaffar.components.TombolHome
import com.roesch.jwsalgaffar.components.TombolKirim
import com.roesch.jwsalgaffar.components.listBilanganBulatPositif
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.LocalContext
import com.roesch.jwsalgaffar.components.SendingProgressDialog
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import com.roesch.jwsalgaffar.utils.PrefManager

@Composable
fun Screen05(
    navController: NavHostController,
    bluetoothSocket: BluetoothSocket? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val options1 = listBilanganBulatPositif()

    var isSending by remember { mutableStateOf(false) }
    var sendProgress by remember { mutableIntStateOf(0) }
    var sendTotal by remember { mutableIntStateOf(0) }
    var currentItemLabel by remember { mutableStateOf("") }

    var input1 by remember { mutableStateOf(PrefManager.getString(context, "s5_subuh", "15")) }
    var input2 by remember { mutableStateOf(PrefManager.getString(context, "s5_dzuhur", "10")) }
    var input3 by remember { mutableStateOf(PrefManager.getString(context, "s5_ashar", "10")) }
    var input4 by remember { mutableStateOf(PrefManager.getString(context, "s5_maghrib", "5")) }
    var input5 by remember { mutableStateOf(PrefManager.getString(context, "s5_isya", "10")) }

    val datas = listOf(
        "NI1$input1",
        "NI4$input2",
        "NI5$input3",
        "NI6$input4",
        "NI7$input5",
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
                    judul = "WAKTU TUNGGU IQOMAT SHOLAT",
                    info = "Pilih nilai dari Waktu Tunggu Iqomat Sholat Yang Diinginkan"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    DynamicSelectTextFieldSufix(
                        selectedValue = input1,
                        options = options1,
                        label = "SUBUH",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input1 = it
                            PrefManager.saveString(context, "s5_subuh", it)
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
                        selectedValue = input2,
                        options = options1,
                        label = "DZUHUR",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input2 = it
                            PrefManager.saveString(context, "s5_dzuhur", it)
                        }
                    )
                    DynamicSelectTextFieldSufix2(
                        selectedValue = input3,
                        options = options1,
                        label = "ASHAR",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input3 = it
                            PrefManager.saveString(context, "s5_ashar", it)
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
                        selectedValue = input4,
                        options = options1,
                        label = "MAGHRIB",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input4 = it
                            PrefManager.saveString(context, "s5_maghrib", it)
                        }
                    )
                    DynamicSelectTextFieldSufix2(
                        selectedValue = input5,
                        options = options1,
                        label = "ISYA",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input5 = it
                            PrefManager.saveString(context, "s5_isya", it)
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
                                    if (isSending) return@clickable
                                    coroutineScope.launch {
                                        isSending = true
                                        sendTotal = datas.size
                                        sendProgress = 0

                                        val success = BluetoothConnectionHolder.sendDataList(
                                            dataList = datas,
                                            delayBetweenMs = 800L
                                        ) { current, total, item ->
                                            sendProgress = current
                                            sendTotal = total
                                            currentItemLabel = when {
                                                item.startsWith("NI1") -> "1. Subuh: $input1 Menit"
                                                item.startsWith("NI4") -> "2. Dzuhur: $input2 Menit"
                                                item.startsWith("NI5") -> "3. Ashar: $input3 Menit"
                                                item.startsWith("NI6") -> "4. Maghrib: $input4 Menit"
                                                item.startsWith("NI7") -> "5. Isya: $input5 Menit"
                                                else -> item
                                            }
                                        }

                                        isSending = false
                                        if (success) {
                                            Toast.makeText(context, "Berhasil! ${datas.size} Waktu Iqomat terkirim.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Gagal mengirim data. Pastikan koneksi terhubung!", Toast.LENGTH_SHORT).show()
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

            SendingProgressDialog(
                show = isSending,
                current = sendProgress,
                total = sendTotal,
                moduleTitle = "WAKTU IQOMAT",
                itemLabel = currentItemLabel
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Screen05Preview() {
    val navController = rememberNavController()
    Screen05(navController = navController)
}