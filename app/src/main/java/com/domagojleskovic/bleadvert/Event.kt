package com.domagojleskovic.bleadvert

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.sql.Time
import java.util.Date

data class Event (
    val id: String = "",
    val title: String = "",
    val description: String = "",
    var uri: Uri = Uri.EMPTY,
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Timestamp = Timestamp.now(),
    val rewards: List<Reward> = emptyList()
){
    companion object {
        fun parseFrom(document: DocumentSnapshot): Event {
            val title = document.getString("title") ?: ""
            val description = document.getString("description") ?: ""
            val imageUri = document.getString("imageUri") ?: ""
            val startDate = document.getDate("startDate")
            val endDate = document.getDate("endDate")
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
            val uri = Uri.parse(imageUri)

            return Event(
                id = document.id,
                title = title,
                description = description,
                uri = uri,
                startDate = startDate?.let { Timestamp(it) } ?: Timestamp.now(),
                endDate = endDate?.let { Timestamp(it) } ?: Timestamp.now(),
                rewards = rewards
            )
        }
        fun getFirebaseModifiedEvent(
            id : String,
            title: String,
            description: String,
            uri: Uri,
            startDate: Date,
            endDate: Date,
            rewards: List<Reward>
        ) : HashMap<String,Any>{
            val event = hashMapOf(
                "id" to id,
                "title" to title,
                "description" to description,
                "imageUri" to uri.toString(),
                "startDate" to startDate,
                "endDate" to endDate,
                "rewards" to rewards.map { it.toMap() }
            )
            return event
        }
    }
}
