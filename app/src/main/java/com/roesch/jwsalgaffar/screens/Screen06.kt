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
import com.roesch.jwsalgaffar.components.listBilanganBulatPositif
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.platform.LocalContext
import com.roesch.jwsalgaffar.components.SendingProgressDialog
import com.roesch.jwsalgaffar.utils.PrefManager

@Composable
fun Screen06(
    navController: NavHostController,
    bluetoothSocket: BluetoothSocket? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val options1 : List<String> = listBilanganBulatPositif()

    var isSending by remember { mutableStateOf(false) }
    var sendProgress by remember { mutableIntStateOf(0) }
    var sendTotal by remember { mutableIntStateOf(0) }
    var currentItemLabel by remember { mutableStateOf("") }

    var input1 by remember { mutableStateOf(PrefManager.getString(context, "s6_adzan", "5")) }
    var input2 by remember { mutableStateOf(PrefManager.getString(context, "s6_info", "12")) }
    var input3 by remember { mutableStateOf(PrefManager.getString(context, "s6_sholat", "10")) }
    var input4 by remember { mutableStateOf(PrefManager.getString(context, "s6_khutbah", "30")) }

    val datas = listOf(
        "NAD$input1",
        "NIN$input2",
        "NSO$input3",
        "NJM$input4",
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
                    judul = "DURASI WAKTU",
                    info = "Pilih Durasi waktu dari ADZAN/INFO JUMAT/SHOLAT/KHUTBAH Yang Diinginkan"
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
                        label = "ADZAN",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input1 = it
                            PrefManager.saveString(context, "s6_adzan", it)
                        }
                    )
                    DynamicSelectTextFieldSufix2(
                        selectedValue = input2,
                        options = options1,
                        label = "INFO",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input2 = it
                            PrefManager.saveString(context, "s6_info", it)
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
                        label = "SHOLAT",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input3 = it
                            PrefManager.saveString(context, "s6_sholat", it)
                        }
                    )
                    DynamicSelectTextFieldSufix2(
                        selectedValue = input4,
                        options = options1,
                        label = "KHUTBAH",
                        suffix = "MENIT",
                        onValueChangedEvent ={
                            input4 = it
                            PrefManager.saveString(context, "s6_khutbah", it)
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
                                                item.startsWith("NAD") -> "1. Durasi Adzan: $input1 Menit"
                                                item.startsWith("NIN") -> "2. Durasi Info: $input2 Menit"
                                                item.startsWith("NSO") -> "3. Durasi Sholat: $input3 Menit"
                                                item.startsWith("NJM") -> "4. Durasi Khutbah: $input4 Menit"
                                                else -> item
                                            }
                                        }

                                        isSending = false
                                        if (success) {
                                            Toast.makeText(context, "Berhasil! ${datas.size} Data Durasi terkirim.", Toast.LENGTH_SHORT).show()
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
                moduleTitle = "DURASI WAKTU",
                itemLabel = currentItemLabel
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Screen06Preview() {
    val navController = rememberNavController()
    Screen06(navController = navController)
}