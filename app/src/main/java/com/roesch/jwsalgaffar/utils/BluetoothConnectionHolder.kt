package com.roesch.jwsalgaffar.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

enum class ConnectionMode {
    BLUETOOTH,
    WIFI
}

object BluetoothConnectionHolder {
    var connectionMode: ConnectionMode = ConnectionMode.BLUETOOTH
    var wifiBaseUrl: String = "http://192.168.4.1"

    var socket: BluetoothSocket? = null
    var connectedDeviceAddress: String? = null
    var lastBluetoothAdapter: BluetoothAdapter? = null
    var lastConnectionError: String? = null

    val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @SuppressLint("MissingPermission")
    fun isLikelyHcDevice(device: BluetoothDevice): Boolean {
        val name = try { device.name ?: "" } catch (_: Exception) { "" }
        return name.contains("HC-06", ignoreCase = true) ||
               name.contains("HC06", ignoreCase = true) ||
               name.contains("HC-05", ignoreCase = true) ||
               name.contains("HC05", ignoreCase = true) ||
               name.contains("BT-06", ignoreCase = true) ||
               name.contains("BT06", ignoreCase = true) ||
               name.contains("LINVOR", ignoreCase = true) ||
               name.contains("BOLUTEK", ignoreCase = true) ||
               name.contains("HC-", ignoreCase = true)
    }

    @SuppressLint("MissingPermission")
    fun createSocket(device: BluetoothDevice): BluetoothSocket {
        if (isLikelyHcDevice(device)) {
            try {
                val method = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                return method.invoke(device, 1) as BluetoothSocket
            } catch (_: Exception) {}
            try {
                val method = device.javaClass.getMethod("createInsecureRfcommSocket", Int::class.javaPrimitiveType)
                return method.invoke(device, 1) as BluetoothSocket
            } catch (_: Exception) {}
        }

        // Method 1: Insecure RFCOMM (Paling kompatibel untuk ESP32 & Android modern)
        try {
            return device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
        } catch (_: Exception) {}

        // Method 2: Secure RFCOMM
        try {
            return device.createRfcommSocketToServiceRecord(SPP_UUID)
        } catch (_: Exception) {}

        // Method 3: Reflection on Channel 1 (Fallback untuk modul HC-06 lama)
        val method = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
        return method.invoke(device, 1) as BluetoothSocket
    }

    @SuppressLint("MissingPermission")
    fun connect(bluetoothAdapter: BluetoothAdapter, address: String): Boolean {
        lastBluetoothAdapter = bluetoothAdapter
        val formattedAddress = address.uppercase()
        connectedDeviceAddress = formattedAddress

        try {
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
            }
            Thread.sleep(250)
        } catch (_: Exception) {}

        try {
            socket?.close()
        } catch (_: Exception) {}
        socket = null

        val device = bluetoothAdapter.getRemoteDevice(formattedAddress)
        val devName = try { device.name ?: formattedAddress } catch (_: Exception) { formattedAddress }
        val isHc = isLikelyHcDevice(device)

        lastConnectionError = null

        // Memicu proses pairing (Bonding) jika perangkat belum ter-pair
        if (device.bondState == BluetoothDevice.BOND_NONE) {
            Log.d("BT_HOLDER", "Perangkat $devName belum di-pair (BOND_NONE), memicu createBond()...")
            try {
                device.createBond()
            } catch (e: Exception) {
                Log.w("BT_HOLDER", "createBond error: ${e.message}")
            }
        }

