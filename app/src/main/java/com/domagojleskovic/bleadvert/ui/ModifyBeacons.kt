package com.domagojleskovic.bleadvert.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.domagojleskovic.bleadvert.Beacon
import com.domagojleskovic.bleadvert.ui.theme.DarkGrayishBlue
import com.domagojleskovic.bleadvert.ui.theme.DarkSurface
import com.domagojleskovic.bleadvert.ui.theme.MainBlue
import com.domagojleskovic.bleadvert.ui.theme.Orange
import com.domagojleskovic.bleadvert.ui.theme.Typography
import com.domagojleskovic.bleadvert.viewmodels.ModifyBeaconsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BeaconItem(
    beacon : Beacon,
    isInDarkMode : Boolean,
    onDeleteBeacon : (Beacon) -> Unit,
    onUpdateBeacon : (Beacon) -> Unit
    ) {

    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(
                onClick = { isExpanded = !isExpanded },
                onLongClick = {
                    onDeleteBeacon(beacon)
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if(isInDarkMode)
                DarkGrayishBlue else Color(0xFFE3E3E3)
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = beacon.address,
                    style = Typography.headlineMedium.copy(fontSize = 16.sp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "URL: ${beacon.url}",
                        style = Typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Coordinates (${beacon.x}, ${beacon.y})",
                        style = Typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Maximum distance: ${beacon.maximumAdvertisementDistance}",
                        style = Typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                    Button(
                        onClick = {
                            onUpdateBeacon(beacon)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(isInDarkMode) Orange.copy(alpha = 0.6f) else MainBlue
                        ),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 64.dp)
                    ) {
                        Text(
                            text = "Edit",
                            style = Typography.labelMedium.copy(fontSize = 16.sp),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ModifyBeacons(
    innerPadding: PaddingValues,
    modifyBeaconsViewModel: ModifyBeaconsViewModel
) {

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val uiColor = if(isSystemInDarkTheme) Color.White else Color.Black
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var updateBeacon by remember { mutableStateOf<Beacon?>(null) }
    var deleteBeacon by remember { mutableStateOf<Beacon?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val beacons by modifyBeaconsViewModel.beacons.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    if(isLoading){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(96.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if(isSystemInDarkTheme) DarkSurface else Color.White)
        ) {
            PageName("Beacons")
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(top = 175.dp)
                    .consumeWindowInsets(innerPadding)
                    .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(beacons) { beacon ->
                    BeaconItem(
                        beacon = beacon,
                        isInDarkMode = isSystemInDarkTheme,
                        onDeleteBeacon = {
                            deleteBeacon = it
                            showDeleteDialog = true
                        },
                        onUpdateBeacon = {
                            updateBeacon = it
                            showUpdateDialog = true
                        }
                    )
                }
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                showAddDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if(isSystemInDarkTheme) Orange.copy(alpha = 0.6f) else MainBlue
                            ),
                        ) {
                            Text(
                                text = "Add beacon",
                                color = Color.White,
                                style = Typography.labelMedium.copy(fontSize = 16.sp),
                            )
                        }
                    }
                }
            }
            if (showDeleteDialog && deleteBeacon != null) {
                DeleteBeaconDialog(
                    onDismiss = { showDeleteDialog = false},
                    onConfirm = {
                        isLoading = true
                        modifyBeaconsViewModel.deleteBeacon(
                            context = context,
                            beacon = deleteBeacon ?: Beacon()
                        ) {
                            deleteBeacon = null
                        }
                        isLoading = false
                    }
                )
            }
            if(showAddDialog){
                BeaconDialog(
                    dialogTitle = "Add Beacon",
                    buttonTitle = "Add",
                    onDismiss = { showAddDialog = false },
                    onConfirm = { address, url, x, y, max ->
                        isLoading = true
                        modifyBeaconsViewModel.addBeacon(
                            context = context,
                            beacon = Beacon(
                                address = address,
                                url = url,
                                x = x,
                                y = y,
                                maximumAdvertisementDistance = max
                            )
                        ){
                            isLoading = false
                        }
                    }
                )
            }
            if(showUpdateDialog){
                BeaconDialog(
                    beacon = updateBeacon,
                    dialogTitle = "Update Beacon",
                    buttonTitle = "Update",
                    onDismiss = { showUpdateDialog = false },
                    onConfirm = { address, url, x, y, max ->
                        isLoading = true
                        modifyBeaconsViewModel.updateBeacon(
                            context = context,
                            oldBeacon = updateBeacon ?: Beacon(),
                            newBeacon = Beacon(
                                address = address,
                                url = url,
                                x = x,
                                y = y,
                                maximumAdvertisementDistance = max
                            )
                        ){
                            updateBeacon = null
                        }
                        isLoading = false
                    }
                )
            }
        }
    }
}

