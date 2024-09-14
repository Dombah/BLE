package com.domagojleskovic.bleadvert.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domagojleskovic.bleadvert.DatabaseAccessObject
import com.domagojleskovic.bleadvert.Event
import com.domagojleskovic.bleadvert.Reward
import com.domagojleskovic.bleadvert.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScannedHistoryViewModel(
    private val databaseAccessObject: DatabaseAccessObject
) : ViewModel() {
    private val _history = MutableStateFlow<List<Event>>(emptyList())
    val history: StateFlow<List<Event>> get() = _history

    fun fetchUserScannedHistory(user : User?){
        databaseAccessObject.fetchUserScannedHistory(
            user,
            onSuccess = { result ->
                _history.value = result
                Log.i("ScannedHistoryViewModel", "Fetched scanned history: $result")
            },
        )
    }
    fun reset(){
        _history.value = emptyList()
    }
}