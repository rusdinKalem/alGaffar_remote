package com.roesch.jwsalgaffar.components

import android.bluetooth.BluetoothSocket
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.roesch.jwsalgaffar.R
import com.roesch.jwsalgaffar.utils.BluetoothConnectionHolder
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Picker(bluetoothSocket: BluetoothSocket? = null) {
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

    var showModalTime by remember { mutableStateOf(false) }
    var showModalDate by remember { mutableStateOf(false) }
    var showEditDate by remember { mutableStateOf(true) }
    var showSelectedDate by remember { mutableStateOf(false) }

    val cal = Calendar.getInstance()
    var selectedTime: TimePickerState? by remember { mutableStateOf(null) }
    val formatter = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val formatterSend = remember { SimpleDateFormat("HHmmss", Locale.getDefault()) }
    val datePickerState = rememberDatePickerState()
    val selectedDate =  datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it) } ?: ""
    val defaultDate = SimpleDateFormat("ddMMyy", Locale.getDefault()).format(cal.time)
    val defaultDay  = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val selectedDateSend =  datePickerState.selectedDateMillis?.let {
        convertMillisToDateSend(it) } ?: defaultDate



    val daySelected = datePickerState.selectedDateMillis?.let {
        convertMillisToDay(it) }?: ""

    val numberDay = when(daySelected){
        "Minggu" ->  "1"
        "Senin" ->  "2"
        "Slasa" ->  "3"
        "Rabu" ->  "4"
        "Kamis" ->  "5"
        "Jumat" ->  "6"
        "Sabtu" ->  "7"
        else -> defaultDay
    }

    Row(
        Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth(),
    ) {
        Column(
            Modifier
                .fillMaxWidth(0.6f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // DATE PICKER
            Box(
                modifier = Modifier
                    .clickable(
                        onClick = {showModalDate = !showModalDate}
                    )
            )
            {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .background(color = Color(0x0C1b1b1b))
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xff1b1b1b)
                        )
                        .padding(horizontal = 5.dp)
                        .height(50.dp)
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (showEditDate){
                        Text(
                            text = "EDIT TANGGAL", fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp, modifier = Modifier.padding(horizontal = 15.dp))
                    } else if (showSelectedDate) {
                        Text(
                            text = selectedDate, fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp, modifier = Modifier.padding(horizontal = 15.dp))
                    }
                    Icon(
                        imageVector = Icons.Rounded.DateRange,
                        contentDescription = "Date",
                        Modifier.size(40.dp)
                    )
                }

                if (showModalDate) {
                    DatePickerDialog(
                        onDismissRequest = { showModalDate = false },
                        confirmButton = {
                            TextButton(onClick = {
                                showModalDate = false
                                showEditDate = false
                                showSelectedDate = true
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showModalDate = false
                                showEditDate = true
                                showSelectedDate = false
                            }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            //======================================================================

            // TIME PICKER

            Box(
                modifier = Modifier
                    .clickable(
                        onClick = {showModalTime=!showModalTime}
                    )
            )
            {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .background(color = Color(0x0C1b1b1b))
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xff1b1b1b)
                        )
                        .padding(horizontal = 5.dp)
                        .height(50.dp)
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (selectedTime != null){
                        cal.set(Calendar.HOUR_OF_DAY, selectedTime!!.hour)
                        cal.set(Calendar.MINUTE, selectedTime!!.minute)
                        cal.isLenient = false
                        Text(
                            text = formatter.format(cal.time), fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp, modifier = Modifier.padding(horizontal = 15.dp))
                    } else {
                        Text(
                            text = "EDIT WAKTU" , fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp, modifier = Modifier.padding(horizontal = 15.dp))
                    }
                    Icon(
                        painter= painterResource(id = R.drawable.a6),
                        contentDescription = "Date",
                        Modifier.size(36.dp)
                    )
                }

                if (showModalTime) {
                    AdvancedTimePickerExample(
                        onDismiss = {
                            showModalTime = false
                        },
                        onConfirm = { time ->
                            selectedTime = time
                            showModalTime = false
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .clickable(
                    onClick = {
                        val currentCal = Calendar.getInstance()
                        if (selectedTime != null) {
                            currentCal.set(Calendar.HOUR_OF_DAY, selectedTime!!.hour)
                            currentCal.set(Calendar.MINUTE, selectedTime!!.minute)
                            currentCal.set(Calendar.SECOND, 0)
                        }
                        val timeStr = formatterSend.format(currentCal.time)
                        val command = "SDT$selectedDateSend$timeStr$numberDay"
                        sendData(command)
                    }
                )
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .background(color = Color.LightGray, shape = RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xff1b1b1b)
                )
                .padding(horizontal = 5.dp)
                .height(120.dp)
            ,
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Rounded.MailOutline,
                contentDescription = "Date",
                Modifier.size(90.dp)
            )
            Text(
                    text = "KIRIM", fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp, modifier = Modifier.padding(horizontal = 15.dp))
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_advanced]
@Composable
fun AdvancedTimePickerExample(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {

    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    /** Determines whether the time picker is dial or input */
    var showDial by remember { mutableStateOf(true) }

    /** The icon used for the icon button that switches from dial to input */
    val toggleIcon = if (showDial) {
        Icons.Filled.Edit
    } else {
        Icons.Filled.DateRange
    }

    AdvancedTimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) },
        toggle = {
            IconButton(onClick = { showDial = !showDial }) {
                Icon(
                    imageVector = toggleIcon,
                    contentDescription = "Time picker type toggle",
                )
            }
        },
    ) {
        if (showDial) {
            androidx.compose.material3.TimePicker(
                state = timePickerState,
            )
        } else {
            TimeInput(
                state = timePickerState,
            )
        }
    }
}

@Composable
fun AdvancedTimePickerDialog(
    title: String = "Select Time",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
            Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
fun convertMillisToDateSend(millis: Long): String {
    val formatter = SimpleDateFormat("ddMMyy", Locale.getDefault())
    return formatter.format(Date(millis))
}
fun convertMillisToDay(millis: Long): String {
    val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
    return formatter.format(Date(millis))
}
