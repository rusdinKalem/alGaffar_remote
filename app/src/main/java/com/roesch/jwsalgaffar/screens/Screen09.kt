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
import com.roesch.jwsalgaffar.components.TombolHome
import com.roesch.jwsalgaffar.components.TombolKirim
import com.roesch.jwsalgaffar.components.listStep
import com.roesch.jwsalgaffar.components.listTriger
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
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

    var inputON1 by remember { mutableStateOf("") }
    var inputON2 by remember { mutableStateOf("") }
    var inputON3 by remember { mutableStateOf("") }
    var inputON4 by remember { mutableStateOf("") }
    var inputOF1 by remember { mutableStateOf("") }
    var inputOF2 by remember { mutableStateOf("") }
    var inputOF3 by remember { mutableStateOf("") }
    var inputOF4 by remember { mutableStateOf("") }
    var inputTN1 by remember { mutableStateOf("") }
    var inputTN2 by remember { mutableStateOf("") }
    var inputTN3 by remember { mutableStateOf("") }
    var inputTN4 by remember { mutableStateOf("") }
    var inputTF1 by remember { mutableStateOf("") }
    var inputTF2 by remember { mutableStateOf("") }
    var inputTF3 by remember { mutableStateOf("") }
    var inputTF4 by remember { mutableStateOf("") }

    val datas = listOf(
        "TN1$inputTN1$inputON1",
        "TF1$inputTF1$inputOF1",
        "TN2$inputTN2$inputON2",
        "TF2$inputTF2$inputOF2",
        "TN3$inputTN3$inputON3",
        "TF3$inputTF3$inputOF3",
        "TN4$inputTN4$inputON4",
        "TF4$inputTF4$inputOF4"
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
                    judul = "PENGATURAN SAKLAR RELAY",
                    info = "Pilih triger dan durasi waktu saklar Relay (R1 - R4) untuk otomatisasi perangkat listrik masjid"
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
                        onValueChangedEvent ={inputTN1 = it}
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputON1,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent ={inputON1 = it}
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
                        onValueChangedEvent ={inputTF1 = it}
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputOF1,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent ={inputOF1 = it}
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
                        onValueChangedEvent ={inputTN2 = it}
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputON2,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent ={inputON2 = it}
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
                        onValueChangedEvent ={inputTF2 = it}
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputOF2,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent ={inputOF2 = it}
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
                        onValueChangedEvent ={inputTN3 = it}
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputON3,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent ={inputON3 = it}
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
                        onValueChangedEvent ={inputTF3 = it}
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputOF3,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent ={inputOF3 = it}
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
                        onValueChangedEvent ={inputTN4 = it}
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputON4,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent ={inputON4 = it}
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
                        onValueChangedEvent ={inputTF4 = it}
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    DynamicSelectTextField2x(
                        selectedValue = inputOF4,
                        options = options2,
                        label = "DUR",
                        onValueChangedEvent ={inputOF4 = it}
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
                                    coroutineScope.launch {
                                        val success = BluetoothConnectionHolder.sendDataList(datas)
                                        if (success) {
                                            Toast.makeText(context, "Data Relay berhasil dikirim!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Gagal mengirim data. Pastikan Bluetooth terhubung!", Toast.LENGTH_SHORT).show()
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
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Screen09Preview() {
    val navController = rememberNavController()
    Screen09(navController = navController)
}