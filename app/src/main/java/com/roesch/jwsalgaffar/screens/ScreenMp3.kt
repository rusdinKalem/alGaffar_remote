package com.roesch.jwsalgaffar.screens

import android.bluetooth.BluetoothSocket
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
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
import com.roesch.jwsalgaffar.components.TombolHome
import com.roesch.jwsalgaffar.ui.theme.JWSAlGaffarTheme
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import com.roesch.jwsalgaffar.utils.PrefManager
import androidx.compose.runtime.rememberCoroutineScope
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

    var tartilMinutes by remember { mutableStateOf(PrefManager.getString(context, "mp3_tartil_min_${currentSlot.slotCode}", currentSlot.defaultTartilMin.toString())) }
    var tarhimMinutes by remember { mutableStateOf(PrefManager.getString(context, "mp3_tarhim_min_${currentSlot.slotCode}", (currentSlot.defaultTarhimSec / 60).toString())) }
    var tarhimSeconds by remember { mutableStateOf(PrefManager.getString(context, "mp3_tarhim_sec_${currentSlot.slotCode}", (currentSlot.defaultTarhimSec % 60).toString())) }
    var tartilTrackNumber by remember { mutableStateOf(PrefManager.getString(context, "mp3_tartil_tr_${currentSlot.slotCode}", currentSlot.defaultTartilTrack.toString())) }
    var tarhimTrackNumber by remember { mutableStateOf(PrefManager.getString(context, "mp3_tarhim_tr_${currentSlot.slotCode}", currentSlot.defaultTarhimTrack.toString())) }

    // Update fields when slot changes
    fun onSlotChanged(index: Int) {
        selectedSlotIndex = index
        val slot = slots[index]
        tartilMinutes = PrefManager.getString(context, "mp3_tartil_min_${slot.slotCode}", slot.defaultTartilMin.toString())
        tarhimMinutes = PrefManager.getString(context, "mp3_tarhim_min_${slot.slotCode}", (slot.defaultTarhimSec / 60).toString())
        tarhimSeconds = PrefManager.getString(context, "mp3_tarhim_sec_${slot.slotCode}", (slot.defaultTarhimSec % 60).toString())
        tartilTrackNumber = PrefManager.getString(context, "mp3_tartil_tr_${slot.slotCode}", slot.defaultTartilTrack.toString())
        tarhimTrackNumber = PrefManager.getString(context, "mp3_tarhim_tr_${slot.slotCode}", slot.defaultTarhimTrack.toString())
    }

    val tartilMinOptions = listOf("0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "60")
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
                .padding(bottom = 30.dp),
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
                        horizontalArrangement = Arrangement.SpaceBetween
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

                        // Stop Button
                        Button(
                            onClick = {
                                sendCommand("PS")
                                Toast.makeText(context, "Audio Dihentikan", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Stop",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("STOP")
                        }
                    }
                }
            }

            // ---------------------------------------------------------
            // CARD 2: JADWAL OTOMATIS TARTIL & TARHIM PER WAKTU SHOLAT
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
                        text = "JADWAL OTOMATIS PER SHOLAT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1B1B1B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Pilih Sholat Dropdown
                    var sholatExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = sholatExpanded,
                        onExpandedChange = { sholatExpanded = !sholatExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = "${currentSlot.name} (Slot ${currentSlot.slotCode})",
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
                                    text = { Text("${slot.name} (Slot ${slot.slotCode})") },
                                    onClick = {
                                        onSlotChanged(idx)
                                        sholatExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mulai Tartil (Menit Sebelum Adzan)
                    var tartilExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = tartilExpanded,
                        onExpandedChange = { tartilExpanded = !tartilExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = "$tartilMinutes Menit",
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
                                val labelText = if (opt == "0") "0 Menit (Tidak Aktif)" else "$opt Menit Sebelum Adzan"
                                DropdownMenuItem(
                                    text = { Text(labelText) },
                                    onClick = {
                                        tartilMinutes = opt
                                        PrefManager.saveString(context, "mp3_tartil_min_${currentSlot.slotCode}", opt)
                                        tartilExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // File Tartil (Folder 01) & File Tarhim (Folder 02)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        var f1Expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = f1Expanded,
                            onExpandedChange = { f1Expanded = !f1Expanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = "File $tartilTrackNumber",
                                onValueChange = {},
                                label = { Text("File Tartil (01)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(f1Expanded) },
                                modifier = Modifier.menuAnchor()
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
                                            PrefManager.saveString(context, "mp3_tartil_tr_${currentSlot.slotCode}", opt)
                                            f1Expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        var f2Expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = f2Expanded,
                            onExpandedChange = { f2Expanded = !f2Expanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = "File $tarhimTrackNumber",
                                onValueChange = {},
                                label = { Text("File Tarhim (02)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(f2Expanded) },
                                modifier = Modifier.menuAnchor()
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
                                            PrefManager.saveString(context, "mp3_tarhim_tr_${currentSlot.slotCode}", opt)
                                            f2Expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Durasi Tarhim (Menit & Detik)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Menit Tarhim (5..25)
                        var tarhimMinExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = tarhimMinExpanded,
                            onExpandedChange = { tarhimMinExpanded = !tarhimMinExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = "$tarhimMinutes Menit",
                                onValueChange = {},
                                label = { Text("Menit Tarhim") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tarhimMinExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = tarhimMinExpanded,
                                onDismissRequest = { tarhimMinExpanded = false }
                            ) {
                                tarhimMinOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text("$opt Menit") },
                                        onClick = {
                                            tarhimMinutes = opt
                                            PrefManager.saveString(context, "mp3_tarhim_min_${currentSlot.slotCode}", opt)
                                            tarhimMinExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Detik Tarhim (0..59)
                        var tarhimSecExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = tarhimSecExpanded,
                            onExpandedChange = { tarhimSecExpanded = !tarhimSecExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = "$tarhimSeconds Detik",
                                onValueChange = {},
                                label = { Text("Detik Tarhim") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tarhimSecExpanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = tarhimSecExpanded,
                                onDismissRequest = { tarhimSecExpanded = false }
                            ) {
                                tarhimSecOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text("$opt Detik") },
                                        onClick = {
                                            tarhimSeconds = opt
                                            PrefManager.saveString(context, "mp3_tarhim_sec_${currentSlot.slotCode}", opt)
                                            tarhimSecExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Button Kirim Jadwal Sholat Terpilih
                    Button(
                        onClick = {
                            val slot = currentSlot.slotCode
                            val totalTarhimSec = (tarhimMinutes.toIntOrNull() ?: 0) * 60 + (tarhimSeconds.toIntOrNull() ?: 0)
                            
                            PrefManager.saveString(context, "mp3_tartil_min_$slot", tartilMinutes)
                            PrefManager.saveString(context, "mp3_tarhim_min_$slot", tarhimMinutes)
                            PrefManager.saveString(context, "mp3_tarhim_sec_$slot", tarhimSeconds)
                            PrefManager.saveString(context, "mp3_tartil_tr_$slot", tartilTrackNumber)
                            PrefManager.saveString(context, "mp3_tarhim_tr_$slot", tarhimTrackNumber)

                            val commands = listOf(
                                "NT$slot$tartilMinutes",
                                "ND$slot$totalTarhimSec",
                                "NF$slot$tartilTrackNumber",
                                "NH$slot$tarhimTrackNumber"
                            )
                            coroutineScope.launch {
                                val success = BluetoothConnectionHolder.sendDataList(commands)
                                if (success) {
                                    Toast.makeText(
                                        context,
                                        "Jadwal MP3 ${currentSlot.name} berhasil disimpan!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Gagal mengirim jadwal. Pastikan Bluetooth terhubung!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1B1B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "KIRIM JADWAL ${currentSlot.name.uppercase()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // ---------------------------------------------------------
            // CARD 3: KONTROL MANUAL (DIRECT PLAY)
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
                        text = "PUTAR MP3 LANGSUNG (TEST AUDIO)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1B1B1B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Play Tartil Now (Folder 01)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.width(130.dp)
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = directTartilTrack,
                                onValueChange = {},
                                label = { Text("Tartil Track") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                trackOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text("File $opt") },
                                        onClick = {
                                            directTartilTrack = opt
                                            PrefManager.saveString(context, "mp3_d_tartil", opt)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                sendCommand("PT$directTartilTrack")
                                Toast.makeText(
                                    context,
                                    "Memutar Tartil Track $directTartilTrack",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PLAY")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Play Tarhim Now (Folder 02)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.width(130.dp)
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = directTarhimTrack,
                                onValueChange = {},
                                label = { Text("Tarhim Track") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                trackOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text("File $opt") },
                                        onClick = {
                                            directTarhimTrack = opt
                                            PrefManager.saveString(context, "mp3_d_tarhim", opt)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                sendCommand("PH$directTarhimTrack")
                                Toast.makeText(
                                    context,
                                    "Memutar Tarhim Track $directTarhimTrack",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PLAY")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Play Custom Folder/Track
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = directCustomFolder,
                            onValueChange = {
                                directCustomFolder = it
                                PrefManager.saveString(context, "mp3_d_folder", it)
                            },
                            label = { Text("Folder") },
                            modifier = Modifier.width(80.dp)
                        )
                        OutlinedTextField(
                            value = directCustomTrack,
                            onValueChange = {
                                directCustomTrack = it
                                PrefManager.saveString(context, "mp3_d_track", it)
                            },
                            label = { Text("Track") },
                            modifier = Modifier.width(80.dp)
                        )
                        Button(
                            onClick = {
                                sendCommand("P$directCustomFolder,$directCustomTrack")
                                Toast.makeText(
                                    context,
                                    "Memutar Folder $directCustomFolder Track $directCustomTrack",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PLAY")
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Button
            TombolHome(name = "MENU UTAMA", navController = navController)
        }
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
