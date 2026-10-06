package com.roesch.jwsalgaffar

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.roesch.jwsalgaffar.ui.theme.JWSAlGaffarTheme
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import com.roesch.jwsalgaffar.utils.DeviceConnectionReceiver
import com.roesch.jwsalgaffar.utils.PermissionManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val bondedDevices = mutableStateListOf<BluetoothDevice>()
    private var isConnecting by mutableStateOf(false)
    private var connectingDeviceName by mutableStateOf<String?>(null)

    private var selectedTab by mutableStateOf(0)
    private var isCheckingWifi by mutableStateOf(false)
    private var showWifiFailedDialog by mutableStateOf(false)

    private val enableBtLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        refreshBondedDevices()
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PermissionManager(this).requestPermissions()
        val btManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = btManager.adapter

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        registerReceiver(DeviceConnectionReceiver(), filter)

        if (!bluetoothAdapter.isEnabled) {
            val requestBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBtLauncher.launch(requestBtIntent)
        }

        refreshBondedDevices()

        setContent {
            JWSAlGaffarTheme {
                MainUI()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun refreshBondedDevices() {
        if (::bluetoothAdapter.isInitialized && bluetoothAdapter.isEnabled) {
            try {
                bondedDevices.clear()
                bondedDevices.addAll(bluetoothAdapter.bondedDevices)
            } catch (_: Exception) {}
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        refreshBondedDevices()
    }

    @SuppressLint("MissingPermission")
    @Composable
    fun MainUI() {
        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.bg01),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.35f
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 45.dp)
            ) {
                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "KONEKSI KONTROL JWS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1B1B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = if (selectedTab == 0)
                        "Pilih modul Bluetooth JWS yang terpasang"
                    else
                        "Remote control melalui jaringan Hotspot WiFi ESP32",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF00796B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // SEGMENTED TAB SWITCHER (BLUETOOTH VS WIFI)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFF00796B).copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // TAB 1: BLUETOOTH
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .background(
                                if (selectedTab == 0) Color(0xFF00796B) else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedTab = 0 },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_bluetooth_connected_24),
                            contentDescription = "Bluetooth",
                            tint = if (selectedTab == 0) Color.White else Color(0xFF00796B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BLUETOOTH",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTab == 0) Color.White else Color(0xFF00796B)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // TAB 2: WIFI ESP32
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .background(
                                if (selectedTab == 1) Color(0xFF00796B) else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedTab = 1 },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_wifi_24),
                            contentDescription = "WiFi ESP32",
                            tint = if (selectedTab == 1) Color.White else Color(0xFF00796B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WIFI ESP32",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTab == 1) Color.White else Color(0xFF00796B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // KONTEN TAB:
                if (selectedTab == 0) {
                    // --- PANEL BLUETOOTH ---
                    if (bondedDevices.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.90f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Tidak Ada Perangkat Bluetooth",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFFD32F2F)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Silakan pasangkan (pair) modul Bluetooth JWS (HC-05 / HC-06 / JWS-RGB-P5) di menu Pengaturan Bluetooth smartphone Anda terlebih dahulu.",
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF424242)
                                )
                            }
                        }
                    } else {
                        for (device in bondedDevices) {
                            PairedDevices(name = device.name ?: "Unknown Device", address = device.address)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                } else {
                    // --- PANEL WIFI ESP32 ---
                    WifiSection(coroutineScope)
                }

                Spacer(modifier = Modifier.height(30.dp))
            }

            // DIALOGS
            if (isConnecting) {
                Dialog(
                    onDismissRequest = {
                        BluetoothConnectionHolder.close()
                        isConnecting = false
                        Toast.makeText(this@MainActivity, "Koneksi dibatalkan", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00796B))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Menghubungkan Bluetooth...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF1B1B1B)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Menyambungkan ke ${connectingDeviceName ?: "Modul Bluetooth"}",
                                fontSize = 14.sp,
                                color = Color(0xFF616161),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = {
                                    BluetoothConnectionHolder.close()
                                    isConnecting = false
                                    Toast.makeText(this@MainActivity, "Koneksi dibatalkan", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("BATAL")
                            }
                        }
                    }
                }
            }

            if (isCheckingWifi) {
                Dialog(onDismissRequest = { isCheckingWifi = false }) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00796B))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Memeriksa WiFi ESP32...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFF1B1B1B)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Menghubungi http://192.168.4.1",
                                fontSize = 13.sp,
                                color = Color(0xFF616161),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            if (showWifiFailedDialog) {
                Dialog(onDismissRequest = { showWifiFailedDialog = false }) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_wifi_24),
                                contentDescription = null,
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Koneksi WiFi Belum Terhubung",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFFD32F2F),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tidak dapat menjangkau ESP32 di 192.168.4.1.\n\nPastikan smartphone Anda sudah terhubung ke WiFi Hotspot:\n• SSID: JWS-RGB-P5\n• Password: 12345678",
                                fontSize = 13.sp,
                                color = Color(0xFF424242),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = {
                                    showWifiFailedDialog = false
                                    try {
                                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                                    } catch (_: Exception) {
                                        startActivity(Intent(Settings.ACTION_SETTINGS))
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("BUKA PENGATURAN WIFI HP")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    showWifiFailedDialog = false
                                    val intent = Intent(this@MainActivity, HomeActivity::class.java).apply {
                                        putExtra("Mode", "WIFI")
                                    }
                                    startActivity(intent)
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("TETAP MASUK KE APLIKASI", color = Color(0xFF00796B))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { showWifiFailedDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                elevation = null
                            ) {
                                Text("TUTUP", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun WifiSection(coroutineScope: kotlinx.coroutines.CoroutineScope) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.94f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_wifi_24),
                        contentDescription = "WiFi",
                        tint = Color(0xFF00796B),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Hotspot Access Point ESP32",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1B1B)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detail Kredensial Hotspot
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0F2F1), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "SSID Hotspot:", fontSize = 13.sp, color = Color(0xFF455A64))
                        Text(text = "JWS-RGB-P5", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Password WiFi:", fontSize = 13.sp, color = Color(0xFF455A64))
                        Text(text = "12345678", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Alamat Server:", fontSize = 13.sp, color = Color(0xFF455A64))
                        Text(text = "192.168.4.1", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tombol 1: Masuk Aplikasi via WiFi
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isCheckingWifi = true
                            val isConnected = BluetoothConnectionHolder.testWifiConnection()
                            isCheckingWifi = false
                            if (isConnected) {
                                val intent = Intent(this@MainActivity, HomeActivity::class.java).apply {
                                    putExtra("Mode", "WIFI")
                                }
                                startActivity(intent)
                            } else {
                                showWifiFailedDialog = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_send_24),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KONEK APLIKASI (MODE WIFI)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tombol 2: Buka Web Dashboard
                Button(
                    onClick = {
                        try {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.4.1"))
                            startActivity(browserIntent)
                        } catch (e: Exception) {
                            Toast.makeText(this@MainActivity, "Gagal membuka browser: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_to_home_screen_24),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BUKA WEB DASHBOARD",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tombol 3: Buka Pengaturan WiFi HP
                Button(
                    onClick = {
                        try {
                            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                        } catch (_: Exception) {
                            startActivity(Intent(Settings.ACTION_SETTINGS))
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_wifi_24),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BUKA PENGATURAN WIFI HP",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    @Composable
    private fun PairedDevices(name: String, address: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(14.dp)
                )
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF00796B)
                )
                .padding(horizontal = 14.dp)
                .height(68.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_bluetooth_connected_24),
                    contentDescription = null,
                    tint = Color(0xFF00796B),
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1B1B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = address,
                        fontSize = 12.sp,
                        color = Color(0xFF757575),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Button(
                modifier = Modifier
                    .height(40.dp)
                    .width(105.dp),
                onClick = {
                    connectingDeviceName = name
                    isConnecting = true
                    Thread { connectToDevice(address) }.start()
                },
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "CONNECT",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(deviceAddress: String) {
        val success = BluetoothConnectionHolder.connect(bluetoothAdapter, deviceAddress)
        runOnUiThread {
            isConnecting = false
            if (success) {
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("Address", deviceAddress)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "Koneksi ke Bluetooth gagal. Pastikan modul HC-05/HC-06 menyala dan sudah terpasang (paired).",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
