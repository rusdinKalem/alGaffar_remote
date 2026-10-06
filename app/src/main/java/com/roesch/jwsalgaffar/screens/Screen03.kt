package com.roesch.jwsalgaffar.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.roesch.jwsalgaffar.components.JudulInfo
import com.roesch.jwsalgaffar.components.Picker
import com.roesch.jwsalgaffar.components.TombolHome
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("SimpleDateFormat")
@Composable
fun Screen03(navController: NavHostController, bluetoothSocket: BluetoothSocket? = null) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun sendData(cmd: String) {
        coroutineScope.launch {
            val success = BluetoothConnectionHolder.sendData(cmd)
            if (success) {
                Toast.makeText(context, "Data Waktu dikirim!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Bluetooth tidak terhubung!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column {

                JudulInfo(
                    judul = "WAKTU DAN TANGGAL",
                    info = "Tekan tombol Refresh untuk menginput Tanggal dan Waktu yang sesuai dengan HP/Device \n" +
                            "Dan tekan tombol Edit Tanggal serta Edit Waktu jika pengaturan secara Manual"
                )

                Column (
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                )   {
                    Text(
                        text = "PENGATURAN OTOMATIS", fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp, modifier = Modifier.padding(horizontal = 15.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    val now = Calendar.getInstance()
                                    val dateStr = SimpleDateFormat("ddMMyyHHmmss", Locale.getDefault()).format(now.time)
                                    val dayNum = now.get(Calendar.DAY_OF_WEEK)
                                    sendData("SDT$dateStr$dayNum")
                                }
                            )
                            .padding(horizontal = 10.dp)
                            .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xff1b1b1b)
                            )
                            .padding(horizontal = 5.dp)
                            .height(150.dp)
                            .width(150.dp)
                        ,
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Date",
                            Modifier.size(100.dp)
                        )
                        Text(
                            text = "REFRESH", fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp, modifier = Modifier.padding(horizontal = 15.dp))
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                    Text(
                        text = "PENGATURAN MANUAL", fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp, modifier = Modifier.padding(horizontal = 15.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Picker(bluetoothSocket)

                    Spacer(modifier = Modifier.height(50.dp))

                    TombolHome(
                        "MENU UTAMA", navController)
                }
            }

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Screen03Preview() {
    val navController = rememberNavController()
    Screen03(navController = navController)
}