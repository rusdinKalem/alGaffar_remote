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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.roesch.jwsalgaffar.components.DynamicSelectTextField2x
import com.roesch.jwsalgaffar.components.DynamicSelectTextFieldX
import com.roesch.jwsalgaffar.components.JudulInfo
import com.roesch.jwsalgaffar.components.TextItem
import com.roesch.jwsalgaffar.components.SendingProgressDialog
import com.roesch.jwsalgaffar.components.TombolHome
import com.roesch.jwsalgaffar.components.TombolKirim
import com.roesch.jwsalgaffar.components.listStep
import com.roesch.jwsalgaffar.components.listTriger
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import com.roesch.jwsalgaffar.utils.PrefManager
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun Screen09(
    navController: NavHostController,
    bluetoothSocket: BluetoothSocket? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val options1 : List<String> = listTriger()
    val options2 : List<String> = listStep()

    var isSending by remember { mutableStateOf(false) }
    var sendProgress by remember { mutableIntStateOf(0) }
    var sendTotal by remember { mutableIntStateOf(0) }
    var currentItemLabel by remember { mutableStateOf("") }

    var inputON1 by remember { mutableStateOf(PrefManager.getString(context, "s9_on1", "")) }
    var inputON2 by remember { mutableStateOf(PrefManager.getString(context, "s9_on2", "")) }
    var inputON3 by remember { mutableStateOf(PrefManager.getString(context, "s9_on3", "")) }
    var inputON4 by remember { mutableStateOf(PrefManager.getString(context, "s9_on4", "")) }
    var inputOF1 by remember { mutableStateOf(PrefManager.getString(context, "s9_of1", "")) }
    var inputOF2 by remember { mutableStateOf(PrefManager.getString(context, "s9_of2", "")) }
    var inputOF3 by remember { mutableStateOf(PrefManager.getString(context, "s9_of3", "")) }
    var inputOF4 by remember { mutableStateOf(PrefManager.getString(context, "s9_of4", "")) }
    var inputTN1 by remember { mutableStateOf(PrefManager.getString(context, "s9_tn1", "")) }
    var inputTN2 by remember { mutableStateOf(PrefManager.getString(context, "s9_tn2", "")) }
    var inputTN3 by remember { mutableStateOf(PrefManager.getString(context, "s9_tn3", "")) }
    var inputTN4 by remember { mutableStateOf(PrefManager.getString(context, "s9_tn4", "")) }
    var inputTF1 by remember { mutableStateOf(PrefManager.getString(context, "s9_tf1", "")) }
    var inputTF2 by remember { mutableStateOf(PrefManager.getString(context, "s9_tf2", "")) }
    var inputTF3 by remember { mutableStateOf(PrefManager.getString(context, "s9_tf3", "")) }
    var inputTF4 by remember { mutableStateOf(PrefManager.getString(context, "s9_tf4", "")) }

    val datas = mutableListOf<String>().apply {
        if (inputTN1.isNotEmpty() && inputON1.isNotEmpty()) add("TN1$inputTN1$inputON1")
        if (inputTF1.isNotEmpty() && inputOF1.isNotEmpty()) add("TF1$inputTF1$inputOF1")
        if (inputTN2.isNotEmpty() && inputON2.isNotEmpty()) add("TN2$inputTN2$inputON2")
        if (inputTF2.isNotEmpty() && inputOF2.isNotEmpty()) add("TF2$inputTF2$inputOF2")
        if (inputTN3.isNotEmpty() && inputON3.isNotEmpty()) add("TN3$inputTN3$inputON3")
        if (inputTF3.isNotEmpty() && inputOF3.isNotEmpty()) add("TF3$inputTF3$inputOF3")
        if (inputTN4.isNotEmpty() && inputON4.isNotEmpty()) add("TN4$inputTN4$inputON4")
        if (inputTF4.isNotEmpty() && inputOF4.isNotEmpty()) add("TF4$inputTF4$inputOF4")
    }
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
                    judul = "PENGATURAN SAKLAR RELAY",
                    info = "Pilih triger dan durasi waktu saklar Relay (R1 - R4) untuk otomatisasi saklar listrik masjid (Khusus modul JWS dengan dukungan modul Relay)."
                )
                //R1
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(end = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextItem("R1ON")
                    DynamicSelectTextFieldX(
                        selectedValue = inputTN1,
                        options = options1,
                        label = "TRIGER",
                        onValueChangedEvent = {
                            inputTN1 = it
                            PrefManager.saveString(context, "s9_tn1", it)
                        }
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputON1,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent = {
                            inputON1 = it
                            PrefManager.saveString(context, "s9_on1", it)
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(end = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextItem("R1OFF")
                    DynamicSelectTextFieldX(
                        selectedValue = inputTF1,
                        options = options1,
                        label = "TRIGER",
                        onValueChangedEvent = {
                            inputTF1 = it
                            PrefManager.saveString(context, "s9_tf1", it)
                        }
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputOF1,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent = {
                            inputOF1 = it
                            PrefManager.saveString(context, "s9_of1", it)
                        }
                    )
                }
                //R2
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(end = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextItem("R2ON")
                    DynamicSelectTextFieldX(
                        selectedValue = inputTN2,
                        options = options1,
                        label = "TRIGER",
                        onValueChangedEvent = {
                            inputTN2 = it
                            PrefManager.saveString(context, "s9_tn2", it)
                        }
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputON2,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent = {
                            inputON2 = it
                            PrefManager.saveString(context, "s9_on2", it)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(end = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextItem("R2OFF")
                    DynamicSelectTextFieldX(
                        selectedValue = inputTF2,
                        options = options1,
                        label = "TRIGER",
                        onValueChangedEvent = {
                            inputTF2 = it
                            PrefManager.saveString(context, "s9_tf2", it)
                        }
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputOF2,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent = {
                            inputOF2 = it
                            PrefManager.saveString(context, "s9_of2", it)
                        }
                    )
                }
                //R3
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(end = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextItem("R3ON")
                    DynamicSelectTextFieldX(
                        selectedValue = inputTN3,
                        options = options1,
                        label = "TRIGER",
                        onValueChangedEvent = {
                            inputTN3 = it
                            PrefManager.saveString(context, "s9_tn3", it)
                        }
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputON3,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent = {
                            inputON3 = it
                            PrefManager.saveString(context, "s9_on3", it)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(end = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextItem("R3OFF")
                    DynamicSelectTextFieldX(
                        selectedValue = inputTF3,
                        options = options1,
                        label = "TRIGER",
                        onValueChangedEvent = {
                            inputTF3 = it
                            PrefManager.saveString(context, "s9_tf3", it)
                        }
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputOF3,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent = {
                            inputOF3 = it
                            PrefManager.saveString(context, "s9_of3", it)
                        }
                    )
                }
                //R4
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(end = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextItem("R4ON")
                    DynamicSelectTextFieldX(
                        selectedValue = inputTN4,
                        options = options1,
                        label = "TRIGER",
                        onValueChangedEvent = {
                            inputTN4 = it
                            PrefManager.saveString(context, "s9_tn4", it)
                        }
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputON4,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent = {
                            inputON4 = it
                            PrefManager.saveString(context, "s9_on4", it)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(end = 10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextItem("R4OFF")
                    DynamicSelectTextFieldX(
                        selectedValue = inputTF4,
                        options = options1,
                        label = "TRIGER",
                        onValueChangedEvent = {
                            inputTF4 = it
                            PrefManager.saveString(context, "s9_tf4", it)
                        }
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputOF4,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent = {
                            inputOF4 = it
                            PrefManager.saveString(context, "s9_of4", it)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    if (isSending) return@clickable
                                    if (datas.isEmpty()) {
                                        Toast.makeText(context, "Silakan pilih setelan saklar relay terlebih dahulu!", Toast.LENGTH_SHORT).show()
                                        return@clickable
                                    }
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
                                                item.startsWith("TN1") -> "Relay 1 ON: ${item.removePrefix("TN1")}"
                                                item.startsWith("TF1") -> "Relay 1 OFF: ${item.removePrefix("TF1")}"
                                                item.startsWith("TN2") -> "Relay 2 ON: ${item.removePrefix("TN2")}"
                                                item.startsWith("TF2") -> "Relay 2 OFF: ${item.removePrefix("TF2")}"
                                                item.startsWith("TN3") -> "Relay 3 ON: ${item.removePrefix("TN3")}"
                                                item.startsWith("TF3") -> "Relay 3 OFF: ${item.removePrefix("TF3")}"
                                                item.startsWith("TN4") -> "Relay 4 ON: ${item.removePrefix("TN4")}"
                                                item.startsWith("TF4") -> "Relay 4 OFF: ${item.removePrefix("TF4")}"
                                                else -> item
                                            }
                                        }

                                        isSending = false
                                        if (success) {
                                            Toast.makeText(context, "Berhasil! ${datas.size} Data Relay terkirim.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Gagal mengirim data. Pastikan koneksi terhubung!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                    ) {
                        TombolKirim()
                    }
                    Spacer(Modifier.height(10.dp))
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
                moduleTitle = "SAKLAR RELAY",
                itemLabel = currentItemLabel
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Screen09Preview() {
    val navController = rememberNavController()
    Screen09(navController = navController)
}