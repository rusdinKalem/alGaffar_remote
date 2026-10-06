package com.roesch.jwsalgaffar.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
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

    val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    @SuppressLint("MissingPermission")
    fun createSocket(device: BluetoothDevice): BluetoothSocket {
        // Method 1: Reflection on Channel 1 (paling ampuh untuk HC-06 & clone)
        try {
            val method = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
            return method.invoke(device, 1) as BluetoothSocket
        } catch (_: Exception) {}

        // Method 2: Insecure RFCOMM
        try {
            return device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
        } catch (_: Exception) {}

        // Method 3: Secure RFCOMM
        return device.createRfcommSocketToServiceRecord(SPP_UUID)
    }

    @SuppressLint("MissingPermission")
    fun connect(bluetoothAdapter: BluetoothAdapter, address: String): Boolean {
        try {
            bluetoothAdapter.cancelDiscovery()
            Thread.sleep(300)
        } catch (_: Exception) {}

        close()

        val formattedAddress = address.uppercase()
        val device = bluetoothAdapter.getRemoteDevice(formattedAddress)

        // Urutan 1: Reflection Direct Channel 1 (Bypass SDP query - Paling ampuh untuk modul HC-06)
        try {
            val method = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
            val s = method.invoke(device, 1) as BluetoothSocket
            s.connect()
            socket = s
            connectedDeviceAddress = formattedAddress
            Log.d("BT_HOLDER", "Berhasil terhubung via Channel 1 Reflection ke ${device.name ?: formattedAddress}")
            return true
        } catch (e1: Exception) {
            Log.w("BT_HOLDER", "Reflection Channel 1 gagal (${e1.message}), mencoba Insecure RFCOMM...")
        }

        // Urutan 2: Insecure RFCOMM (Sangat stabil untuk Android 11-14)
        try {
            val s = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
            s.connect()
            socket = s
            connectedDeviceAddress = formattedAddress
            Log.d("BT_HOLDER", "Berhasil terhubung via Insecure RFCOMM ke ${device.name ?: formattedAddress}")
            return true
        } catch (e2: Exception) {
            Log.w("BT_HOLDER", "Insecure RFCOMM gagal (${e2.message}), mencoba Secure RFCOMM...")
        }

        // Urutan 3: Secure RFCOMM (Metode standar)
        try {
            val s = device.createRfcommSocketToServiceRecord(SPP_UUID)
            s.connect()
            socket = s
            connectedDeviceAddress = formattedAddress
            Log.d("BT_HOLDER", "Berhasil terhubung via Secure RFCOMM ke ${device.name ?: formattedAddress}")
            return true
        } catch (e3: Exception) {
            Log.e("BT_HOLDER", "Semua metode koneksi gagal: ${e3.message}")
            close()
            return false
        }
    }

    fun close() {
        try {
            socket?.close()
        } catch (_: Exception) {}
        socket = null
        connectedDeviceAddress = null
    }

    val isConnected: Boolean
        get() = if (connectionMode == ConnectionMode.WIFI) true else (socket?.isConnected == true)

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
            code in 200..299
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

        try {
            val s = socket ?: return@withContext false
            val stream = s.outputStream ?: return@withContext false
            val dataWithNewline = if (data.endsWith("\n")) data else "$data\n"
            stream.write(dataWithNewline.toByteArray())
            stream.flush()
            true
        } catch (e: Exception) {
            Log.e("CONN_HOLDER", "Error sending data via Bluetooth: ${e.message}")
            false
        }
    }

    suspend fun sendDataList(dataList: List<String>): Boolean = withContext(Dispatchers.IO) {
        if (connectionMode == ConnectionMode.WIFI) {
            return@withContext try {
                for (data in dataList) {
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
                    if (code !in 200..299) {
                        Log.w("CONN_HOLDER", "WiFi cmd failed: $code for: $clean")
                        return@withContext false
                    }
                    kotlinx.coroutines.delay(50)
                }
                true
            } catch (e: Exception) {
                Log.e("CONN_HOLDER", "Error sending data list via WiFi: ${e.message}")
                false
            }
        }

        try {
            val s = socket ?: return@withContext false
            val stream = s.outputStream ?: return@withContext false
            for (data in dataList) {
                val dataWithNewline = if (data.endsWith("\n")) data else "$data\n"
                stream.write(dataWithNewline.toByteArray())
                stream.flush()
                kotlinx.coroutines.delay(50)
            }
            true
        } catch (e: Exception) {
            Log.e("CONN_HOLDER", "Error sending data list via Bluetooth: ${e.message}")
            false
        }
    }
}
