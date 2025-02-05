package com.example.mybulkyee.domain.repository

import android.util.Log
import com.example.mybulkyee.data.User
import com.example.mybulkyee.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun signInWithGoogle(account: GoogleSignInAccount?): Boolean {
        return try {
            val credential: AuthCredential =
                GoogleAuthProvider.getCredential(account?.idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user ?: return false

            val userRef = firestore.collection("users").document(user.uid)
            val documentSnapshot = userRef.get().await()

            if (!documentSnapshot.exists()) {
                val newUser = User(
                    uid = user.uid,
                    email = account?.email ?: user.email.orEmpty(),
                    name = account?.displayName.orEmpty(),
                    phone = "",
                    address = "",
                    shopName = "",
                    createdAt = System.currentTimeMillis()
                )
                userRef.set(newUser).await()
            }
            true
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "Google Sign-In failed", e)
            false
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}
