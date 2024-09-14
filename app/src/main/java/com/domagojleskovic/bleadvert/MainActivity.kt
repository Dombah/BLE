package com.domagojleskovic.bleadvert

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.domagojleskovic.bleadvert.ui.HomeScreen
import com.domagojleskovic.bleadvert.ui.LoginScreen
import com.domagojleskovic.bleadvert.ui.RegisterScreen
import com.domagojleskovic.bleadvert.ui.theme.BLEAdvertTheme
import com.domagojleskovic.bleadvert.viewmodels.AdminViewModel
import com.domagojleskovic.bleadvert.viewmodels.ModifyBeaconsViewModel
import com.domagojleskovic.bleadvert.viewmodels.RewardsViewModel
import com.domagojleskovic.bleadvert.viewmodels.ScannedHistoryViewModel
import com.domagojleskovic.bleadvert.viewmodels.SharedViewModel
import kotlinx.coroutines.launch
import java.math.RoundingMode
import kotlin.math.log
import kotlin.math.pow


class MainActivity : ComponentActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private lateinit var bluetoothManager: BluetoothManager
    // private val EDDYSTONE_SERVICE_UUID = ParcelUuid(UUID.fromString("0000FEAA-0000-1000-8000-00805F9B34FB"))
    private val scanSetting = ScanSettings.SCAN_MODE_LOW_LATENCY

    private val sharedViewModel: SharedViewModel by viewModels()
    private fun createWithFactory(
        create: () -> ViewModel
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST") // Casting T as ViewModel
                return create.invoke() as T
            }
        }
    }
    private val databaseAccessObject = DatabaseAccessObject.getInstance()
    private val modifyBeaconViewModel: ModifyBeaconsViewModel by lazy {
        ViewModelProvider(
            this,
            createWithFactory {
                ModifyBeaconsViewModel(databaseAccessObject)
            }
        )[ModifyBeaconsViewModel::class.java]
    }
    private val rewardsViewModel: RewardsViewModel by lazy {
        ViewModelProvider(
            this,
            createWithFactory {
                RewardsViewModel(databaseAccessObject)
            }
        )[RewardsViewModel::class.java]
    }

    private val adminViewModel: AdminViewModel by lazy {
        ViewModelProvider(
            this,
            createWithFactory {
                AdminViewModel(databaseAccessObject)
            }
        )[AdminViewModel::class.java]
    }
    private val scannedHistoryViewModel: ScannedHistoryViewModel by lazy {
        ViewModelProvider(
            this,
            createWithFactory {
                ScannedHistoryViewModel(databaseAccessObject)
            }
        )[ScannedHistoryViewModel::class.java]
    }

    var beacons = mutableListOf<Beacon>()

    private fun initializeBluetooth(){
        try {
            bluetoothManager = getSystemService(BluetoothManager::class.java)
            bluetoothAdapter = bluetoothManager.adapter
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        }catch(e : Exception) {
            Log.i("MainActivity","Failed initializing bluetooth\n$e")
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initializeBluetooth()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.setActiveEvent()
                modifyBeaconViewModel.beacons.collect { newBeacons ->
                    // Update the local variable whenever the beacons change
                    beacons = newBeacons.toMutableList()
                }
            }
        }
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            BLEAdvertTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val emailPasswordAuthenticator = EmailPasswordAuthenticator.getInstance()
                    NavHost(
                        navController = navController, startDestination = "login",
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None }
                    ){
                        composable("login",
                            enterTransition = {
                                fadeIn(
                                    animationSpec = tween(
                                        300, easing = LinearEasing
                                    )
                                )
                            },
                        ){
                            LoginScreen(
                                onNavigateRegisterScreen = {
                                    navController.navigate("register")
                                    rewardsViewModel.fetchRewards(EmailPasswordAuthenticator.currentUser)
                                    scannedHistoryViewModel.fetchUserScannedHistory(EmailPasswordAuthenticator.currentUser)
                                },
                                onLoginSuccess = {
                                    navController.navigate("home_screen?source=login")
                                    rewardsViewModel.fetchRewards(EmailPasswordAuthenticator.currentUser)
                                    scannedHistoryViewModel.fetchUserScannedHistory(EmailPasswordAuthenticator.currentUser)
                                },
                                emailPasswordAuthenticator = emailPasswordAuthenticator,
                            )
                        }
                        composable("register",
                            enterTransition = {
                                fadeIn(
                                    animationSpec = tween(
                                        300, easing = LinearEasing
                                    )
                                )
                            },
                        ){
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("home_screen?source=register")
                                    rewardsViewModel.fetchRewards(EmailPasswordAuthenticator.currentUser)
                                },
                                onNavigateLoginScreen = {
                                    navController.popBackStack()
                                },
                                onSignInAsGuest = {
                                    navController.navigate("home_screen?source=guest")
                                },
                                emailPasswordAuthenticator = emailPasswordAuthenticator
                            )
                        }
                        composable(
                            "home_screen?source={source}",
                            arguments = listOf(navArgument("source") { defaultValue = "unknown" }),
                            enterTransition = {
                                fadeIn(
                                    animationSpec = tween(
                                        300, easing = LinearEasing
                                    )
                                )
                            },

                        ){ backStackEntry ->
                            val source = backStackEntry.arguments?.getString("source")
                            HomeScreen(
                                modifyBeaconsViewModel = modifyBeaconViewModel,
                                rewardsViewModel = rewardsViewModel,
                                onSignOut = {
                                    navController.navigate("login")
                                    emailPasswordAuthenticator.signOut()
                                    scannedHistoryViewModel.reset()
                                },
                                sharedViewModel = sharedViewModel,
                                adminViewModel = adminViewModel,
                                scannedHistoryViewModel = scannedHistoryViewModel,
                                toggleScanning = { toggleScanning() },
                                source = source
                            )
                        }
                    }
                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val deviceAddress = result.device.address
            if(beacons.any{it.address == deviceAddress}){
                result.let { found ->
                    // val device = found.device
                    val rssi = found.rssi
                    // val record = found.scanRecord
                    // val beaconUUID = record?.serviceUuids
                    // val url = parseUrlFromScanRecord(record)
                    // Log.i("DeviceURL", "${found.device.address}: $url")
                    val beacon = beacons.find { it.address == deviceAddress }
                    // if(beacon?.url?.isEmpty() == true) modifyBeaconViewModel.setBeaconUrl(beacon, url)
                    val distance = getDistance(rssi)
                    modifyBeaconViewModel.updateDistances(beacon, distance)
                    // Log.i("Beacon", "$beacon")
                    val avgDistance = modifyBeaconViewModel.averageDistance(beacon)
                    // Log.d("Beacons","Beacon ${beacon?.address} - Average Distance: $avgDistance meters")
                }
            }
        }
    }
    /*
    private fun parseUrlFromScanRecord(scanRecord: ScanRecord?): String? {
        val serviceData = scanRecord?.getServiceData(EDDYSTONE_SERVICE_UUID)
        serviceData?.let {
            if (it[0] == 0x10.toByte()) { // Eddystone-URL frame type
                val urlScheme = decodeUrlScheme(it[2])
                val url = decodeUrl(it.copyOfRange(3, it.size))
                return "$urlScheme$url"
            }
        }
        return null
    }*/
    fun getDistance(measuredRssi: Int): Double {
        val n = 2.0
        val referenceRssi = -59.0
        val rawMeasure = (10.0.pow((referenceRssi - measuredRssi) / (10 * n)))
        val roundedUp = rawMeasure.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
        return roundedUp
    }
    /*
    private fun decodeUrlScheme(b: Byte): String {
        return when (b.toInt()) {
            0x00 -> "http://www."
            0x01 -> "https://www."
            0x02 -> "http://"
            0x03 -> "https://"
            else -> ""
        }
    }

    private fun decodeUrl(data: ByteArray): String {
        val urlBuilder = StringBuilder()
        data.forEach { byte ->
            urlBuilder.append(
                when (byte.toInt()) {
                    0x00 -> ".com/"
                    0x01 -> ".org/"
                    0x02 -> ".edu/"
                    0x03 -> ".net/"
                    0x04 -> ".info/"
                    0x05 -> ".biz/"
                    0x06 -> ".gov/"
                    0x07 -> ".com"
                    0x08 -> ".org"
                    0x09 -> ".edu"
                    0x0A -> ".net"
                    0x0B -> ".info"
                    0x0C -> ".biz"
                    0x0D -> ".gov"
                    else -> String(byteArrayOf(byte), StandardCharsets.US_ASCII)
                }
            )
        }
        return urlBuilder.toString()
    }
     */
    @SuppressLint("MissingPermission")
    fun startScanning() {
        val scanSettings = ScanSettings.Builder()
            .setScanMode(scanSetting)
            .build()
        bluetoothLeScanner.startScan(null,scanSettings, scanCallback)
    }
    @SuppressLint("MissingPermission")
    fun stopScanning(){
        bluetoothLeScanner.stopScan(scanCallback)
    }

    private fun toggleScanning() {
        if (sharedViewModel.scanning.value) {
            stopScanning()
        } else {
            startScanning()
        }
        sharedViewModel.scanning.value = !sharedViewModel.scanning.value
    }
}