        // Prioritas strategi koneksi:
        // Untuk HC-06/HC-05: Direct Channel 1 Reflection didahulukan agar tidak terkena timeout SDP query (15 detik)
        // Untuk ESP32 / Lainnya: Insecure SPP UUID didahulukan
        val attempts: List<Pair<String, () -> BluetoothSocket>> = if (isHc) {
            listOf(
                "Channel 1 Reflection (Secure - Standar HC-06)" to {
                    val m = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                    m.invoke(device, 1) as BluetoothSocket
                },
                "Channel 1 Reflection (Insecure - Clone HC-06)" to {
                    val m = device.javaClass.getMethod("createInsecureRfcommSocket", Int::class.javaPrimitiveType)
                    m.invoke(device, 1) as BluetoothSocket
                },
                "Secure RFCOMM SPP UUID" to {
                    device.createRfcommSocketToServiceRecord(SPP_UUID)
                },
                "Insecure RFCOMM SPP UUID" to {
                    device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
                }
            )
        } else {
            listOf(
                "Insecure RFCOMM SPP UUID (Standar ESP32)" to {
                    device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
                },
                "Channel 1 Reflection (Secure - Fallback HC)" to {
                    val m = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                    m.invoke(device, 1) as BluetoothSocket
                },
                "Channel 1 Reflection (Insecure - Fallback HC)" to {
                    val m = device.javaClass.getMethod("createInsecureRfcommSocket", Int::class.javaPrimitiveType)
                    m.invoke(device, 1) as BluetoothSocket
                },
                "Secure RFCOMM SPP UUID" to {
                    device.createRfcommSocketToServiceRecord(SPP_UUID)
                }
            )
        }

        val errors = mutableListOf<String>()

        for ((methodName, socketFactory) in attempts) {
            // Pastikan discovery Bluetooth mati sebelum setiap percobaan connect
            try {
                if (bluetoothAdapter.isDiscovering) {
                    bluetoothAdapter.cancelDiscovery()
                }
            } catch (_: Exception) {}

            var s: BluetoothSocket? = null
            try {
                Log.d("BT_HOLDER", "Mencoba koneksi ke $devName ($formattedAddress) dengan: $methodName")
                s = socketFactory()
                s.connect()

                if (s.isConnected) {
                    socket = s
                    connectedDeviceAddress = formattedAddress
                    Log.d("BT_HOLDER", "BERHASIL TERHUBUNG ke $devName via $methodName!")
                    return true
                } else {
                    try { s.close() } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                val err = e.message ?: "Koneksi ditolak/timeout"
                Log.w("BT_HOLDER", "Gagal via $methodName: $err")
                errors.add("$methodName: $err")
                try { s?.close() } catch (_: Exception) {}
            }

            // Jeda 350ms agar stack Bluetooth Android dan modul me-reset state channel
            try {
                Thread.sleep(350)
            } catch (_: Exception) {}
        }

        try {
            socket?.close()
        } catch (_: Exception) {}
        socket = null

        lastConnectionError = if (isHc) {
            "Gagal menghubungkan ke modul $devName ($formattedAddress).\n\n" +
            "Panduan Troubleshooting HC-06:\n" +
            "1. Pastikan modul sudah di-pair di Pengaturan HP dengan PIN 1234 atau 0000.\n" +
            "2. Pastikan lampu LED HC-06 berkedip cepat (tanda standby, bukan nyala diam yang berarti tersambung ke HP lain).\n" +
            "3. Pastikan kabel TX/RX tidak tertukar: TX HC-06 ke Pin 0 (RX) Arduino, RX HC-06 ke Pin 1 (TX) Arduino.\n" +
            "4. Pastikan VCC HC-06 terhubung ke 5V."
        } else {
            "Gagal menghubungkan ke $devName ($formattedAddress): ${errors.joinToString(" | ")}"
        }

        Log.e("BT_HOLDER", "Semua percobaan koneksi gagal ke $formattedAddress: ${errors.joinToString(" | ")}")
        return false
    }

    fun close() {
        try {
            socket?.close()
        } catch (_: Exception) {}
        socket = null
    }

    private fun tryReconnect(): Boolean {
        val adapter = lastBluetoothAdapter ?: return false
        val addr = connectedDeviceAddress ?: return false
        if (addr.isEmpty()) return false
        Log.d("BT_HOLDER", "Mencoba auto-reconnect ke $addr...")
        return connect(adapter, addr)
    }

    val isConnected: Boolean
        get() = (connectionMode == ConnectionMode.WIFI) || (socket?.isConnected == true)

