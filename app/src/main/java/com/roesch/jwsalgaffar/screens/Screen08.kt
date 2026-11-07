@file:Suppress("UNUSED_EXPRESSION")

package com.roesch.jwsalgaffar.screens

import android.bluetooth.BluetoothSocket
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.roesch.jwsalgaffar.components.DynamicSelectTextField
import com.roesch.jwsalgaffar.components.DynamicSelectTextFieldSufix2
import com.roesch.jwsalgaffar.components.JudulInfo
import com.roesch.jwsalgaffar.components.SendingProgressDialog
import com.roesch.jwsalgaffar.components.TextSwitch
import com.roesch.jwsalgaffar.components.TombolHome
import com.roesch.jwsalgaffar.components.TombolKirim
import com.roesch.jwsalgaffar.components.listMode
import com.roesch.jwsalgaffar.components.listWaktuMalam
import com.roesch.jwsalgaffar.components.listWaktuPagi
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.widget.Toast

import androidx.compose.ui.platform.LocalContext
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import com.roesch.jwsalgaffar.utils.PrefManager

@Composable
fun Screen08(navController: NavHostController, bluetoothSocket: BluetoothSocket? = null) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var isSending by remember { mutableStateOf(false) }
    var sendProgress by remember { mutableIntStateOf(0) }
    var sendTotal by remember { mutableIntStateOf(0) }
    var currentItemLabel by remember { mutableStateOf("") }

    val options1 = listMode()
    val options2 = listWaktuMalam()
    val options3 = listWaktuPagi()

    var checked1 by remember { mutableStateOf(PrefManager.getBoolean(context, "s8_sw1", true)) }
    var checked2 by remember { mutableStateOf(PrefManager.getBoolean(context, "s8_sw2", true)) }
    var checked3 by remember { mutableStateOf(PrefManager.getBoolean(context, "s8_sw3", true)) }
    var checked4 by remember { mutableStateOf(PrefManager.getBoolean(context, "s8_sw4", true)) }
    var sliderPosition by remember { mutableFloatStateOf(PrefManager.getFloat(context, "s8_sl1", 0.35f)) }
    var sliderPosition2 by remember { mutableFloatStateOf(PrefManager.getFloat(context, "s8_sl2", 0.30f)) }
    var theme by remember { mutableStateOf(PrefManager.getString(context, "s8_theme", "NORMAL")) }
    var start by remember { mutableStateOf(PrefManager.getString(context, "s8_start", "10")) }
    var finish by remember { mutableStateOf(PrefManager.getString(context, "s8_finish", "4")) }

    val mode = when(theme){
        "NORMAL" ->  1
        "JAM BESAR" ->  2
        "PADAM" ->  3
        else -> 1
    }


    val kali1 = 255
    val kali2 = 150
    val dataSlider1 = (sliderPosition * kali1).toInt().coerceIn(15, 255)
    val dataSlider2 = (sliderPosition2 * kali2).toInt().coerceIn(10, 150)

    val buzzer = when(checked1){
        true -> 1
        else -> 0
    }
    val imsak = when(checked2){
        true -> 1
        else -> 0
    }
    val terbit = when(checked3){
        true -> 1
        else -> 0
    }
    val dhuha = when(checked4){
        true -> 1
        else -> 0
    }
    val datas = mutableListOf(
        "NBZ$buzzer",
        "NSI$imsak",
        "NST$terbit",
        "NSU$dhuha",
        "NBL$dataSlider1",
        "NRT$dataSlider2",
        "NHS$mode"
    ).apply {
        if (theme != "NORMAL" && start.isNotEmpty() && finish.isNotEmpty()) {
            add("NHM$start")
            add("NHH$finish")
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            JudulInfo(
                judul = "SETTINGS TOOLS",
                info = "Pilih tombol aktifasi pengingat penunjuk waktu, kecerahan LED, kecepatan running text, serta tampilan layar dimalam hari."
            )

            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

            )   {
                TextSwitch("SUARA BUZZER/PENGINGAT")

                androidx.compose.material3.Switch(
                    checked = checked1,
                    onCheckedChange = {
                        checked1 = it
                        PrefManager.saveBoolean(context, "s8_sw1", it)
                    },
                    thumbContent = if (checked1) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                )
                        }
                    } else {
                        null
                    }
                )

            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

            )   {
                TextSwitch("TAMPILAN IMSAK")

                androidx.compose.material3.Switch(
                    checked = checked2,
                    onCheckedChange = {
                        checked2 = it
                        PrefManager.saveBoolean(context, "s8_sw2", it)
                    },
                    thumbContent = if (checked2) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,

                                )
                        }
                    } else {
                        null
                    }
                )

            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

            )   {
                TextSwitch("TAMPILAN TERBIT")

                androidx.compose.material3.Switch(
                    checked = checked3,
                    onCheckedChange = {
                        checked3 = it
                        PrefManager.saveBoolean(context, "s8_sw3", it)
                    },
                    thumbContent = if (checked3) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,

                                )
                        }
                    } else {
                        null
                    }
                )

            }
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

            )   {
                TextSwitch("TAMPILAN DHUHA")

                androidx.compose.material3.Switch(
                    checked = checked4,
                    onCheckedChange = {
                        checked4 = it
                        PrefManager.saveBoolean(context, "s8_sw4", it)
                    },
                    thumbContent = if (checked4) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,

                                )
                        }
                    } else {
                        null
                    }
                )

            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),

            ) {
                Column {
                    Text(text = "KECERAHAN LED = $dataSlider1", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                    androidx.compose.material3.Slider(
                        value = sliderPosition,
                        onValueChange = {
                            sliderPosition = it
                            PrefManager.saveFloat(context, "s8_sl1", it)
                        }
                    )

                }
                Spacer(modifier = Modifier.height(10.dp))
                Column {
                    Text(text = "KECEPATAN RUNNING TEXT = $dataSlider2", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                    androidx.compose.material3.Slider(
                        value = sliderPosition2,
                        onValueChange = {
                            sliderPosition2 = it
                            PrefManager.saveFloat(context, "s8_sl2", it)
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                DynamicSelectTextField(
                    selectedValue = theme,
                    options = options1,
                    label = "MODE MALAM",
                    onValueChangedEvent ={
                        theme = it
                        PrefManager.saveString(context, "s8_theme", it)
                    }
                )
            }
            if (theme != "NORMAL"){

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    DynamicSelectTextFieldSufix2(
                        selectedValue = start,
                        options = options2,
                        label = "START",
                        suffix = "MALAM",
                        onValueChangedEvent ={
                            start = it
                            PrefManager.saveString(context, "s8_start", it)
                        }
                    )
                    DynamicSelectTextFieldSufix2(
                        selectedValue = finish,
                        options = options3,
                        label = "FINISH",
                        suffix = "PAGI",
                        onValueChangedEvent ={
                            finish = it
                            PrefManager.saveString(context, "s8_finish", it)
                        }
                    )
                }
            } else {
                null
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
                                            item.startsWith("NBZ") -> "1. Buzzer: ${if (buzzer == 1) "ON" else "OFF"}"
                                            item.startsWith("NSI") -> "2. Imsak: ${if (imsak == 1) "ON" else "OFF"}"
                                            item.startsWith("NST") -> "3. Terbit: ${if (terbit == 1) "ON" else "OFF"}"
                                            item.startsWith("NSU") -> "4. Dhuha: ${if (dhuha == 1) "ON" else "OFF"}"
                                            item.startsWith("NBL") -> "5. Kecerahan: $dataSlider1"
                                            item.startsWith("NRT") -> "6. Kecepatan Runtext: $dataSlider2"
                                            item.startsWith("NHS") -> "7. Mode Malam: $theme"
                                            item.startsWith("NHM") -> "8. Start Malam: Jam $start"
                                            item.startsWith("NHH") -> "9. Finish Pagi: Jam $finish"
                                            else -> item
                                        }
                                    }

                                    isSending = false
                                    if (success) {
                                        Toast.makeText(context, "Berhasil! ${datas.size} Pengaturan Tools terkirim.", Toast.LENGTH_SHORT).show()
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
                Spacer(Modifier.height(30.dp))
            }
        }

        SendingProgressDialog(
            show = isSending,
            current = sendProgress,
            total = sendTotal,
            moduleTitle = "PENGATURAN TOOLS",
            itemLabel = currentItemLabel
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Screen08Preview() {
    val navController = rememberNavController()
    Screen08(navController = navController)
}

