package com.domagojleskovic.bleadvert

import com.google.firebase.firestore.PropertyName

data class User(
    val id : String = "",
    val name : String = "",
    @PropertyName("admin") val isAdmin : Boolean = false
)