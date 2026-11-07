package com.roesch.jwsalgaffar.utils


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.IntentCompat
import android.widget.Toast

class DeviceConnectionReceiver: BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val action = intent.action ?: return
            val device: BluetoothDevice? = IntentCompat.getParcelableExtra(
                intent,
                BluetoothDevice.EXTRA_DEVICE,
                BluetoothDevice::class.java
            )
            val devName = try { device?.name ?: "Bluetooth" } catch (_: Exception) { "Bluetooth" }
            if (action == BluetoothDevice.ACTION_ACL_CONNECTED) {
                Toast.makeText(context, "Terhubung ke $devName", Toast.LENGTH_SHORT).show()
            } else if (action == BluetoothDevice.ACTION_ACL_DISCONNECTED) {
                Toast.makeText(context, "Koneksi $devName terputus!", Toast.LENGTH_SHORT).show()
            }
        } catch (_: Exception) {}
    }
}
