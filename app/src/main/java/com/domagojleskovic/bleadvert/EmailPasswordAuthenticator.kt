package com.domagojleskovic.bleadvert

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class EmailPasswordAuthenticator() {
    private var auth: FirebaseAuth = Firebase.auth

    fun createAccount(email: String, password: String, onSuccess: () -> Unit) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    onSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    // updateUI(null)
                }
            }
        // [END create_user_with_email]
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    onSuccess()
                    // updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    // updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }
    fun signOut() {
        auth.signOut()
    }
    companion object {
        private const val TAG = "EmailPassword"
    }
}