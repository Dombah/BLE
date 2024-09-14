package com.domagojleskovic.bleadvert.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Discount
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.SubcomposeAsyncImage
import com.domagojleskovic.bleadvert.Beacon
import com.domagojleskovic.bleadvert.EmailPasswordAuthenticator
import com.domagojleskovic.bleadvert.Event
import com.domagojleskovic.bleadvert.User
import com.domagojleskovic.bleadvert.UserInfoStorage
import com.domagojleskovic.bleadvert.ui.theme.BrightRed
import com.domagojleskovic.bleadvert.ui.theme.DarkSurface
import com.domagojleskovic.bleadvert.ui.theme.LightTeal
import com.domagojleskovic.bleadvert.ui.theme.LightYellow
import com.domagojleskovic.bleadvert.ui.theme.MainBlue
import com.domagojleskovic.bleadvert.ui.theme.OliveGreen
import com.domagojleskovic.bleadvert.ui.theme.Typography
import com.domagojleskovic.bleadvert.viewmodels.AdminViewModel
import com.domagojleskovic.bleadvert.viewmodels.ModifyBeaconsViewModel
import com.domagojleskovic.bleadvert.viewmodels.RewardsViewModel
import com.domagojleskovic.bleadvert.viewmodels.ScannedHistoryViewModel
import com.domagojleskovic.bleadvert.viewmodels.SharedViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val content: @Composable (PaddingValues) -> Unit
)

