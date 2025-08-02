package com.masum.todo.data.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthService(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient
    
    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("624447237215-e1muuisscc1kpi7jj2mjsn44m87pkoeg.apps.googleusercontent.com")
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }
    
    fun getCurrentUser() = auth.currentUser
    
    fun isUserLoggedIn() = auth.currentUser != null
    
    fun getGoogleSignInClient() = googleSignInClient
    
    suspend fun signInWithGoogle(idToken: String): AuthResult {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(credential).await()
    }
    
    suspend fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().await()
    }
    
    suspend fun deleteAccount() {
        auth.currentUser?.delete()?.await()
        googleSignInClient.revokeAccess().await()
    }
    
    fun getUserId(): String? = auth.currentUser?.uid
    
    fun getUserEmail(): String? = auth.currentUser?.email
    
    fun getUserDisplayName(): String? = auth.currentUser?.displayName
}
