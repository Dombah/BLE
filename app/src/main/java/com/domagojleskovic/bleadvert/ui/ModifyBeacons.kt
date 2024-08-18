package com.domagojleskovic.bleadvert.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.domagojleskovic.bleadvert.Beacon
import com.domagojleskovic.bleadvert.R
import com.domagojleskovic.bleadvert.viewmodels.ModifyBeaconsViewModel

@Composable
fun ModifyBeacons(
    innerPadding: PaddingValues,
    viewModel: ModifyBeaconsViewModel
) {
    val context = LocalContext.current
    val showAddDialog =  remember { mutableStateOf(false) }
    val (addFields, setAddFields) = remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

    val showUpdateDialog =  remember { mutableStateOf(false) }
    val (updateFields, setUpdateFields) = remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    val updateBeacon = remember { mutableStateOf<Beacon?>(null)}

    val beacons by viewModel.beacons.collectAsState()


    AddBeacon(context, addFields, viewModel) // Add beacon upon change of fields
    updateBeacon.value?.let { UpdateBeacon(context,updateFields, viewModel, it) }

    if (showAddDialog.value) {
        AddBeaconDialog(
            title = "Add Beacon",
            buttonTitle = "Add",
            setShowDialog = { showAddDialog.value = it },
            setFieldData = { setAddFields(it) }
        )
    }
    if(showUpdateDialog.value && updateBeacon.value != null){
        UpdateBeaconDialog(
            title = "Update beacon",
            buttonTitle = "Update",
            beacon = updateBeacon.value!!,
            setShowDialog = {showUpdateDialog.value = it},
            setFieldData = { setUpdateFields(it) }
        )
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(innerPadding)
            .padding(16.dp),
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(beacons){ index, beacon ->
            BeaconItem(index, beacon){
                showUpdateDialog.value = !showUpdateDialog.value
                updateBeacon.value = beacon
            }
        }
        item { 
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        showAddDialog.value = true
                    }
                ) {
                    Text(text = "Add beacon")
                }
            }
        }
    }
}

@Composable
fun AddBeacon(context: Context, fields : List<Pair<String,String>>, viewModel : ModifyBeaconsViewModel) {
    LaunchedEffect(fields) {
        if(fields.isNotEmpty()){
            viewModel.addBeacon(
                context = context,
                Beacon(
                    address = fields[0].second,
                    url = fields[1].second,
                    x = fields[2].second.toDouble(),
                    y = fields[3].second.toDouble(),
                    maximumAdvertisementDistance = fields[4].second.toDouble()
                )
            )
        }
    }
}
@Composable
fun UpdateBeacon(
    context: Context,
    fields : List<Pair<String,String>>,
    viewModel : ModifyBeaconsViewModel,
    oldBeacon: Beacon
)
{

    LaunchedEffect(fields) {
        if(fields.isNotEmpty()){
            Log.i("UpdateBeacon", oldBeacon.toString())
            viewModel.updateBeacon(
                context,
                oldBeacon,
                Beacon(
                    address = fields[0].second,
                    url = fields[1].second,
                    x = fields[2].second.toDouble(),
                    y = fields[3].second.toDouble(),
                    maximumAdvertisementDistance = fields[4].second.toDouble()
                )
            )
        }
    }
}

@Composable
fun BeaconItem(index: Int,
               beacon: Beacon,
               showDialogClicked : () -> Unit,
) {
    Column (
        modifier = Modifier.clickable{
           showDialogClicked()
        }
    ){
        Row {
            Icon(imageVector = Icons.Filled.Bluetooth, contentDescription = null)
            Text(text = "Beacon ${index + 1}: ")
        }
        Text(text = "Address: ${beacon.address}")
        Text(text = "Url: ${beacon.url}")
        Text(text = "Coordinates: (${beacon.x}, ${beacon.y})")
        Text(text = "Maximum advertise distance: ${beacon.maximumAdvertisementDistance}")
    }
}

@Composable
fun AddBeaconDialog(
    title: String,
    buttonTitle : String,
    setShowDialog: (Boolean) -> Unit,
    setFieldData: (List<Pair<String, String>>) -> Unit

) {
    val initialFieldData = listOf(
        Pair("Address", ""),
        Pair("Url", ""),
        Pair("X offset", ""),
        Pair("Y offset", ""),
        Pair("Max adv distance", "")
    )
    val (fields, setFields) = remember { mutableStateOf(initialFieldData) }

    InputDialog(
        title = title,
        buttonTitle = buttonTitle,
        fieldData = fields,
        setShowDialog = setShowDialog,
        setFieldData = { updatedFields ->
            setFields(updatedFields)
            setFieldData(updatedFields)
        }
    )
}

@Composable
fun InputDialog(
    title: String,
    buttonTitle : String,
    fieldData: List<Pair<String, String>>,
    setShowDialog: (Boolean) -> Unit,
    setFieldData: (List<Pair<String, String>>) -> Unit,
    limitAddressTo : Int = 17
) {

    val fields = remember { mutableStateListOf(*fieldData.toTypedArray()) }
    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center){
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Icon(
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = "",
                            tint = colorResource(R.color.black),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .clickable { setShowDialog(false) }
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    fieldData.forEachIndexed { index, _ ->
                        OutlinedTextField(
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                            value = fields[index].second,
                            onValueChange = {
                                fields[index] = Pair(fields[index].first, if(index == 0) it.take(limitAddressTo) else it)
                            },
                            shape = RoundedCornerShape(32.dp),
                            label = {
                                Text(text = "Enter ${fields[index].first}:")
                            },
                            singleLine = true,
                            )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            val errors = mutableListOf<String>()
                            fields.forEachIndexed { index, (_, value) ->
                                if (value.isEmpty()) {
                                    errors.add("Field ${fieldData[index].first} cannot be empty")
                                }
                            }
                            if (errors.isEmpty()) {
                                setFieldData(fields.toList())
                                setShowDialog(false)
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = buttonTitle)
                    }
                    }
                }
            }
        }
}

@Composable
fun UpdateBeaconDialog(
    title : String,
    buttonTitle : String,
    beacon: Beacon,
    setShowDialog : (Boolean) -> Unit,
    setFieldData: (List<Pair<String, String>>) -> Unit
) {
    val address by remember { mutableStateOf(beacon.address) }
    val url by remember { mutableStateOf(beacon.url) }
    val x by remember { mutableStateOf(beacon.x.toString()) }
    val y by remember { mutableStateOf(beacon.y.toString()) }
    val maxAdv by remember { mutableStateOf(beacon.maximumAdvertisementDistance.toString()) }

    val initialFieldData = listOf(
        Pair("Address", address),
        Pair("Url", url),
        Pair("X offset", x),
        Pair("Y offset", y),
        Pair("Max adv distance", maxAdv)
    )
    val (fields, setFields) = remember { mutableStateOf(initialFieldData) }

    InputDialog(
        title = title,
        buttonTitle = buttonTitle,
        fieldData = fields,
        setShowDialog = setShowDialog,
        setFieldData = { updatedFields ->
            setFields(updatedFields)
            setFieldData(updatedFields)
        }
    )
}