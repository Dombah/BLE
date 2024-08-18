package com.domagojleskovic.bleadvert

import android.net.Uri

data class Reward(
    val title: String = "",
    val description: String = "",
    var image: Uri = Uri.EMPTY,
    val requiredScans: Int = 0,
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "description" to description,
            "image" to image.toString(),
            "requiredScans" to requiredScans
        )
    }
}

