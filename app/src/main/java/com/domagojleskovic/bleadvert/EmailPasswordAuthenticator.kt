package com.domagojleskovic.bleadvert

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import javax.inject.Singleton

class EmailPasswordAuthenticator private constructor() {
    private var auth: FirebaseAuth = Firebase.auth
    private val databaseAccessObject = DatabaseAccessObject.getInstance()

    companion object {
        const val TAG = "EmailPassword"
        @Volatile
        private var instance: EmailPasswordAuthenticator? = null

        @Volatile
        var currentUser: User? = null
            private set

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: EmailPasswordAuthenticator().also { instance = it }
            }

    }

    fun createAccount(email: String, password: String, isAdmin : Boolean = false, onSuccess: () -> Unit, onFailure: () -> Unit) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser?.let { User(id = it.uid, isAdmin = isAdmin, name = it.email!!) }
                    if(user != null){
                        databaseAccessObject.addUser(user){
                            currentUser = user
                            onSuccess()
                        }
                    }
                    else{
                        Log.w(TAG, "Error in adding user to database. User cannot be null")
                        onFailure()
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    onFailure()
                    // updateUI(null)
                }
            }
        // [END create_user_with_email]
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    databaseAccessObject.parseFirebaseUser(auth.currentUser) { parsedUser ->
                        if (parsedUser != null) {
                            currentUser = parsedUser
                            onSuccess()
                        } else {
                            Log.e(TAG, "Failed to parse user after sign-in")
                            onFailure()
                        }
                    }
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    onFailure()
                }
            }
    }
    fun signInAsGuest(onSuccess: () -> Unit, onFailure: () -> Unit){
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInAnonymously:success")
                    currentUser = User(auth.currentUser?.uid ?: "null", "guest", false)
                    onSuccess()
                } else {
                    Log.w(TAG, "signInAnonymously:failure", task.exception)
                    onFailure()
                }
            }
    }
    fun signOut() {
        auth.signOut()
        currentUser = null
    }
}