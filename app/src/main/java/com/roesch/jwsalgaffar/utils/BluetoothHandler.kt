package com.roesch.jwsalgaffar.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothHandler(
    private val bluetoothAdapter: BluetoothAdapter,
    private val macAddress: String
) {
    fun createSocket(device: BluetoothDevice): BluetoothSocket {
        return BluetoothConnectionHolder.createSocket(device)
    }

    fun connect(): Boolean {
        return BluetoothConnectionHolder.connect(bluetoothAdapter, macAddress)
    }
}