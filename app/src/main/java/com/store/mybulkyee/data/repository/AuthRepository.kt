package com.store.mybulkyee.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount


interface AuthRepository {
    suspend fun signInWithGoogle(account: GoogleSignInAccount?): Boolean
    fun isUserLoggedIn(): Boolean
    suspend fun signOut()
}
