package com.domagojleskovic.bleadvert

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.PropertyName

data class User(
    @PropertyName("id") val id : String = "",
    @PropertyName("name") val name : String = "",
    @PropertyName("admin") val isAdmin : Boolean = false,
    @PropertyName("scans") var scans : Int = 0,
    @PropertyName("rewards") val rewards : MutableList<Reward> = mutableListOf()
){
    companion object {
        fun parseFrom(document: DocumentSnapshot) : User{
            val id = document.getString("id") ?: ""
            val name = document.getString("name") ?: ""
            val isAdmin = document.getBoolean("admin") ?: false
            val scans = document.getLong("scans")?.toInt() ?: 0
            val rewards = Reward.parseFrom(document)

            return User(
                id = id,
                name = name,
                isAdmin = isAdmin,
                scans = scans,
                rewards = rewards
            )
        }
    }
}