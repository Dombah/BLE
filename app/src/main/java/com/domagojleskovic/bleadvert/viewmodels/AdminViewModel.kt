package com.domagojleskovic.bleadvert.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.domagojleskovic.bleadvert.DatabaseAccessObject
import com.domagojleskovic.bleadvert.Event
import com.domagojleskovic.bleadvert.Reward


class AdminViewModel(
    private val databaseAccessObject: DatabaseAccessObject
) : ViewModel() {

    fun addAdminPrivilegeTo(context: Context, name: String) {
        databaseAccessObject.addAdminPrivilegeTo(
            context,
            name,
            onSuccess = {

            },
            onFailure = {

            }
        )
    }

    fun revokeAdminPrivilegeFrom(context: Context, name: String) {
        databaseAccessObject.revokeAdminPrivilegeFrom(
            context,
            name,
            onSuccess = {

            },
            onFailure = {

            }
        )
    }

    suspend fun addEvent(
        context: Context,
        title: String,
        description: String,
        imageURI: String,
        startDate: String,
        endDate: String,
        rewards: List<Reward>
    ) {
        databaseAccessObject.addEvent(
            context,
            title,
            description,
            imageURI,
            startDate,
            endDate,
            rewards,
        )
    }
    fun fetchActiveEvents(onSuccess: (List<Event>) -> Unit) {
        databaseAccessObject.fetchActiveEvents(onSuccess)
    }
}
