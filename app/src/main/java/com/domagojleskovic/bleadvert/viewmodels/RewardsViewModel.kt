package com.domagojleskovic.bleadvert.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.domagojleskovic.bleadvert.Beacon
import com.domagojleskovic.bleadvert.DatabaseAccessObject
import com.domagojleskovic.bleadvert.Reward
import com.domagojleskovic.bleadvert.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RewardsViewModel(
    private val databaseAccessObject: DatabaseAccessObject
) : ViewModel() {
    private val _rewards = MutableStateFlow<List<Reward>>(emptyList())
    val rewards: StateFlow<List<Reward>> get() = _rewards

    fun fetchRewards(user : User){
        databaseAccessObject.fetchRewards(
            user,
            onSuccess = { result ->
                _rewards.value = result
            },
            onFailure = { exception ->
                Log.e("RewardsViewModel", "Error fetching rewards", exception)
            }
        )
    }
    /*
    fun addRewardTo(context: Context,userID : String){
        databaseAccessObject.addRewardToUser(
            context,
            userID,
            Reward(name = "test", points = 100),
            onSuccess = {
                Log.d("RewardsViewModel", "Reward added successfully")
            },
            onFailure = { exception ->
                Log.e("RewardsViewModel", "Error adding reward", exception)
            }
        )
    }
    fun giveRewardToRandomUser(context: Context, reward: Reward){
        databaseAccessObject.giveRewardToRandomUser(
            context,
            reward,
            onSuccess = {
                Log.d("RewardsViewModel", "Reward added successfully")
            },
            onFailure = { exception ->
                Log.e("RewardsViewModel", "Error adding reward", exception)
            }
        )
    }*/
}