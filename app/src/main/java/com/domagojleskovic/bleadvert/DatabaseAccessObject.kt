package com.domagojleskovic.bleadvert

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DatabaseAccessObject private constructor() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        const val TAG = "DatabaseAccessObject"

        @Volatile
        private var instance: DatabaseAccessObject? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: DatabaseAccessObject().also { instance = it }
            }
    }

    private fun getStorageReference(path : String) : StorageReference {
        return FirebaseStorage.getInstance().reference.
        child(path)
    }

    suspend fun addEvent(
        context: Context,
        title: String,
        description: String,
        imageUri: String,
        startDate : String,
        endDate : String,
        rewards: List<Reward>,
    ) {
        var eventImageUri = Uri.EMPTY
        // Upload main event image
        if(imageUri.isNotEmpty()){
            val imageRef = getStorageReference("images/${UUID.randomUUID()}/${imageUri.toUri().lastPathSegment}")
            eventImageUri = uploadImage(imageUri, imageRef)
        }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val startDateConverted = dateFormat.parse(startDate)
        val endDateConverted = dateFormat.parse(endDate)

        val db = FirebaseFirestore.getInstance()
        for (reward in rewards) {
            val rewardImageUri = uploadImage(
                reward.image.toString(),
                getStorageReference("images/${UUID.randomUUID()}/${reward.image.lastPathSegment}")
            )
            reward.image = rewardImageUri
        }

        val activeEventsDocRef = db.collection("events").document("active_events")
        activeEventsDocRef.set(hashMapOf("dummy" to true))

        val event = Event.getFirebaseModifiedEvent(
            id = "",
            title = title,
            description = description,
            uri = eventImageUri,
            startDate = startDateConverted!!,
            endDate = endDateConverted!!,
            rewards = rewards
        )

        activeEventsDocRef.collection("events").add(event)
            .addOnSuccessListener { documentReference ->
                val eventId = documentReference.id
                documentReference.update("id", eventId)
                    .addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Event added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        activeEventsDocRef.collection("events").get()
                            .addOnSuccessListener {
                                activeEventsDocRef.update(mapOf("dummy" to FieldValue.delete()))
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            context,
                            "Failed to create document: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Failed to add event: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private suspend fun uploadImage(imageUri: String, storageReference: StorageReference): Uri {
        return suspendCoroutine { continuation ->
            storageReference.putFile(imageUri.toUri())
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        continuation.resume(uri)
                    }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    suspend fun processActiveEvents() : Event? {
        val db = FirebaseFirestore.getInstance()

        val activeEventsRef = db.collection("events")
            .document("active_events")
            .collection("events")

        db.collection("events")
            .document("expired_events")
            .set(mapOf("dummy" to "dummy")).await()

        val expiredEventsRef = db.collection("events")
            .document("expired_events")
            .collection("events")

        try {
            val snapshot = activeEventsRef.get().await()
            val currentDate = Timestamp.now()
            var activeEvent: Event? = null

            for (document in snapshot.documents) {
                val event =  Event.parseFrom(document)
                if (currentDate > event.endDate) {
                    val eventData = Event.getFirebaseModifiedEvent(
                        id = event.id,
                        title = event.title,
                        description = event.description,
                        uri = event.uri,
                        startDate = event.startDate.toDate(),
                        endDate = event.endDate.toDate(),
                        rewards = event.rewards
                    )
                    expiredEventsRef.document(event.id).set(eventData).await()
                    activeEventsRef.document(event.id).delete().await()
                    println("Moving event ${event.title} to expired_events")
                } else if (currentDate in event.startDate..event.endDate) {
                    activeEvent = event
                }
            }

            // Remove dummy field from expired_events document after processing
            db.collection("events")
                .document("expired_events")
                .update(mapOf("dummy" to FieldValue.delete()))

            activeEvent?.let {
                println("Current Active Event: ${it.title}")
            }
            return activeEvent

        } catch (e: Exception) {
            e.printStackTrace()
            return null
            // Handle the error appropriately
        }
    }

    fun fetchActiveEvents(onSuccess: (List<Event>) -> Unit) {
        db.collection("events").document("active_events").collection("events").get()
            .addOnSuccessListener { result ->
                if(result.isEmpty){
                    Log.i("DatabaseAccessObject", "No active events found")
                    onSuccess(emptyList())
                    return@addOnSuccessListener
                }
                val events = result.map { document ->
                    Event.parseFrom(document)
                }
                onSuccess(events)
            }
            .addOnFailureListener { exception ->
                Log.e("DatabaseAccessObject", "Error fetching active events", exception)
                onSuccess(emptyList())
            }
    }
    // fun fetchActiveEvent()
    // fun moveEventToExpiredCollection()
    fun addUser(user: User, onSuccess: () -> Unit) {

        val userData = hashMapOf(
            "id" to user.id,
            "admin" to user.isAdmin,
            "name" to user.name
        )
        // Add dummy field so that the document isn't virtual
        db.collection("users").document(user.id).set(mapOf("dummy" to "dummy"))
        db.collection("users")
            .document(user.id)
            .collection("info")
            .add(userData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document", exception)
            }
        val updates = hashMapOf<String, Any>("dummy" to FieldValue.delete())
        // Remove the dummy field cause there is literally no need to have it but Firebase is stupid
        // Else it won't work
        db.collection("users").document(user.id).update(updates)

    }


    fun parseFirebaseUser(user: FirebaseUser?, callback: (User?) -> Unit) {
        if (user != null) {
            Log.w(TAG, user.uid)
            db.collection("users")
                .document(user.uid)
                .collection("info")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val parsedUser = document.toObject(User::class.java)
                        callback(parsedUser)
                    } else {
                        Log.w(TAG, "No such document")
                        callback(null)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error fetching user", exception)
                    callback(null)
                }
        } else {
            Log.w("DatabaseAccessObject", "User is null")
            callback(null)
        }
    }

    fun fetchBeacons(onSuccess: (List<Beacon>?) -> Unit) {
        db.collection("beacons").get()
            .addOnSuccessListener { result ->
                val beacons = result.map { document ->
                    document.toObject(Beacon::class.java).copy(id = document.id)
                }
                onSuccess(beacons)
            }
            .addOnFailureListener { exception ->
                Log.e("DatabaseAccessObject", "Error fetching beacons", exception)
                onSuccess(null)
            }
    }

    fun addBeacon(context: Context, beacon: Beacon, onSuccess: (Beacon) -> Unit) {
        val beaconData = hashMapOf(
            "id" to "",
            "address" to beacon.address,
            "url" to beacon.url,
            "x" to beacon.x,
            "y" to beacon.y,
            "maximumAdvertisementDistance" to beacon.maximumAdvertisementDistance
        )
        db.collection("beacons")
            .add(beaconData)
            .addOnSuccessListener { documentReference ->
                val documentId = documentReference.id
                Log.d(TAG, "DocumentSnapshot added with ID: $documentId")
                documentReference.update("id", documentId)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully updated with ID field")
                        Toast.makeText(context, "Successfully added beacon", Toast.LENGTH_SHORT).show()
                        val beaconWithID = beacon.copy(id = documentId)
                        onSuccess(beaconWithID)
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error updating document with ID field", exception)
                        Toast.makeText(context, "Error updating beacon ID", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document", exception)
                Toast.makeText(context, "Error adding beacon", Toast.LENGTH_SHORT).show()
            }
    }

    fun updateBeacon(context: Context, oldBeacon: Beacon, newBeacon: Beacon, onSuccess: (List<Beacon>?) -> Unit) {
        val beaconData = hashMapOf(
            "id" to oldBeacon.id,
            "address" to newBeacon.address,
            "url" to newBeacon.url,
            "x" to newBeacon.x,
            "y" to newBeacon.y,
            "maximumAdvertisementDistance" to newBeacon.maximumAdvertisementDistance
        )
        db.collection("beacons").document(oldBeacon.id)
            .set(beaconData)
            .addOnSuccessListener {
                fetchBeacons{ beaconsOrNull ->
                    Toast.makeText(context,"Successfully updated beacon", Toast.LENGTH_SHORT).show()
                    onSuccess(beaconsOrNull)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DatabaseAccessObject", "Error updating beacon", exception)
                Toast.makeText(context,"Error updating beacon", Toast.LENGTH_SHORT).show()
            }
    }

    /*
    private fun addRewardToUserWithID(
        context: Context,
        userID: String,
        reward: Reward,
        showToast: Boolean = true,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val rewardRef = db.collection("users").document(userID).collection("rewards").document()

        rewardRef.set(reward.copy(rewardId = rewardRef.id))
            .addOnSuccessListener {
                if(showToast){
                    Toast.makeText(context,"Successfully added reward to user", Toast.LENGTH_SHORT).show()
                }
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }*/

    /* Old way of adding reward to user
    fun addRewardToUser(
        context: Context,
        name: String,
        reward: Reward,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("users")
            .get()
            .addOnSuccessListener { usersSnapshot ->
                var userFound = false
                var completedQueries = 0

                usersSnapshot.documents.forEach { userDoc ->
                    userDoc.reference.collection("info").whereEqualTo("name", name)
                        .get()
                        .addOnSuccessListener { infoSnapshot ->
                            completedQueries++
                            if (!infoSnapshot.isEmpty && !userFound) {
                                val user = infoSnapshot.documents[0].toObject(User::class.java)
                                if (user != null) {
                                    userFound = true
                                    addRewardToUserWithID(context,user.id, reward, onSuccess = onSuccess, onFailure = onFailure)
                                    Log.i("DatabaseAccessObject", "User found: ${user.name}")
                                }
                            }
                            if (completedQueries == usersSnapshot.documents.size && !userFound) {
                                Log.i("DatabaseAccessObject", "No user found")
                                Toast.makeText(context,"User not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context,"Failed adding reward to user", Toast.LENGTH_SHORT).show()
                            onFailure(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context,"Failed adding reward to user", Toast.LENGTH_SHORT).show()
                onFailure(exception)
            }
    }
    */


    /* Old way of giving reward to users with random selection
    fun giveRewardToRandomUser(context : Context, reward: Reward, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { usersSnapshot ->
                val nonAdminUsers = mutableListOf<User>()
                val userFetchTasks = usersSnapshot.documents.map { userDoc ->
                    userDoc.reference.collection("info")
                        .get()
                        .addOnSuccessListener { infoSnapshot ->
                            for (document in infoSnapshot.documents) {val user = document.toObject(User::class.java)
                                Log.i(TAG, "User: ${user?.name} ${user?.isAdmin}")
                                if (user != null && !user.isAdmin) {
                                    nonAdminUsers.add(user)
                                }
                            }
                        }
                        .addOnFailureListener {
                            Log.e(TAG, "Error fetching user info", it)
                            Toast.makeText(context, "Error fetching user info", Toast.LENGTH_SHORT).show()
                        }
                }
                Tasks.whenAllComplete(userFetchTasks).addOnSuccessListener {
                    if (nonAdminUsers.isNotEmpty()) {
                        val randomUser = nonAdminUsers.random()
                        Log.i("DatabaseAccessObject", "Random user: ${randomUser.name}")
                        addRewardToUserWithID(
                            context,randomUser.id,
                            reward,
                            showToast = false,
                            onSuccess = onSuccess,
                            onFailure = onFailure
                        )
                        Toast.makeText(context, "Random reward given to ${randomUser.name}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No non-admin users found", Toast.LENGTH_SHORT).show()
                        Log.w("DatabaseAccessObject", "No non-admin users found")
                    }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    */


    fun fetchRewards(user : User, onSuccess: (List<Reward>) -> Unit, onFailure: (Exception) -> Unit){
        db.collection("users").document(user.id).collection("rewards")
            .get()
            .addOnSuccessListener { result ->
                val rewards = result.map { document ->
                    document.toObject(Reward::class.java)
                }
                onSuccess(rewards)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun addAdminPrivilegeTo(context: Context, name: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        modifyAdminPrivilege(context, name, true, onSuccess, onFailure)
    }

    fun revokeAdminPrivilegeFrom(context: Context, name: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        modifyAdminPrivilege(context, name, false, onSuccess, onFailure)
    }

    private fun modifyAdminPrivilege(
        context: Context,
        name: String,
        makeAdmin: Boolean, // True for adding, false for revoking
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("users")
            .get()
            .addOnSuccessListener { usersSnapshot ->
                var userFound = false
                var queriesCompleted = 0

                usersSnapshot.documents.forEachIndexed { _, userDoc ->
                    userDoc.reference.collection("info")
                        .whereEqualTo("name", name)
                        .whereEqualTo("admin", !makeAdmin)
                        .get()
                        .addOnSuccessListener { infoSnapshot ->
                            queriesCompleted++
                            if (!infoSnapshot.isEmpty && !userFound) {
                                val infoDoc = infoSnapshot.documents[0]
                                userFound = true
                                infoDoc.reference.update("admin", makeAdmin)
                                    .addOnSuccessListener {
                                        val action = if (makeAdmin) "gave admin to" else "revoked admin from"
                                        Toast.makeText(context, "Successfully $action $name", Toast.LENGTH_SHORT).show()
                                        onSuccess()
                                    }
                                    .addOnFailureListener(onFailure)
                            }
                            if (queriesCompleted == usersSnapshot.documents.size && !userFound) {
                                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener(onFailure)
                }
            }
            .addOnFailureListener(onFailure)
    }
}