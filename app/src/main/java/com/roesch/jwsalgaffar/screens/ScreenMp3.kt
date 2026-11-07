package com.roesch.jwsalgaffar.screens

import android.bluetooth.BluetoothSocket
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.roesch.jwsalgaffar.R
import com.roesch.jwsalgaffar.components.JudulInfo
import com.roesch.jwsalgaffar.components.SendingProgressDialog
import com.roesch.jwsalgaffar.components.TombolHome
import com.roesch.jwsalgaffar.ui.theme.JWSAlGaffarTheme
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import com.roesch.jwsalgaffar.utils.PrefManager
import kotlinx.coroutines.launch

data class SholatSlot(
    val name: String,
    val slotCode: Int,
    val defaultTartilMin: Int = 20,
    val defaultTarhimSec: Int = 390,
    val defaultTartilTrack: Int = 1,
    val defaultTarhimTrack: Int = 1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenMp3(
    navController: NavHostController,
    bluetoothSocket: BluetoothSocket? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    var isSendingSchedule by remember { mutableStateOf(false) }
    var scheduleProgress by remember { mutableIntStateOf(0) }
    var scheduleTotal by remember { mutableIntStateOf(0) }
    var scheduleItemLabel by remember { mutableStateOf("") }
    var scheduleDialogTitle by remember { mutableStateOf("JADWAL MP3") }
    var showApplyAllDialog by remember { mutableStateOf(false) }

    fun sendCommand(cmd: String) {
        coroutineScope.launch {
            val success = BluetoothConnectionHolder.sendData(cmd)
            if (!success) {
                Toast.makeText(context, "Bluetooth tidak terhubung!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Master Switch & Volume
    var mp3Enabled by remember { mutableStateOf(PrefManager.getBoolean(context, "mp3_enabled", true)) }
    var volume by remember { mutableFloatStateOf(PrefManager.getFloat(context, "mp3_volume", 25f)) }

    // Manual Playback controls
    var directTartilTrack by remember { mutableStateOf(PrefManager.getString(context, "mp3_d_tartil", "1")) }
    var directTarhimTrack by remember { mutableStateOf(PrefManager.getString(context, "mp3_d_tarhim", "1")) }
    var directCustomFolder by remember { mutableStateOf(PrefManager.getString(context, "mp3_d_folder", "1")) }
    var directCustomTrack by remember { mutableStateOf(PrefManager.getString(context, "mp3_d_track", "1")) }

    var isPlayingTartil by remember { mutableStateOf(false) }
    var isPlayingTarhim by remember { mutableStateOf(false) }
    var isPlayingCustom by remember { mutableStateOf(false) }

    // Schedule Slot Settings
    val slots = remember {
        listOf(
            SholatSlot("Subuh", 1, defaultTartilMin = 20, defaultTarhimSec = 390, 1, 1),
            SholatSlot("Dzuhur", 4, defaultTartilMin = 15, defaultTarhimSec = 390, 2, 2),
            SholatSlot("Ashar", 5, defaultTartilMin = 15, defaultTarhimSec = 390, 3, 3),
            SholatSlot("Maghrib", 6, defaultTartilMin = 15, defaultTarhimSec = 390, 4, 4),
            SholatSlot("Isya", 7, defaultTartilMin = 15, defaultTarhimSec = 390, 5, 5),
            SholatSlot("Jum'at", 8, defaultTartilMin = 30, defaultTarhimSec = 390, 6, 6)
        )
    }

    var selectedSlotIndex by remember { mutableIntStateOf(0) }
    val currentSlot = slots[selectedSlotIndex]

    val initSlot = currentSlot
    val initTartilMinStr = PrefManager.getString(context, "mp3_tartil_min_${initSlot.slotCode}", initSlot.defaultTartilMin.toString())
    val initTarhimMinStr = PrefManager.getString(context, "mp3_tarhim_min_${initSlot.slotCode}", (initSlot.defaultTarhimSec / 60).toString())
    val initTarhimSecStr = PrefManager.getString(context, "mp3_tarhim_sec_${initSlot.slotCode}", (initSlot.defaultTarhimSec % 60).toString())
    val initTotalTarhimSec = (initTarhimMinStr.toIntOrNull() ?: 0) * 60 + (initTarhimSecStr.toIntOrNull() ?: 0)

    var isTartilActive by remember {
        mutableStateOf(PrefManager.getBoolean(context, "mp3_tartil_act_${initSlot.slotCode}", initTartilMinStr != "0") && initTartilMinStr != "0")
    }
    var isTarhimActive by remember {
        mutableStateOf(PrefManager.getBoolean(context, "mp3_tarhim_act_${initSlot.slotCode}", initTotalTarhimSec > 0) && initTotalTarhimSec > 0)
    }

    var tartilMinutes by remember {
        val last = PrefManager.getString(context, "mp3_tartil_last_${initSlot.slotCode}", initSlot.defaultTartilMin.toString())
        mutableStateOf(if (initTartilMinStr != "0") initTartilMinStr else (if (last != "0") last else initSlot.defaultTartilMin.toString()))
    }
    var tarhimMinutes by remember {
        val lastM = PrefManager.getString(context, "mp3_tarhim_last_m_${initSlot.slotCode}", (initSlot.defaultTarhimSec / 60).toString())
        mutableStateOf(if (initTotalTarhimSec > 0) initTarhimMinStr else (if (lastM != "0") lastM else (initSlot.defaultTarhimSec / 60).toString()))
    }
    var tarhimSeconds by remember {
        val lastS = PrefManager.getString(context, "mp3_tarhim_last_s_${initSlot.slotCode}", (initSlot.defaultTarhimSec % 60).toString())
        mutableStateOf(if (initTotalTarhimSec > 0) initTarhimSecStr else lastS)
    }
    var tartilTrackNumber by remember { mutableStateOf(PrefManager.getString(context, "mp3_tartil_tr_${initSlot.slotCode}", initSlot.defaultTartilTrack.toString())) }
    var tarhimTrackNumber by remember { mutableStateOf(PrefManager.getString(context, "mp3_tarhim_tr_${initSlot.slotCode}", initSlot.defaultTarhimTrack.toString())) }

    // Update fields when slot changes
    fun onSlotChanged(index: Int) {
        selectedSlotIndex = index
        val slot = slots[index]

        val savedTartilMin = PrefManager.getString(context, "mp3_tartil_min_${slot.slotCode}", slot.defaultTartilMin.toString())
        val savedTarhimMin = PrefManager.getString(context, "mp3_tarhim_min_${slot.slotCode}", (slot.defaultTarhimSec / 60).toString())
        val savedTarhimSec = PrefManager.getString(context, "mp3_tarhim_sec_${slot.slotCode}", (slot.defaultTarhimSec % 60).toString())
        val totalSec = (savedTarhimMin.toIntOrNull() ?: 0) * 60 + (savedTarhimSec.toIntOrNull() ?: 0)

        val tartilAct = PrefManager.getBoolean(context, "mp3_tartil_act_${slot.slotCode}", savedTartilMin != "0") && savedTartilMin != "0"
        val tarhimAct = PrefManager.getBoolean(context, "mp3_tarhim_act_${slot.slotCode}", totalSec > 0) && totalSec > 0

        isTartilActive = tartilAct
        isTarhimActive = tarhimAct

        val lastTartil = PrefManager.getString(context, "mp3_tartil_last_${slot.slotCode}", slot.defaultTartilMin.toString())
        tartilMinutes = if (savedTartilMin != "0") savedTartilMin else (if (lastTartil != "0") lastTartil else slot.defaultTartilMin.toString())

        val lastTarhimM = PrefManager.getString(context, "mp3_tarhim_last_m_${slot.slotCode}", (slot.defaultTarhimSec / 60).toString())
        val lastTarhimS = PrefManager.getString(context, "mp3_tarhim_last_s_${slot.slotCode}", (slot.defaultTarhimSec % 60).toString())
        tarhimMinutes = if (totalSec > 0) savedTarhimMin else (if (lastTarhimM != "0" || lastTarhimS != "0") lastTarhimM else (slot.defaultTarhimSec / 60).toString())
        tarhimSeconds = if (totalSec > 0) savedTarhimSec else (if (lastTarhimM != "0" || lastTarhimS != "0") lastTarhimS else (slot.defaultTarhimSec % 60).toString())

        tartilTrackNumber = PrefManager.getString(context, "mp3_tartil_tr_${slot.slotCode}", slot.defaultTartilTrack.toString())
        tarhimTrackNumber = PrefManager.getString(context, "mp3_tarhim_tr_${slot.slotCode}", slot.defaultTarhimTrack.toString())
    }

    val tartilMinOptions = listOf("5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "60")
    val tarhimMinOptions = (0..10).map { it.toString() }
    val tarhimSecOptions = (0..59).map { it.toString() }
    val trackOptions = (1..150).map { it.toString() }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg02),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            alpha = 0.20f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            JudulInfo(
                judul = "PENGATURAN MP3 & TARTIL",
                info = "Pengaturan pemutaran Tartil dan Tarhim otomatis sebelum sholat, pengaturan volume audio, serta kontrol pemutaran MP3 secara langsung."
            )

            // ---------------------------------------------------------
            // CARD 1: KONTROL UTAMA & VOLUME
            // ---------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.93f)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCCCCC))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "KONTROL UTAMA MP3",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1B1B1B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Switch Master MP3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fitur MP3 Otomatis",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = mp3Enabled,
                            onCheckedChange = {
                                mp3Enabled = it
                                PrefManager.saveBoolean(context, "mp3_enabled", it)
                                val cmd = if (it) "NPM1" else "NPM0"
                                sendCommand(cmd)
                                Toast.makeText(
                                    context,
                                    if (it) "MP3 Diaktifkan" else "MP3 Dimatikan",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            thumbContent = if (mp3Enabled) {
                                { Icon(Icons.Filled.Check, contentDescription = null) }
                            } else null
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Volume Slider
                    Text(
                        text = "Volume Suara: ${volume.toInt()} / 30",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Slider(
                        value = volume,
                        onValueChange = {
                            volume = it
                            PrefManager.saveFloat(context, "mp3_volume", it)
                        },
                        valueRange = 0f..30f,
                        steps = 29,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.Center
                    ) {
                        Button(
                            onClick = {
                                val volInt = volume.toInt()
                                sendCommand("NPV$volInt")
                                Toast.makeText(context, "Volume diset ke $volInt", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_volume_up_24),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SAVE VOLUME")
                        }
                    }
                }
            }

            // ---------------------------------------------------------
            // CARD 2: KONTROL MANUAL (PUTAR MP3 LANGSUNG & TEST AUDIO)
            // ---------------------------------------------------------

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.93f)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCCCCC))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PUTAR MP3 LANGSUNG",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1B1B1B)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Play Tartil Now (Folder 01)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = "File - $directTartilTrack",
                                onValueChange = {},
                                label = { Text("Tartil (Fldr 01)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                trackOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text("File - $opt") },
                                        onClick = {
                                            directTartilTrack = opt
                                            PrefManager.saveString(context, "mp3_d_tartil", opt)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        if (!isPlayingTartil) {
                            Button(
                                onClick = {
                                    sendCommand("PT$directTartilTrack")
                                    isPlayingTartil = true
                                    isPlayingTarhim = false
                                    isPlayingCustom = false
                                    Toast.makeText(
                                        context,
                                        "Memutar Tartil Track $directTartilTrack",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PLAY", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    sendCommand("PS")
                                    isPlayingTartil = false
                                    Toast.makeText(context, "Audio Dihentikan", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Icon(Icons.Rounded.Close, contentDescription = "Stop", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("STOP", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Play Tarhim Now (Folder 02)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = "File - $directTarhimTrack",
                                onValueChange = {},
                                label = { Text("Tarhim (Fldr 02)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                trackOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text("File - $opt") },
                                        onClick = {
                                            directTarhimTrack = opt
                                            PrefManager.saveString(context, "mp3_d_tarhim", opt)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        if (!isPlayingTarhim) {
                            Button(
                                onClick = {
                                    sendCommand("PH$directTarhimTrack")
                                    isPlayingTarhim = true
                                    isPlayingTartil = false
                                    isPlayingCustom = false
                                    Toast.makeText(
                                        context,
                                        "Memutar Tarhim Track $directTarhimTrack",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PLAY", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    sendCommand("PS")
                                    isPlayingTarhim = false
                                    Toast.makeText(context, "Audio Dihentikan", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Icon(Icons.Rounded.Close, contentDescription = "Stop", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("STOP", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Play Custom Folder/Track
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = directCustomFolder,
                            onValueChange = {
                                directCustomFolder = it
                                PrefManager.saveString(context, "mp3_d_folder", it)
                            },
                            label = { Text("Fldr") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = directCustomTrack,
                            onValueChange = {
                                directCustomTrack = it
                                PrefManager.saveString(context, "mp3_d_track", it)
                            },
                            label = { Text("Trk") },
                            modifier = Modifier.weight(1f)
                        )

                        if (!isPlayingCustom) {
                            Button(
                                onClick = {
                                    sendCommand("P$directCustomFolder,$directCustomTrack")
                                    isPlayingCustom = true
                                    isPlayingTartil = false
                                    isPlayingTarhim = false
                                    Toast.makeText(
                                        context,
                                        "Memutar Folder $directCustomFolder Track $directCustomTrack",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PLAY", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    sendCommand("PS")
                                    isPlayingCustom = false
                                    Toast.makeText(context, "Audio Dihentikan", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                modifier = Modifier.height(52.dp)
                            ) {
                                Icon(Icons.Rounded.Close, contentDescription = "Stop", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("STOP", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ---------------------------------------------------------
            // CARD 3: JADWAL OTOMATIS TARTIL & TARHIM PER WAKTU SHOLAT
            // ---------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.93f)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                border = BorderStroke(1.dp, Color(0xFFCCCCCC))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "JADWAL OTOMATIS PER SHOLAT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1B1B1B)
                    )
                    // Pilih Sholat Dropdown
                    var sholatExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = sholatExpanded,
                        onExpandedChange = { sholatExpanded = !sholatExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = "${currentSlot.name.uppercase()}",
                            onValueChange = {},
                            label = { Text("PILIH WAKTU SHOLAT") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(sholatExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = sholatExpanded,
                            onDismissRequest = { sholatExpanded = false }
                        ) {
                            slots.forEachIndexed { idx, slot ->
                                DropdownMenuItem(
                                    text = { Text("${slot.name.uppercase()}") },
                                    onClick = {
                                        onSlotChanged(idx)
                                        sholatExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ---------------------------------------------------------
                    // TOMBOL CEPAT MODE PEMUTARAN
                    // ---------------------------------------------------------
                    val isBothActive = isTartilActive && isTarhimActive
                    val isTartilOnly = isTartilActive && !isTarhimActive
                    val isTarhimOnly = !isTartilActive && isTarhimActive
                    val isBothDisabled = !isTartilActive && !isTarhimActive

                    Text(
                        text = "TOMBOL CEPAT MODE AUDIO (${currentSlot.name.uppercase()})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF455A64)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                isTartilActive = true
                                isTarhimActive = true
                                if (tartilMinutes == "0") tartilMinutes = currentSlot.defaultTartilMin.toString()
                                val curTot = (tarhimMinutes.toIntOrNull() ?: 0) * 60 + (tarhimSeconds.toIntOrNull() ?: 0)
                                if (curTot == 0) {
                                    tarhimMinutes = (currentSlot.defaultTarhimSec / 60).toString()
                                    tarhimSeconds = (currentSlot.defaultTarhimSec % 60).toString()
                                }
                            },
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBothActive) Color(0xFF00796B) else Color(0xFFF1F1F1),
                                contentColor = if (isBothActive) Color.White else Color(0xFF333333)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = if (!isBothActive) BorderStroke(1.dp, Color(0xFFCCCCCC)) else null,
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            if (isBothActive) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text("Keduanya Aktif", fontSize = 12.sp, fontWeight = if (isBothActive) FontWeight.Bold else FontWeight.Medium)
                        }

                        Button(
                            onClick = {
                                isTartilActive = true
                                isTarhimActive = false
                                if (tartilMinutes == "0") tartilMinutes = currentSlot.defaultTartilMin.toString()
                            },
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTartilOnly) Color(0xFF2E7D32) else Color(0xFFF1F1F1),
                                contentColor = if (isTartilOnly) Color.White else Color(0xFF333333)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = if (!isTartilOnly) BorderStroke(1.dp, Color(0xFFCCCCCC)) else null,
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            if (isTartilOnly) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text("Hanya Tartil", fontSize = 12.sp, fontWeight = if (isTartilOnly) FontWeight.Bold else FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                isTartilActive = false
                                isTarhimActive = true
                                val curTot = (tarhimMinutes.toIntOrNull() ?: 0) * 60 + (tarhimSeconds.toIntOrNull() ?: 0)
                                if (curTot == 0) {
                                    tarhimMinutes = (currentSlot.defaultTarhimSec / 60).toString()
                                    tarhimSeconds = (currentSlot.defaultTarhimSec % 60).toString()
                                }
                            },
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTarhimOnly) Color(0xFF1565C0) else Color(0xFFF1F1F1),
                                contentColor = if (isTarhimOnly) Color.White else Color(0xFF333333)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = if (!isTarhimOnly) BorderStroke(1.dp, Color(0xFFCCCCCC)) else null,
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            if (isTarhimOnly) {
                                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text("Hanya Tarhim", fontSize = 12.sp, fontWeight = if (isTarhimOnly) FontWeight.Bold else FontWeight.Medium)
                        }

                        Button(
                            onClick = {
                                isTartilActive = false
                                isTarhimActive = false
                            },
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isBothDisabled) Color(0xFFD32F2F) else Color(0xFFF1F1F1),
                                contentColor = if (isBothDisabled) Color.White else Color(0xFF333333)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = if (!isBothDisabled) BorderStroke(1.dp, Color(0xFFCCCCCC)) else null,
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                        ) {
                            if (isBothDisabled) {
                                Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text("Semua Nonaktif", fontSize = 12.sp, fontWeight = if (isBothDisabled) FontWeight.Bold else FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ---------------------------------------------------------
                    // SUB-BAGIAN 1: PENGATURAN TARTIL (FOLDER 01)
                    // ---------------------------------------------------------
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTartilActive) Color(0xFFF4FAF5) else Color(0xFFFAFAFA)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isTartilActive) Color(0xFFA5D6A7) else Color(0xFFE0E0E0)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_music_note_24),
                                        contentDescription = null,
                                        tint = if (isTartilActive) Color(0xFF2E7D32) else Color(0xFF9E9E9E),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "TARTIL AL-QUR'AN",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isTartilActive) Color(0xFF1B5E20) else Color(0xFF616161)
                                    )
                                }
                                Switch(
                                    checked = isTartilActive,
                                    onCheckedChange = { checked ->
                                        isTartilActive = checked
                                        if (checked && tartilMinutes == "0") {
                                            val last = PrefManager.getString(
                                                context,
                                                "mp3_tartil_last_${currentSlot.slotCode}",
                                                currentSlot.defaultTartilMin.toString()
                                            )
                                            tartilMinutes = if (last != "0") last else currentSlot.defaultTartilMin.toString()
                                        }
                                    },
                                    thumbContent = if (isTartilActive) {
                                        { Icon(Icons.Filled.Check, contentDescription = null) }
                                    } else null
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (isTartilActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isTartilActive)
                                        "AKTIF ($tartilMinutes menit sebelum Adzan)"
                                    else
                                        "NONAKTIF (Tidak diputar)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isTartilActive) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }

                            if (isTartilActive) {
                                Spacer(modifier = Modifier.height(10.dp))

                                // Dropdown Waktu Mulai Tartil
                                var tartilExpanded by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = tartilExpanded,
                                    onExpandedChange = { tartilExpanded = !tartilExpanded },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        readOnly = true,
                                        value = "$tartilMinutes mnt",
                                        onValueChange = {},
                                        label = { Text("WAKTU MULAI TARTIL") },
                                        suffix = { Text("sebelum Adzan") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tartilExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = tartilExpanded,
                                        onDismissRequest = { tartilExpanded = false }
                                    ) {
                                        tartilMinOptions.forEach { opt ->
                                            DropdownMenuItem(
                                                text = { Text("$opt mnt sebelum Adzan") },
                                                onClick = {
                                                    tartilMinutes = opt
                                                    PrefManager.saveString(
                                                        context,
                                                        "mp3_tartil_last_${currentSlot.slotCode}",
                                                        opt
                                                    )
                                                    tartilExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Dropdown File Tartil
                                var f1Expanded by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = f1Expanded,
                                    onExpandedChange = { f1Expanded = !f1Expanded },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        readOnly = true,
                                        value = "File $tartilTrackNumber",
                                        onValueChange = {},
                                        label = { Text("Pilih File Tartil (01)") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(f1Expanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = f1Expanded,
                                        onDismissRequest = { f1Expanded = false }
                                    ) {
                                        trackOptions.forEach { opt ->
                                            DropdownMenuItem(
                                                text = { Text("File $opt") },
                                                onClick = {
                                                    tartilTrackNumber = opt
                                                    f1Expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // ---------------------------------------------------------
                    // SUB-BAGIAN 2: PENGATURAN TARHIM (FOLDER 02)
                    // ---------------------------------------------------------
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTarhimActive) Color(0xFFF3F8FD) else Color(0xFFFAFAFA)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isTarhimActive) Color(0xFF90CAF9) else Color(0xFFE0E0E0)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_music_note_24),
                                        contentDescription = null,
                                        tint = if (isTarhimActive) Color(0xFF1565C0) else Color(0xFF9E9E9E),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "TARHIM/SHOLAWAT",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isTarhimActive) Color(0xFF0D47A1) else Color(0xFF616161)
                                    )
                                }
                                Switch(
                                    checked = isTarhimActive,
                                    onCheckedChange = { checked ->
                                        isTarhimActive = checked
                                        val curTot = (tarhimMinutes.toIntOrNull() ?: 0) * 60 + (tarhimSeconds.toIntOrNull() ?: 0)
                                        if (checked && curTot == 0) {
                                            val lastM = PrefManager.getString(
                                                context,
                                                "mp3_tarhim_last_m_${currentSlot.slotCode}",
                                                (currentSlot.defaultTarhimSec / 60).toString()
                                            )
                                            val lastS = PrefManager.getString(
                                                context,
                                                "mp3_tarhim_last_s_${currentSlot.slotCode}",
                                                (currentSlot.defaultTarhimSec % 60).toString()
                                            )
                                            tarhimMinutes = if (lastM != "0" || lastS != "0") lastM else (currentSlot.defaultTarhimSec / 60).toString()
                                            tarhimSeconds = if (lastM != "0" || lastS != "0") lastS else (currentSlot.defaultTarhimSec % 60).toString()
                                        }
                                    },
                                    thumbContent = if (isTarhimActive) {
                                        { Icon(Icons.Filled.Check, contentDescription = null) }
                                    } else null
                                )
                            }

                            val totalSecDisplay = (tarhimMinutes.toIntOrNull() ?: 0) * 60 + (tarhimSeconds.toIntOrNull() ?: 0)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = if (isTarhimActive) Color(0xFFE3F2FD) else Color(0xFFFFEBEE),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isTarhimActive)
                                        "AKTIF (Durasi: ${tarhimMinutes}m ${tarhimSeconds}s)"
                                    else
                                        "NONAKTIF (tidak diputar)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isTarhimActive) Color(0xFF1565C0) else Color(0xFFC62828)
                                )
                            }

                            if (isTarhimActive) {
                                Spacer(modifier = Modifier.height(10.dp))

                                // Dropdown File Tarhim
                                var f2Expanded by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = f2Expanded,
                                    onExpandedChange = { f2Expanded = !f2Expanded },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        readOnly = true,
                                        value = "File $tarhimTrackNumber",
                                        onValueChange = {},
                                        label = { Text("Pilih File Tarhim (02)") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(f2Expanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = f2Expanded,
                                        onDismissRequest = { f2Expanded = false }
                                    ) {
                                        trackOptions.forEach { opt ->
                                            DropdownMenuItem(
                                                text = { Text("File $opt") },
                                                onClick = {
                                                    tarhimTrackNumber = opt
                                                    f2Expanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Durasi Tarhim (Menit & Detik)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Menit Tarhim
                                    var tarhimMinExpanded by remember { mutableStateOf(false) }
                                    ExposedDropdownMenuBox(
                                        expanded = tarhimMinExpanded,
                                        onExpandedChange = { tarhimMinExpanded = !tarhimMinExpanded },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        OutlinedTextField(
                                            readOnly = true,
                                            value = "$tarhimMinutes Mnt",
                                            onValueChange = {},
                                            label = { Text("Menit Tarhim") },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(tarhimMinExpanded)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = tarhimMinExpanded,
                                            onDismissRequest = { tarhimMinExpanded = false }
                                        ) {
                                            tarhimMinOptions.forEach { opt ->
                                                DropdownMenuItem(
                                                    text = { Text("$opt Mnt") },
                                                    onClick = {
                                                        tarhimMinutes = opt
                                                        PrefManager.saveString(
                                                            context,
                                                            "mp3_tarhim_last_m_${currentSlot.slotCode}",
                                                            opt
                                                        )
                                                        tarhimMinExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // Detik Tarhim
                                    var tarhimSecExpanded by remember { mutableStateOf(false) }
                                    ExposedDropdownMenuBox(
                                        expanded = tarhimSecExpanded,
                                        onExpandedChange = { tarhimSecExpanded = !tarhimSecExpanded },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        OutlinedTextField(
                                            readOnly = true,
                                            value = "$tarhimSeconds Dtk",
                                            onValueChange = {},
                                            label = { Text("Detik Tarhim") },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(tarhimSecExpanded)
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = tarhimSecExpanded,
                                            onDismissRequest = { tarhimSecExpanded = false }
                                        ) {
                                            tarhimSecOptions.forEach { opt ->
                                                DropdownMenuItem(
                                                    text = { Text("$opt Dtk") },
                                                    onClick = {
                                                        tarhimSeconds = opt
                                                        PrefManager.saveString(
                                                            context,
                                                            "mp3_tarhim_last_s_${currentSlot.slotCode}",
                                                            opt
                                                        )
                                                        tarhimSecExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button Kirim Jadwal Sholat Terpilih
            Button(
                onClick = {
                    if (isSendingSchedule) return@Button
                    val slot = currentSlot.slotCode
                    val effectiveTartilMin = if (isTartilActive) (tartilMinutes.toIntOrNull() ?: currentSlot.defaultTartilMin) else 0
                    val effectiveTarhimSec = if (isTarhimActive) {
                        val m = tarhimMinutes.toIntOrNull() ?: (currentSlot.defaultTarhimSec / 60)
                        val s = tarhimSeconds.toIntOrNull() ?: (currentSlot.defaultTarhimSec % 60)
                        m * 60 + s
                    } else 0

                    PrefManager.saveBoolean(context, "mp3_tartil_act_$slot", isTartilActive)
                    PrefManager.saveBoolean(context, "mp3_tarhim_act_$slot", isTarhimActive)
                    PrefManager.saveString(context, "mp3_tartil_min_$slot", effectiveTartilMin.toString())
                    PrefManager.saveString(context, "mp3_tarhim_min_$slot", if (isTarhimActive) tarhimMinutes else "0")
                    PrefManager.saveString(context, "mp3_tarhim_sec_$slot", if (isTarhimActive) tarhimSeconds else "0")
                    if (isTartilActive) {
                        PrefManager.saveString(context, "mp3_tartil_last_$slot", tartilMinutes)
                    }
                    if (isTarhimActive) {
                        PrefManager.saveString(context, "mp3_tarhim_last_m_$slot", tarhimMinutes)
                        PrefManager.saveString(context, "mp3_tarhim_last_s_$slot", tarhimSeconds)
                    }
                    PrefManager.saveString(context, "mp3_tartil_tr_$slot", tartilTrackNumber)
                    PrefManager.saveString(context, "mp3_tarhim_tr_$slot", tarhimTrackNumber)

                    val commands = listOf(
                        "NT$slot$effectiveTartilMin",
                        "ND$slot$effectiveTarhimSec",
                        "NF$slot$tartilTrackNumber",
                        "NH$slot$tarhimTrackNumber"
                    )
                    coroutineScope.launch {
                        scheduleDialogTitle = "JADWAL MP3 ${currentSlot.name.uppercase()}"
                        isSendingSchedule = true
                        scheduleTotal = commands.size
                        scheduleProgress = 0

                        val success = BluetoothConnectionHolder.sendDataList(
                            dataList = commands,
                            delayBetweenMs = 800L
                        ) { current, total, item ->
                            scheduleProgress = current
                            scheduleTotal = total
                            scheduleItemLabel = when {
                                item.startsWith("NT") -> if (effectiveTartilMin > 0) "1. Tartil: $effectiveTartilMin Menit" else "1. Tartil: NONAKTIF (0 Menit)"
                                item.startsWith("ND") -> if (effectiveTarhimSec > 0) "2. Tarhim: $effectiveTarhimSec Detik" else "2. Tarhim: NONAKTIF (0 Detik)"
                                item.startsWith("NF") -> "3. Track Tartil: #$tartilTrackNumber"
                                item.startsWith("NH") -> "4. Track Tarhim: #$tarhimTrackNumber"
                                else -> item
                            }
                        }

                        isSendingSchedule = false
                        if (success) {
                            val msg = buildString {
                                append("Berhasil! Jadwal ${currentSlot.name}: ")
                                append(if (effectiveTartilMin > 0) "Tartil ${effectiveTartilMin}m, " else "Tartil NONAKTIF, ")
                                append(if (effectiveTarhimSec > 0) "Tarhim ${effectiveTarhimSec}s" else "Tarhim NONAKTIF")
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Gagal mengirim jadwal. Pastikan koneksi terhubung!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp))
                    .background(color = Color(0xFF00796B), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp)
                    .height(50.dp),
            ) {
                Text(
                    text = "KIRIM JADWAL ${currentSlot.name.uppercase()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    painter = painterResource(id = R.drawable.baseline_send_24),
                    contentDescription = "Kirim",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Button Terapkan ke Semua Sholat
            OutlinedButton(
                onClick = { showApplyAllDialog = true },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.5.dp, Color(0xFF00796B)),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color(0xFF00796B),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TERAPKAN KE SEMUA SHOLAT",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF00796B)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Button
            TombolHome(name = "MENU UTAMA", navController = navController)
        }

        // Dialog Konfirmasi Terapkan ke Semua Sholat
        if (showApplyAllDialog) {
            val effTartilMin = if (isTartilActive) (tartilMinutes.toIntOrNull() ?: currentSlot.defaultTartilMin) else 0
            val effTarhimSec = if (isTarhimActive) {
                val m = tarhimMinutes.toIntOrNull() ?: (currentSlot.defaultTarhimSec / 60)
                val s = tarhimSeconds.toIntOrNull() ?: (currentSlot.defaultTarhimSec % 60)
                m * 60 + s
            } else 0

            Dialog(onDismissRequest = { showApplyAllDialog = false }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Terapkan ke Semua Sholat?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1B1B1B)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Pengaturan berikut akan disalin dan dikirim ke seluruh 6 waktu sholat (Subuh, Dzuhur, Ashar, Maghrib, Isya, dan Jum'at):\n\n" +
                                    "• Tartil: ${if (isTartilActive) "AKTIF ($effTartilMin Menit, File #$tartilTrackNumber)" else "NONAKTIF (0 Menit)"}\n" +
                                    "• Tarhim: ${if (isTarhimActive) "AKTIF (${tarhimMinutes}m ${tarhimSeconds}s, File #$tarhimTrackNumber)" else "NONAKTIF (0 Detik)"}",
                            fontSize = 13.sp,
                            color = Color(0xFF424242),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showApplyAllDialog = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("BATAL", fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    showApplyAllDialog = false
                                    if (isSendingSchedule) return@Button

                                    val allCommands = mutableListOf<String>()
                                    slots.forEach { s ->
                                        val slotCode = s.slotCode
                                        PrefManager.saveBoolean(context, "mp3_tartil_act_$slotCode", isTartilActive)
                                        PrefManager.saveBoolean(context, "mp3_tarhim_act_$slotCode", isTarhimActive)
                                        PrefManager.saveString(context, "mp3_tartil_min_$slotCode", effTartilMin.toString())
                                        PrefManager.saveString(context, "mp3_tarhim_min_$slotCode", if (isTarhimActive) tarhimMinutes else "0")
                                        PrefManager.saveString(context, "mp3_tarhim_sec_$slotCode", if (isTarhimActive) tarhimSeconds else "0")
                                        if (isTartilActive) PrefManager.saveString(context, "mp3_tartil_last_$slotCode", tartilMinutes)
                                        if (isTarhimActive) {
                                            PrefManager.saveString(context, "mp3_tarhim_last_m_$slotCode", tarhimMinutes)
                                            PrefManager.saveString(context, "mp3_tarhim_last_s_$slotCode", tarhimSeconds)
                                        }
                                        PrefManager.saveString(context, "mp3_tartil_tr_$slotCode", tartilTrackNumber)
                                        PrefManager.saveString(context, "mp3_tarhim_tr_$slotCode", tarhimTrackNumber)

                                        allCommands.add("NT$slotCode$effTartilMin")
                                        allCommands.add("ND$slotCode$effTarhimSec")
                                        allCommands.add("NF$slotCode$tartilTrackNumber")
                                        allCommands.add("NH$slotCode$tarhimTrackNumber")
                                    }

                                    coroutineScope.launch {
                                        scheduleDialogTitle = "JADWAL MP3 SEMUA SHOLAT"
                                        isSendingSchedule = true
                                        scheduleTotal = allCommands.size
                                        scheduleProgress = 0

                                        val success = BluetoothConnectionHolder.sendDataList(
                                            dataList = allCommands,
                                            delayBetweenMs = 600L
                                        ) { current, total, item ->
                                            scheduleProgress = current
                                            scheduleTotal = total
                                            val slotNum = item.getOrNull(2)?.toString() ?: ""
                                            val slotName = slots.find { it.slotCode.toString() == slotNum }?.name ?: "Slot $slotNum"
                                            scheduleItemLabel = when {
                                                item.startsWith("NT") -> "$slotName - Tartil: ${if (effTartilMin > 0) "$effTartilMin m" else "NONAKTIF"}"
                                                item.startsWith("ND") -> "$slotName - Tarhim: ${if (effTarhimSec > 0) "$effTarhimSec s" else "NONAKTIF"}"
                                                item.startsWith("NF") -> "$slotName - Track Tartil #$tartilTrackNumber"
                                                item.startsWith("NH") -> "$slotName - Track Tarhim #$tarhimTrackNumber"
                                                else -> item
                                            }
                                        }

                                        isSendingSchedule = false
                                        if (success) {
                                            Toast.makeText(context, "Berhasil menerapkan jadwal ke seluruh waktu sholat!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Gagal mengirim ke semua sholat. Periksa Bluetooth!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("YA, TERAPKAN", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        SendingProgressDialog(
            show = isSendingSchedule,
            current = scheduleProgress,
            total = scheduleTotal,
            moduleTitle = scheduleDialogTitle,
            itemLabel = scheduleItemLabel
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScreenMp3Preview() {
    JWSAlGaffarTheme {
        val navController = rememberNavController()
        ScreenMp3(navController = navController)
    }
}