    suspend fun testWifiConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL("$wifiBaseUrl/get-data")
            val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                connectTimeout = 2500
                readTimeout = 2500
                requestMethod = "GET"
                useCaches = false
            }
            val code = conn.responseCode
            conn.disconnect()
            (code in 200..299)
        } catch (e: Exception) {
            Log.w("CONN_HOLDER", "WiFi test connection failed: ${e.message}")
            false
        }
    }

    suspend fun sendData(data: String): Boolean = withContext(Dispatchers.IO) {
        if (connectionMode == ConnectionMode.WIFI) {
            return@withContext try {
                val clean = data.trim()
                val encoded = java.net.URLEncoder.encode(clean, "UTF-8")
                val url = java.net.URL("$wifiBaseUrl/cmd?val=$encoded")
                val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                    connectTimeout = 3000
                    readTimeout = 3000
                    requestMethod = "GET"
                    useCaches = false
                }
                val code = conn.responseCode
                conn.disconnect()
                code in 200..299
            } catch (e: Exception) {
                Log.e("CONN_HOLDER", "Error sending data via WiFi: ${e.message}")
                false
            }
        }

        val dataWithNewline = if (data.endsWith("\n")) data else "$data\n"
        val bytes = dataWithNewline.toByteArray()

        // Percobaan 1: Kirim lewat socket aktif
        try {
            var s = socket
            if (s == null || !s.isConnected) {
                if (tryReconnect()) {
                    s = socket
                }
            }
            val activeSocket = s ?: return@withContext false
            val stream = activeSocket.outputStream ?: return@withContext false
            stream.write(bytes)
            stream.flush()
            return@withContext true
        } catch (e: Exception) {
            Log.w("CONN_HOLDER", "Socket write gagal (${e.message}), mencoba auto-reconnect...")
        }

        // Percobaan 2: Auto-reconnect jika socket mengalami Broken Pipe / terputus
        return@withContext try {
            if (!tryReconnect()) return@withContext false
            val s = socket ?: return@withContext false
            val stream = s.outputStream ?: return@withContext false
            stream.write(bytes)
            stream.flush()
            true
        } catch (e: Exception) {
            Log.e("CONN_HOLDER", "Error sending data via Bluetooth after reconnect: ${e.message}")
            false
        }
    }

    suspend fun sendDataList(
        dataList: List<String>,
        delayBetweenMs: Long = 800L,
        onProgress: ((current: Int, total: Int, item: String) -> Unit)? = null
    ): Boolean = withContext(Dispatchers.IO) {
        if (dataList.isEmpty()) return@withContext true

        if (connectionMode == ConnectionMode.WIFI) {
            return@withContext try {
                for ((index, data) in dataList.withIndex()) {
                    val clean = data.trim()
                    withContext(Dispatchers.Main) {
                        onProgress?.invoke(index + 1, dataList.size, clean)
                    }
                    val encoded = java.net.URLEncoder.encode(clean, "UTF-8")
                    val url = java.net.URL("$wifiBaseUrl/cmd?val=$encoded")
                    val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                        connectTimeout = 3000
                        readTimeout = 3000
                        requestMethod = "GET"
                        useCaches = false
                    }
                    val code = conn.responseCode
                    conn.disconnect()
                    if (code !in 200..299) {
                        Log.w("CONN_HOLDER", "WiFi cmd failed: $code for: $clean")
                        return@withContext false
                    }
                    kotlinx.coroutines.delay(delayBetweenMs)
                }
                true
            } catch (e: Exception) {
                Log.e("CONN_HOLDER", "Error sending data list via WiFi: ${e.message}")
                false
            }
        }

        try {
            var s = socket
            if (s == null || !s.isConnected) {
                if (tryReconnect()) {
                    s = socket
                }
            }
            var activeSocket = s ?: return@withContext false
            var stream = activeSocket.outputStream ?: return@withContext false

            for ((index, data) in dataList.withIndex()) {
                val clean = data.trim()
                withContext(Dispatchers.Main) {
                    onProgress?.invoke(index + 1, dataList.size, clean)
                }
                val dataWithNewline = "$clean\n"
                val bytes = dataWithNewline.toByteArray()
                try {
                    stream.write(bytes)
                    stream.flush()
                } catch (_: Exception) {
                    if (!tryReconnect()) return@withContext false
                    activeSocket = socket ?: return@withContext false
                    stream = activeSocket.outputStream ?: return@withContext false
                    stream.write(bytes)
                    stream.flush()
                }
                kotlinx.coroutines.delay(delayBetweenMs)
            }
            true
        } catch (e: Exception) {
            Log.e("CONN_HOLDER", "Error sending data list via Bluetooth: ${e.message}")
            false
        }
    }
}
