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
        val action = intent.action
        val device: BluetoothDevice? = IntentCompat.getParcelableExtra(
            intent,
            BluetoothDevice.EXTRA_DEVICE,
            BluetoothDevice::class.java
        )
        if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
            Toast.makeText(context, "Connected to ${device?.name}", Toast.LENGTH_SHORT).show()
        } else if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
            Toast.makeText(context, "${device?.name} disconnected!", Toast.LENGTH_SHORT).show()
        }
    }
}
