package com.domagojleskovic.bleadvert.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domagojleskovic.bleadvert.Beacon
import com.domagojleskovic.bleadvert.DatabaseAccessObject
import com.domagojleskovic.bleadvert.Event
import com.domagojleskovic.bleadvert.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    val scanning = mutableStateOf(false)

    private val _closestBeacon = MutableStateFlow<Beacon?>(null)
    val closestBeacon: StateFlow<Beacon?> = _closestBeacon

    private val _gesturesEnabled = MutableStateFlow(true)
    val gesturesEnabled: StateFlow<Boolean> = _gesturesEnabled

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _activeEvent = MutableStateFlow<Event?>(null)
    val activeEvent: StateFlow<Event?> = _activeEvent

    private val dao = DatabaseAccessObject.getInstance()

    fun setClosestBeacon(beacon: Beacon?) {
        viewModelScope.launch {
            _closestBeacon.emit(beacon)
        }
    }
    fun setCurrentUser(user : User?){
        viewModelScope.launch {
            _currentUser.emit(user)
        }
    }
    fun toggleGesturesEnabled() {
        viewModelScope.launch {
            _gesturesEnabled.emit(!_gesturesEnabled.value)
        }
    }
    fun setActiveEvent(){
        viewModelScope.launch {
            _activeEvent.value = dao.processActiveEvents()
            Log.i("Current event", "${activeEvent.value}")
        }
    }
    fun processUserEventScan(user: User, event: Event, scannedBeacon: Beacon){
        viewModelScope.launch {
            dao.processUserEventScan(
                user,
                event,
                scannedBeacon
            )
        }
    }
}