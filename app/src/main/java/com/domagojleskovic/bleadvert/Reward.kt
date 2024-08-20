package com.domagojleskovic.bleadvert

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot

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
    companion object {
        fun parseFrom(document: DocumentSnapshot) : MutableList<Reward>{
            val rewards = (document.get("rewards") as? List<*>)?.mapNotNull { item ->
                (item as? Map<String, Any>)?.let { rewardData ->
                    Reward(
                        title = rewardData["title"] as? String ?: "",
                        description = rewardData["description"] as? String ?: "",
                        image = Uri.parse(rewardData["image"] as? String ?: ""),
                        requiredScans = (rewardData["requiredScans"] as? Long ?: 0L).toInt()
                    )
                }
            } ?: emptyList()
            return rewards.toMutableList()
        }
    }
}

