package com.domagojleskovic.bleadvert.ui



import android.app.TimePickerDialog
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.domagojleskovic.bleadvert.Reward
import com.domagojleskovic.bleadvert.ui.theme.Beige
import com.domagojleskovic.bleadvert.ui.theme.BrightRed
import com.domagojleskovic.bleadvert.ui.theme.DarkBlueGreen
import com.domagojleskovic.bleadvert.ui.theme.DarkGrayishBlue
import com.domagojleskovic.bleadvert.ui.theme.DarkSurface
import com.domagojleskovic.bleadvert.ui.theme.LightYellow
import com.domagojleskovic.bleadvert.ui.theme.MainBlue
import com.domagojleskovic.bleadvert.ui.theme.Orange
import com.domagojleskovic.bleadvert.ui.theme.Typography
import com.domagojleskovic.bleadvert.viewmodels.AdminViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AddEvent(onButtonClick: () -> Unit, isInDarkMode : Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(isInDarkMode)
                DarkGrayishBlue else Color(0xFFE3E3E3)
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Add Event",
                    style = Typography.headlineMedium.copy(fontSize = 16.sp),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onButtonClick,
                modifier = Modifier
                    .size(100.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(isInDarkMode) Beige.copy(alpha = 0.5f) else Color(0xFF56AEC3),
                    contentColor = Color.White
                ),
            ) {
                Text(
                    text = "ADD EVENT",
                    style = Typography.labelMedium, // Customize text style
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AddAdmin(
    title : String,
    buttonTitle : String,
    fraction : Float = 0.6f,
    isInDarkMode: Boolean,
    onSubmitName : (String) -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(fraction)
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(isInDarkMode)
                DarkGrayishBlue else Color(0xFFE3E3E3)
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = Typography.headlineMedium.copy(fontSize = 16.sp),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Email:") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),

            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {onSubmitName(name)},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(isInDarkMode) Orange.copy(alpha = 0.6f) else MainBlue
                )
            ) {
                Text(
                    text = buttonTitle,
                    color = Color.White,
                    style = Typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun PageName(
    title: String,
    isInDarkMode: Boolean = isSystemInDarkTheme()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        // Centered Admin Panel Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(
                topStart = 0.dp, topEnd = 0.dp, bottomStart = 16.dp, bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if(isInDarkMode) BrightRed.copy(alpha = 0.7f) else Color(0xFF344C64).copy(alpha = 0.7f)
            )
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = Typography.headlineMedium.copy(fontSize = 20.sp),
                    color = Color.White
                )
            }
        }
    }
}