fun extractNameFromEmail(email: String): String {
    val regex = "^(.*?)@.*$".toRegex()
    val matchResult = regex.find(email)

    return matchResult?.groups?.get(1)?.value?.let {
        it.replaceFirstChar { char -> char.uppercase() }
    }.toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSignOut : () -> Unit,
    modifyBeaconsViewModel: ModifyBeaconsViewModel,
    rewardsViewModel: RewardsViewModel,
    sharedViewModel: SharedViewModel,
    adminViewModel: AdminViewModel,
    scannedHistoryViewModel: ScannedHistoryViewModel,
    toggleScanning: () -> Unit,
    source: String?
) {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val uiColor = if(isSystemInDarkTheme) Color.White else Color.Black
    val currentUser = EmailPasswordAuthenticator.currentUser
    val isAdmin = currentUser?.isAdmin
    val context = LocalContext.current
    val userInfoStorage = UserInfoStorage(context)
    val gesturesEnabled by sharedViewModel.gesturesEnabled.collectAsState()
    val items = listOfNotNull(
        NavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            content = { padding ->
                ScrollContent(
                    innerPadding = padding,
                    toggleScanning = toggleScanning,
                    sharedViewModel = sharedViewModel,
                    currentUser = currentUser,
                    source = source,
                    modifyBeaconsViewModel = modifyBeaconsViewModel,
                    rewardsViewModel = rewardsViewModel
                )
            }
        ),
        if (currentUser?.name != "guest") NavigationItem(
            title = "Scanned History",
            selectedIcon = Icons.Filled.History,
            unselectedIcon = Icons.Outlined.History,
            content = { padding -> ScannedHistory(innerPadding = padding, scannedHistoryViewModel = scannedHistoryViewModel)}
        )else null,
        if (isAdmin == true) NavigationItem(
            title = "Modify Beacons",
            selectedIcon = Icons.Filled.Bluetooth,
            unselectedIcon = Icons.Outlined.Bluetooth,
            content = { padding -> ModifyBeacons(innerPadding = padding, modifyBeaconsViewModel = modifyBeaconsViewModel)}
        ) else null,
        if(isAdmin == true) NavigationItem(
            title = "Admin Panel",
            selectedIcon = Icons.Filled.AdminPanelSettings,
            unselectedIcon = Icons.Outlined.AdminPanelSettings,
            content = { padding -> AdminPanel(innerPadding = padding, adminViewModel) }
        ) else null,
        if(currentUser?.name != "guest") NavigationItem(
            title = "Rewards",
            selectedIcon = Icons.Filled.Discount,
            unselectedIcon = Icons.Outlined.Discount,
            content = { padding -> RewardsScreen(innerPadding = padding, rewardsViewModel = rewardsViewModel) }
        ) else null,
        /*
        NavigationItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            content = { padding -> Settings(innerPadding = padding)}
        ),*/
        NavigationItem(
            title = "Sign out",
            selectedIcon = Icons.Filled.Close,
            unselectedIcon = Icons.Outlined.Close,
            content = {}
        )
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    ModalNavigationDrawer(
        gesturesEnabled = gesturesEnabled || drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                TopAppBar(modifier = Modifier)
                Spacer(modifier = Modifier.height(16.dp))
                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = {
                            Text(
                                color = uiColor,
                                text = item.title,
                                style = if(index != selectedItemIndex)
                                    Typography.labelMedium.copy(fontSize = 16.sp)
                                else
                                    Typography.labelMedium.copy(fontSize = 16.sp).copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            scope.launch {
                                drawerState.close()
                                if(index == items.lastIndex){
                                    userInfoStorage.setEmailAndPassword("","")
                                    onSignOut()
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if(isSystemInDarkTheme) DarkSurface else MainBlue,
                        titleContentColor = Color.White,
                    ),
                    title = {
                        Text(
                            "BLE Advert",
                            style = Typography.headlineMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 30.dp)
                                .padding(vertical = 16.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description",
                                modifier = Modifier.size(30.dp),
                                tint = Color.White
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { innerPadding ->
            if(selectedItemIndex < items.size){
                items[selectedItemIndex].content(innerPadding)
            }
        }
    }
}
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ScrollContent(
    innerPadding: PaddingValues,
    sharedViewModel: SharedViewModel,
    modifyBeaconsViewModel: ModifyBeaconsViewModel,
    currentUser: User?,
    source: String?,
    toggleScanning : () -> Unit,
    rewardsViewModel: RewardsViewModel,
) {

    var showRewardAddedToast by remember {
        mutableStateOf(false)
    }
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val uiColor = if(isSystemInDarkTheme) Color.White else Color.Black
    val isPermissionDialogVisible = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val multiplePermissionLauncher =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions.containsValue(false)) {
                    isPermissionDialogVisible.value = true
                    /*
                    Toast.makeText(
                        context,
                        "At least one of the permissions was not granted. Go to app settings and give permissions manually",
                        Toast.LENGTH_SHORT
                    ).show()*/
                }
                else{
                    toggleScanning()
                    sharedViewModel.toggleGesturesEnabled()
                }
            }
        } else {
            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val coarseLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

                if (coarseLocationGranted) {
                    isPermissionDialogVisible.value = true
                    /*
                    Toast.makeText(
                        context,
                        "You must select the option 'Allow all the time' for the app to work",
                        Toast.LENGTH_SHORT
                    ).show()*/
                } else {
                    Toast.makeText(
                        context,
                        "Location permission was not granted. Please do so manually",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    if (isPermissionDialogVisible.value) {
        PermissionDialog(
            onDismissRequest = { isPermissionDialogVisible.value = false },
            onOkClick = {isPermissionDialogVisible.value = false}
        )
    }
    val requiredPermissionsInitialClient =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

    DisposableEffect(Unit) {
        onDispose {
            if (sharedViewModel.scanning.value) {
                toggleScanning()
                sharedViewModel.toggleGesturesEnabled()
            }
        }
    }
    val currentActiveEvent = sharedViewModel.activeEvent.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val closestBeacon = sharedViewModel.closestBeacon.collectAsState().value
    val beacons by modifyBeaconsViewModel.beacons.collectAsState()
    val noneVirtualBeacon = Beacon(address = "None", maximumAdvertisementDistance = Double.MAX_VALUE)

    val oldClosestBeacon = remember { mutableStateOf<Beacon?>(null) }
    val timeSpentAtBeacon = remember { mutableLongStateOf(0L) }
    val timerJob = remember { mutableStateOf<Job?>(null) }
    LaunchedEffect(sharedViewModel.scanning.value) {
        timeSpentAtBeacon.longValue = 0L

        while (sharedViewModel.scanning.value) {
            val validBeacons = beacons.filter { modifyBeaconsViewModel.averageDistance(it) <= it.maximumAdvertisementDistance }
            val closest = validBeacons.minByOrNull { modifyBeaconsViewModel.averageDistance(it) }

            if (closest?.address != oldClosestBeacon.value?.address) {
                // User has moved to a new beacon or is in a zone with no beacons
                if(closest != null){
                    oldClosestBeacon.value?.let { previousBeacon ->
                        // User is leaving the old beacon; save time spent and cancel the timer
                        timerJob.value?.cancel()
                        Log.i("Previous", "Old: ${previousBeacon.address}\n New: ${closest.address}")
                        // Store time spent at the previous beacon if greater than 5 seconds
                        if (timeSpentAtBeacon.longValue >= 5000) {
                            sharedViewModel.storeUserVisitTime(
                                event = currentActiveEvent!!,
                                beacon = previousBeacon,
                                user = currentUser ?: User(),
                                time = timeSpentAtBeacon.longValue // Pass the time spent
                            )
                        }
                        Log.i("BeaconExit", "User exited beacon: ${previousBeacon.address}")
                        timeSpentAtBeacon.longValue = 0L
                    }
                }

                sharedViewModel.setClosestBeacon(closest ?: noneVirtualBeacon) // Update the closest beacon
                if (closest != null) {
                    // If the new closest beacon is not null and user is not a guest
                    if (currentUser?.name != "guest") {
                        // Reset time spent for the new beacon
                        timeSpentAtBeacon.longValue = 0L

                        // Start accumulating time for the new beacon
                        timerJob.value = coroutineScope.launch {
                            while (true) {
                                delay(1000) // Increment every second
                                timeSpentAtBeacon.longValue += 1000
                                Log.i("TimeSpent", "User has spent ${timeSpentAtBeacon.longValue} ms at beacon: ${closest.address}")
                                // Process the scan after 5 seconds
                                if (timeSpentAtBeacon.longValue >= 5000) {
                                    sharedViewModel.processUserEventScan(
                                        user = currentUser ?: User(),
                                        event = sharedViewModel.activeEvent.value ?: Event(),
                                        scannedBeacon = closest,
                                        rewardsViewModel = rewardsViewModel
                                    ) {
                                        Log.i("Reached", "Should Show")
                                        showRewardAddedToast = true
                                    }
                                    sharedViewModel.storeFirstVisit(
                                        user = currentUser ?: User(),
                                        event = sharedViewModel.activeEvent.value ?: Event(),
                                        beacon = closest,
                                        time = System.currentTimeMillis() - 5000L
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // If no beacon is in range, cancel the timer if it exists
                    timerJob.value?.cancel()
                    Log.i("BeaconExit", "User is in a zone with no beacons.")
                }

                oldClosestBeacon.value = closest // Update the last known closest beacon
            }

            delay(50) // Delay between scans
        }

        // Clean up the timer job if the scanning stops
        timerJob.value?.cancel()
    }
    LaunchedEffect(showRewardAddedToast) {
        if (showRewardAddedToast) {
            val toast = Toast.makeText(context, "Reward added!", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 200)
            toast.show()

            showRewardAddedToast = false
        }
    }

    val shouldDisplayWebView = sharedViewModel.scanning.value && closestBeacon?.url?.isNotEmpty() == true
    if (shouldDisplayWebView) {
        val webView = remember {
            WebView(context).apply {
                settings.javaScriptEnabled = true
            }
        }

        // Update the WebView's URL whenever the closest beacon's URL changes
        closestBeacon?.url?.let { url ->
            LaunchedEffect(url) {
                webView.loadUrl(url)
            }
        }

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
            factory = { webView }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .height(48.dp)
                    .padding(),
                onClick = {
                    oldClosestBeacon.value = null
                    // lastKnownClosestBeacon.value = null
                    toggleScanning()
                    sharedViewModel.toggleGesturesEnabled()

                    if(timeSpentAtBeacon.longValue >= 5000){
                        sharedViewModel.storeUserVisitTime(
                            currentActiveEvent!!,
                            closestBeacon!!,
                            user = currentUser!!,
                            time = timeSpentAtBeacon.longValue
                        )
                    }
                    if(showRewardAddedToast){
                        Log.i("Reached", "Should Show")
                        Toast.makeText(context, "Reward added", Toast.LENGTH_SHORT).show()
                        showRewardAddedToast = false
                    }
                    // Cancel the timer job safely
                    timerJob.value?.cancel()
                    timerJob.value = null // Nullify after cancellation to avoid potential issues
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(isSystemInDarkTheme)
                        DarkSurface.copy(alpha = 0.9f) else LightYellow.copy(alpha = .90f),
                    contentColor = uiColor,
                ),
            ) {
                Text(
                    text = "STOP SCANNING",
                    color = uiColor,
                    style = Typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isSystemInDarkTheme) DarkSurface else Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if(isSystemInDarkTheme)
                            BrightRed.copy(alpha = 0.25f) else MainBlue.copy(alpha = 0.75f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = if(source == "login") "Welcome back!" else "Welcome!",
                            color = Color.White,
                            style = Typography.labelMedium.copy(fontSize = 24.sp),

                            )
                        Text(
                            text =
                                if(currentUser?.name?.contains("@") == true)
                                    extractNameFromEmail(currentUser.name)
                                else
                                    currentUser?.name.let {
                                        it?.take(1)?.uppercase() + it?.substring(1)
                                    },
                            color = Color.White,
                            style = Typography.headlineLarge.copy(fontSize = 42.sp),

                            )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 1f)
                        .clip(RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = if(isSystemInDarkTheme)
                            OliveGreen.copy(alpha = 0.8f) else LightTeal.copy(alpha = 0.8f)
                    ),
                ) {
                    if (currentActiveEvent != null) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            SubcomposeAsyncImage(
                                model = currentActiveEvent.uri,
                                contentDescription = "Event Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                loading = {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .size(100.dp)
                                        )
                                    }

                                }
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = "No Events Available",
                                color = Color.White,
                                style = Typography.labelLarge.copy(fontSize = 30.sp),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text(
                                text = "Unfortunately, there are no events active for you to scan at the moment.",
                                color = Color.White.copy(alpha = 0.8f),  // Softer text color for a subtler look
                                style = Typography.labelMedium.copy(fontSize = 20.sp),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "😟",
                                fontSize = 64.sp,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            Text(
                                text = "Please check again later.",
                                color = Color.White,
                                style = Typography.labelMedium.copy(fontSize = 20.sp),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.systemBars.asPaddingValues())
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    modifier = Modifier
                        .height(48.dp)
                        .padding(),
                    onClick = {
                        multiplePermissionLauncher.launch(requiredPermissionsInitialClient)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(isSystemInDarkTheme)
                            DarkSurface.copy(alpha = 0.9f) else LightYellow.copy(alpha = .90f),
                        contentColor = uiColor,
                    ),
                    enabled = currentActiveEvent != null
                    ) {
                    Text(
                        text = if(sharedViewModel.scanning.value) "STOP SCANNING" else "START SCANNING",
                        color = uiColor,
                        style = Typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                    )
                }
            }
        }
    }
}

@Composable
fun TopAppBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .height(160.dp),
        color = if(isSystemInDarkTheme()) BrightRed else MainBlue,
        ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BLE Advert",
                color = Color.White,
                style = Typography.headlineMedium,
                textAlign = TextAlign.Center,
            )

        }
    }
}
