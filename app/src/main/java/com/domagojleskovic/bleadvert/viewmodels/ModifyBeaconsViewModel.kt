package com.domagojleskovic.bleadvert.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImagePainter
import com.domagojleskovic.bleadvert.Beacon
import com.domagojleskovic.bleadvert.DatabaseAccessObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.RoundingMode


class ModifyBeaconsViewModel(
    private val databaseAccessObject: DatabaseAccessObject
) : ViewModel() {
    private val _beacons = MutableStateFlow<List<Beacon>>(emptyList())
    val beacons: StateFlow<List<Beacon>> get() = _beacons

    init {
        databaseAccessObject.fetchBeacons{ result ->
            _beacons.value = result ?: emptyList()
            // Log.i("BeaconCount", _beacons.value.count().toString())
        }
    }

    fun addBeacon(context: Context, beacon: Beacon, onSuccess: () -> Unit) {
        databaseAccessObject.addBeacon(context, beacon){  result ->
            _beacons.value += result
            onSuccess()
        }
    }

    fun updateBeacon(
        context: Context,
        oldBeacon: Beacon,
        newBeacon: Beacon,
        onSuccess: () -> Unit
    ){
        databaseAccessObject.updateBeacon(context, oldBeacon, newBeacon){ result ->
            _beacons.value = result ?: emptyList()
            onSuccess()
        }
    }

    fun deleteBeacon(
        context: Context,
        beacon: Beacon,
        onSuccess: () -> Unit
    ){
        databaseAccessObject.deleteBeacon(context, beacon){
            _beacons.value = _beacons.value.filter { it != beacon }
            onSuccess()
        }
    }

    fun updateDistances(beacon: Beacon?, newDistance: Double) {
        val n = 6
        if (beacon != null && _beacons.value.contains(beacon)) {
            val updatedBeacons = _beacons.value.map {
                if (it == beacon) {
                    val updatedDistances = it.distances.toMutableList()
                    if (updatedDistances.size >= n) {
                        updatedDistances.removeAt(0)
                    }
                    it.copy(distances = updatedDistances.apply { add(newDistance) })
                } else {
                    it
                }
            }
            _beacons.value = updatedBeacons
        }
    }

    fun averageDistance(beacon: Beacon?): Double {
        return try {
            val rawValue = beacon?.distances?.average() ?: 0.0
            rawValue.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
        } catch (e: NumberFormatException) {
            // Log.e("AverageDistanceError", "Error computing average distance for beacon with address: ${beacon?.address}")
            100000.0
        }
    }
}
