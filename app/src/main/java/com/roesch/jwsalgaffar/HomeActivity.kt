package com.roesch.jwsalgaffar

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.roesch.jwsalgaffar.screens.HomeScreen
import com.roesch.jwsalgaffar.screens.Screen01
import com.roesch.jwsalgaffar.screens.Screen02
import com.roesch.jwsalgaffar.screens.Screen03
import com.roesch.jwsalgaffar.screens.Screen04
import com.roesch.jwsalgaffar.screens.Screen05
import com.roesch.jwsalgaffar.screens.Screen06
import com.roesch.jwsalgaffar.screens.Screen07
import com.roesch.jwsalgaffar.screens.Screen08
import com.roesch.jwsalgaffar.screens.ScreenMp3
import com.roesch.jwsalgaffar.ui.theme.JWSAlGaffarTheme
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import com.roesch.jwsalgaffar.utils.BluetoothHandler

class HomeActivity : ComponentActivity() {

    private val permissions = arrayOf(
    android.Manifest.permission.ACCESS_COARSE_LOCATION,
    android.Manifest.permission.ACCESS_FINE_LOCATION,
    )

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var address: String = ""
    private var bluetoothSocket: BluetoothSocket? = null

    private  lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequired : Boolean = false

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationCallback.let {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000
            )
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(1000)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mode = intent.getStringExtra("Mode") ?: "BLUETOOTH"
        if (mode == "WIFI") {
            com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder.connectionMode = com.roesch.jwsalgaffar.utils.ConnectionMode.WIFI
        } else {
            com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder.connectionMode = com.roesch.jwsalgaffar.utils.ConnectionMode.BLUETOOTH
            val btManager = getSystemService(BluetoothManager::class.java)
            if (btManager != null) {
                bluetoothAdapter = btManager.adapter
            }
            address = intent.getStringExtra("Address") ?: ""

            val activeSocket = com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder.socket
            if (activeSocket != null && activeSocket.isConnected) {
                bluetoothSocket = activeSocket
            } else if (address.isNotEmpty() && ::bluetoothAdapter.isInitialized) {
                val device = bluetoothAdapter.getRemoteDevice(address)
                bluetoothSocket = BluetoothConnectionHolder.createSocket(device)
                Thread {
                    val success = BluetoothConnectionHolder.connect(bluetoothAdapter, address)
                    if (success) {
                        BluetoothConnectionHolder.socket?.let {
                            bluetoothSocket = it
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "Koneksi Bluetooth terputus.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }
        }

        enableEdgeToEdge()

        setContent {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            var currentLocation by remember { mutableStateOf(LatLng(0.toDouble(),0.toDouble())) }

            locationCallback = object : LocationCallback(){
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    for (location in p0.locations) {
                        currentLocation = LatLng(
                            location.latitude,
                            location.longitude
                        )
                    }
                }
            }

            val launchMultiplePermissions =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) {
                        permissionMaps ->
                    val areGranted = permissionMaps.values.reduce{ _, next -> next}
                    if(areGranted){
                        locationRequired = true
                        startLocationUpdates()
                        Toast.makeText(this,"Permission Granted", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this,"Permission Not Granted", Toast.LENGTH_LONG).show()
                    }

                }
            LaunchedEffect(fusedLocationClient) {
                if (permissions.all {
                        ContextCompat.checkSelfPermission(
                            this@HomeActivity,
                            it
                        ) == PackageManager.PERMISSION_GRANTED
                    }) {
                    startLocationUpdates()
                } else {
                    launchMultiplePermissions.launch(permissions)
                }
            }



            JWSAlGaffarTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Nav(
                        modifier = Modifier.padding(innerPadding),
                        bluetoothSocket, currentLocation
                    )
                }
            }
        }
    }
}

@Composable
fun Nav(modifier: Modifier, bluetoothSocket: BluetoothSocket?, currentLocation: LatLng) {
    val navController = rememberNavController()
    val scrollState = rememberScrollState()
    NavHost(navController = navController, startDestination = "Home", modifier = modifier) {

       composable(route="Home"){
            HomeScreen(navController)
        }
        composable(route="01"){
            Screen01(navController, bluetoothSocket)
        }
        composable(route="02"){
            Screen02(navController, bluetoothSocket, currentLocation )
        }
        composable(route="03"){
            Screen03(navController, bluetoothSocket)
        }
        composable(route="04"){
            Screen04(navController, bluetoothSocket)
        }
        composable(route="05"){
            Screen05(navController, bluetoothSocket)
        }
        composable(route="06"){
            Screen06(navController, bluetoothSocket)
        }
        composable(route="07"){
            Screen07(navController, bluetoothSocket, scrollState)
        }
        composable(route="08"){
            Screen08(navController, bluetoothSocket)
        }
        composable(route="10"){
            ScreenMp3(navController, bluetoothSocket)
        }
    }
}