@Composable
fun AdminPanel(innerPadding: PaddingValues, adminViewModel: AdminViewModel) {

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
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
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isSystemInDarkTheme) DarkSurface else Color.White)
        ) {

            PageName("Admin Page")
            LazyColumn(
                modifier = Modifier
                    .padding(top = 200.dp, start = 8.dp, end = 8.dp)
                    .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
                    .consumeWindowInsets(innerPadding)
                    .fillMaxWidth(),
            ) {
                item {
                    Row {
                        AddAdmin(
                            isInDarkMode = isSystemInDarkTheme,
                            title = "Add admin to user",
                            buttonTitle = "ADD"
                        ) { name ->
                            adminViewModel.addAdminPrivilegeTo(context, name)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        AddEvent(
                            isInDarkMode = isSystemInDarkTheme,
                            onButtonClick = { showDialog = true }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    AddAdmin(
                        isInDarkMode = isSystemInDarkTheme,
                        title = "Revoke admin from user",
                        buttonTitle = "REVOKE",
                        fraction = 1f,
                    ) { name ->
                        adminViewModel.revokeAdminPrivilegeFrom(context, name)
                    }
                }
            }

            if (showDialog) {
                InputDialogWithDateTimePicker(
                    dialogTitle = "Event Details",
                    buttonTitle = "Confirm",
                    setShowDialog = { showDialog = it },
                    onConfirm = { title, description, imageUri, startDate, endDate, rewards ->
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                adminViewModel.addEvent(
                                    context,
                                    title,
                                    description,
                                    imageUri,
                                    startDate,
                                    endDate,
                                    rewards
                                )
                                Toast.makeText(context, "Event added successfully!", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to add event: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun InputDialogWithDateTimePicker(
    dialogTitle: String,
    buttonTitle: String,
    setShowDialog: (Boolean) -> Unit,
    onConfirm: (String, String, String, String, String, List<Reward>) -> Unit
) {

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val uiColor = if(isSystemInDarkTheme) Color.White else Color.Black
    var imageUri by remember { mutableStateOf<Uri?>(Uri.EMPTY) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
        }
    }
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val rewards = remember { mutableStateListOf<Reward>() }
    var showRewardDialog by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { setShowDialog(false) }) {
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
                    value = title,
                    onValueChange = { title = it },
                    label = {
                        Text(
                            "Title",
                            style = Typography.labelMedium.copy(fontSize = 16.sp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text(
                            "Description",
                            style = Typography.labelMedium.copy(fontSize = 16.sp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = imageUri.toString(),
                    onValueChange = { },
                    label = {
                        Text(
                            "Image Uri",
                            style = Typography.labelMedium.copy(fontSize = 16.sp)
                        )
                    },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { launcher.launch("image/*") }) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = "Select image")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { },
                    label = {
                        Text(
                            "Start Date",
                            style = Typography.labelMedium.copy(fontSize = 16.sp)
                        )
                    },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select start date"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { },
                    label = {
                        Text(
                            "End Date",
                            style = Typography.labelMedium.copy(fontSize = 16.sp)
                        )
                    },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showEndDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select end date"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showRewardDialog = true }, // Open secondary dialog
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        if(isSystemInDarkTheme) Orange.copy(alpha = 0.6f) else MainBlue
                    )
                ) {
                    Text(
                        "Add Reward",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 16.sp)
                    )
                }

                rewards.forEach { reward ->
                    Text(
                        text = "${reward.title}: ${reward.description}",
                        style = Typography.labelSmall.copy(fontSize = 16.sp)
                    )
                }

                if (showRewardDialog) {
                    AddRewardDialog(
                        onDismiss = { showRewardDialog = false },
                        onAddReward = { reward ->
                            rewards.add(reward)
                            showRewardDialog = false
                        }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        if(startDate > endDate)
                        {
                            Toast.makeText(context, "Start date cannot be after end date", Toast.LENGTH_SHORT).show()
                            return@Button

                        }
                        onConfirm(title, description, imageUri.toString(), startDate, endDate, rewards)
                        setShowDialog(false)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(isSystemInDarkTheme) Orange.copy(alpha = 0.6f) else MainBlue
                    )
                ) {
                    Text(
                        buttonTitle,
                        style = Typography.labelMedium.copy(fontSize = 16.sp),
                        color = Color.White
                    )
                }
                if (showStartDatePicker) {
                    DateTimePickerModal(
                        onDateTimeSelected = { dateInMillis ->
                            startDate = convertMillisToDateTime(dateInMillis) ?: ""
                        },
                        onDismiss = { showStartDatePicker = false },
                    )
                }

                if (showEndDatePicker) {
                    DateTimePickerModal(
                        onDateTimeSelected = { dateInMillis ->
                            endDate = convertMillisToDateTime(dateInMillis) ?: ""
                        },
                        onDismiss = { showEndDatePicker = false },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerModal(
    onDateTimeSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val currentTime = calendar.timeInMillis
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val minDateInMillis = calendar.timeInMillis
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentTime
    )

    var selectedTime: TimePickerState? by remember { mutableStateOf(null) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateInMillis by remember { mutableStateOf<Long?>(null) }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                selectedDateInMillis = datePickerState.selectedDateMillis
                if (selectedDateInMillis != null && selectedDateInMillis!! >= minDateInMillis) {
                    showTimePicker = true
                } else {
                    Toast.makeText(
                        context,
                        "Cannot select a date before today",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
                Text("Next")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
        )
    }

    if (showTimePicker) {
        TimePickerModal(
            onConfirm = { timeState ->
                selectedTime = timeState
                val selectedDateTime = Calendar.getInstance().apply {
                    timeInMillis = selectedDateInMillis ?: 0L
                    set(Calendar.HOUR_OF_DAY, selectedTime!!.hour)
                    set(Calendar.MINUTE, selectedTime!!.minute)
                }.timeInMillis
                onDateTimeSelected(selectedDateTime)
                onDismiss()
                showTimePicker = false
            },
            onDismiss = {
                showTimePicker = false
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )
    Column {
        TimePickerDialog(
            onDismiss = { onDismiss() },
            onConfirm = { onConfirm(timePickerState) }
        ) {
            TimeInput(
                state = timePickerState,
            )
        }
    }
}
@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
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
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}

fun convertMillisToDateTime(millis: Long): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(millis)
}

@Composable
fun AddRewardDialog(
    onDismiss: () -> Unit,
    onAddReward: (Reward) -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(Uri.EMPTY) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
        }
    }

    var rewardTitle by remember { mutableStateOf("") }
    var rewardDescription by remember { mutableStateOf("") }
    var requiredScans by remember { mutableIntStateOf(0) }

    val incrementScans = {
        requiredScans = (requiredScans + 1).coerceAtLeast(0)
    }

    val decrementScans = {
        requiredScans = (requiredScans - 1).coerceAtLeast(0)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Add Reward", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = rewardTitle,
                    onValueChange = { rewardTitle = it },
                    label = { Text("Reward Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = rewardDescription,
                    onValueChange = { rewardDescription = it },
                    label = { Text("Reward Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = imageUri.toString(),
                    onValueChange = { },
                    label = { Text("Image URI") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { launcher.launch("image/*") }) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = "Select image")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = requiredScans.toString(),
                        onValueChange = { newValue ->
                            newValue.toIntOrNull()?.let {
                                requiredScans = it
                            }
                        },
                        label = { Text("Required Scans") },
                        readOnly = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        IconButton(onClick = incrementScans) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increment")
                        }
                        IconButton(onClick = decrementScans) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrement")
                        }
                    }
                }
                Button(
                    onClick = {
                        val newReward = Reward(title = rewardTitle, description = rewardDescription, image = imageUri!!, requiredScans = requiredScans)
                        onAddReward(newReward)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add")
                }
            }
        }
    }
}