@Composable
fun DeleteBeaconDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text(
                    "Delete",
                    style = Typography.labelMedium.copy(fontSize = 16.sp)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    "Cancel",
                    style = Typography.labelMedium.copy(fontSize = 16.sp)
                )
            }
        },
        title = {
            Text(
                "Delete Beacon",
                style = Typography.titleMedium.copy(fontSize = 24.sp)
            )
        },
        text = {
            Text(
                "Do you really want to delete this beacon?",
                style = Typography.labelMedium.copy(fontSize = 16.sp)
            )
        }
    )
}


@Composable
fun BeaconDialog(
    beacon: Beacon? = null, // Beacon from which the data is drawn if updating
    dialogTitle: String,
    buttonTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, Double, Double) -> Unit
) {

    val context = LocalContext.current
    var address by remember { mutableStateOf(beacon?.address ?: "") }
    var url by remember { mutableStateOf(beacon?.url ?: "") }

    var xCordsString by remember { mutableStateOf(beacon?.x.toString()) }
    var yCordsString by remember { mutableStateOf(beacon?.y.toString()) }
    var maxAdvertiseRangeString by remember { mutableStateOf(beacon?.maximumAdvertisementDistance.toString()) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(32.dp)) {
                Text(
                    text = dialogTitle,
                    style = Typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it.take(17) },
                    label = {
                        Text(
                            "Address",
                            style = Typography.labelMedium.copy(fontSize = 16.sp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = url ,
                    onValueChange = { url = it },
                    label = {
                        Text(
                            "Advertising URL",
                            style = Typography.labelMedium.copy(fontSize = 16.sp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = if(xCordsString == "null") "" else xCordsString,
                    onValueChange = { xCordsString = it },
                    label = {
                        Text(
                            "x",
                            style = Typography.labelMedium.copy(fontSize = 16.sp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = if(yCordsString == "null") "" else yCordsString,
                    onValueChange = { yCordsString = it },
                    label = {
                        Text(
                            "y",
                            style = Typography.labelMedium.copy(fontSize = 16.sp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = if(maxAdvertiseRangeString == "null") "" else maxAdvertiseRangeString,
                    onValueChange = { maxAdvertiseRangeString = it },
                    label = {
                        Text(
                            "Maximum range",
                            style = Typography.labelMedium.copy(fontSize = 16.sp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {

                        val x = xCordsString.toDoubleOrNull()
                        val y = yCordsString.toDoubleOrNull()
                        val max = maxAdvertiseRangeString.toDoubleOrNull()

                        if (x == null || y == null || max == null) {
                            Toast.makeText(context, "Please enter valid numbers for x, y, and max.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        onConfirm(address, url, x, y, max)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(isSystemInDarkTheme()) Orange.copy(alpha = 0.6f) else MainBlue
                    ),
                ) {
                    Text(
                        text = buttonTitle,
                        style = Typography.labelMedium.copy(fontSize = 16.sp),
                        color = Color.White
                    )
                }
            }
        }
    }
}
